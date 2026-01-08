import sqlite3
import requests
import os

# Writable path for Android (Chaquopy)
DB_PATH = os.path.join(os.environ.get("HOME", "."), "currency_engine.db")

def initialize(default_base):
    global base_currency
    base_currency = default_base
    print(f"Python module initialized with base: {base_currency}")

def get_db_connection():
    return sqlite3.connect(DB_PATH)

def init_db():
    with get_db_connection() as conn:
        # Create a single table for everything
        conn.execute("""
            CREATE TABLE IF NOT EXISTS currencies (
                code TEXT PRIMARY KEY,
                name TEXT,
                rate REAL,
                selected INTEGER
            )
        """)
        conn.commit()

def get_db_count():
    """Returns the number of currencies in the DB, or 0 if table doesn't exist."""
    if not os.path.exists(DB_PATH):
        return 0

    try:
        with get_db_connection() as conn:
            # Check if the table 'currencies' exists in the master schema
            query = "SELECT name FROM sqlite_master WHERE type='table' AND name='currencies'"
            table_exists = conn.execute(query).fetchone()

            if not table_exists:
                return 0

            # Now it is safe to count
            count = conn.execute("SELECT COUNT(*) FROM currencies").fetchone()[0]
            return count
    except Exception as e:
        print(f"Error checking db count: {e}")
        return 0

def populate_database():
    init_db()
    names_url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies.json"
    rates_url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/{}.json".format(base_currency)

    try:
        names_data = requests.get(names_url, timeout=10).json()
        rates_data = requests.get(rates_url, timeout=10).json()[base_currency]

        with get_db_connection() as conn:
            for code, name in names_data.items():
                if(name != ""):
                    code_lower = code.lower()
                    rate = rates_data.get(code, -1.0)

                    # Insert only if currency doesn't exist (preserves 'selected')
                    if(code_lower == base_currency):
                        conn.execute(
                            "INSERT OR IGNORE INTO currencies (code, name, rate, selected) VALUES (?, ?, ?, 1)",
                            (code_lower, name, rate)
                        )
                    else:
                        conn.execute(
                            "INSERT OR IGNORE INTO currencies (code, name, rate, selected) VALUES (?, ?, ?, 0)",
                            (code_lower, name, rate)
                        )

                    # Update rate and name for existing currencies (preserves 'selected')
                    conn.execute(
                        "UPDATE currencies SET name = ?, rate = ? WHERE code = ?",
                        (name, rate, code_lower)
                    )

            conn.commit()
        return len(names_data)
    except Exception as e:
        print(f"Error populating DB: {e}")
        return 0


def get_filtered_currencies(query=None):
    """Retrieves the full list (or filtered) from our unified table."""
    with get_db_connection() as conn:
        if query:
            search = f"%{query.lower()}%"
            cursor = conn.execute(
                """
                SELECT code, name, rate, selected 
                FROM currencies 
                WHERE selected = 1 OR LOWER(code) LIKE ? OR LOWER(name) LIKE ?
                ORDER BY  selected DESC, name ASC
                """,
                (search, search)
            )
        else:
            cursor = conn.execute(
                """
                SELECT code, name, rate, selected 
                FROM currencies 
                ORDER BY  selected DESC, name ASC
                """
            )

        return [{"code": r[0], "name": r[1], "rate": r[2], "selected": r[3]} for r in cursor.fetchall()]

def toggle_currency_selection(code, is_selected):
    """Updates the is_selected flag for a specific currency."""
    val = 1 if is_selected else 0
    if(code == base_currency):
        return
    try:
        with  get_db_connection() as conn:
            conn.execute(
                "UPDATE currencies SET selected = ? WHERE code = ?",
                (val, code.lower())
            )
            conn.commit()
    except Exception as e:
        print(f"Error selecting element : {e}")

def get_selected_currencies():
    """Returns only the currencies marked as selected for the main page."""
    with get_db_connection() as conn:
        cursor = conn.execute(
            "SELECT code, name, rate FROM currencies WHERE selected = 1 OR code LIKE ? ORDER BY rate ASC", (base_currency,)
        )
        return [{"code": r[0], "name": r[1], "rate": r[2]} for r in cursor.fetchall()]