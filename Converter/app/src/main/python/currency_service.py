import sqlite3
import requests
import os

# Set a writable path for Android.
# Chaquopy sets HOME to the app's internal files directory.
DB_PATH = os.path.join(os.environ.get("HOME", "."), "currency_engine.db")

def get_db_connection():
    """Shared connection logic to ensure tables exist."""
    conn = sqlite3.connect(DB_PATH)
    conn.execute("CREATE TABLE IF NOT EXISTS rates (code TEXT PRIMARY KEY, rate REAL)")
    conn.execute("CREATE TABLE IF NOT EXISTS labels (code TEXT PRIMARY KEY, name TEXT)")
    conn.commit()
    return conn


def fetch_rate_from_eur(target_currency):
    """Fetches a specific rate (e.g. 'pln') and caches it."""
    target = target_currency.lower()
    url = f"https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/eur.json"

    try:
        # 1. Try Network
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        data = response.json()
        rate = float(data["eur"][target])

        # 2. Update Cache
        with get_db_connection() as conn:
            conn.execute("INSERT OR REPLACE INTO rates (code, rate) VALUES (?, ?)", (target, rate))
        return rate
    except Exception:
        # 3. Fallback to Cache
        try:
            with get_db_connection() as conn:
                cursor = conn.execute("SELECT rate FROM rates WHERE code = ?", (target,))
                row = cursor.fetchone()
                return row[0] if row else -1.0
        except:
            return -1.0

def fetch_rates():
    """
    Fetches all currency names/codes and stores them in 'labels'.
    Returns a count of currencies synced for your UI to display.
    """
    url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies.json"
    try:
        response = requests.get(url, timeout=10)
        response.raise_for_status()
        labels_dict = response.json() # Structure: {"usd": "United States Dollar", ...}

        # Convert dict to list of tuples for high-performance bulk insert
        data_to_insert = list(labels_dict.items())

        with get_db_connection() as conn:
            conn.executemany("INSERT OR REPLACE INTO labels (code, name) VALUES (?, ?)", data_to_insert)
            conn.commit()
        return len(data_to_insert)
    except Exception as e:
        print(f"Error syncing names: {e}")
        return 0

def get_currency_name(code):
    """Helper to get a full name (e.g., 'pln' -> 'Polish Zloty') from DB."""
    try:
        with get_db_connection() as conn:
            cursor = conn.execute("SELECT name FROM labels WHERE code = ?", (code.lower(),))
            row = cursor.fetchone()
            return row[0] if row else "Unknown Currency"
    except:
        return "Unknown Currency"

def get_currencies(filter_query=None):
    """
    Returns filtered currencies.
    If filter_query is 'us', it matches 'USD' and 'United States Dollar'.
    """

    with get_db_connection() as conn:
        if filter_query:
            # Case-insensitive search using LIKE
            # We wrap the query in % for 'contains' logic
            search_term = f"%{filter_query.lower()}%"
            query = "SELECT code, name FROM labels WHERE code LIKE ? OR name LIKE ? ORDER BY name ASC"
            cursor = conn.execute(query, (search_term, search_term))
        else:
            query = "SELECT code, name FROM labels ORDER BY name ASC"
            cursor = conn.execute(query)

        return [{"code": row[0], "name": row[1]} for row in cursor.fetchall()]
    

# print(f"launching : fetch_rates()") # Initial sync on module load
# #print(f"Fetched {fetch_rates()} currencies.")
#
# print(f"Fetching the eur->usd rate: {fetch_rate_from_eur('usd')}")
# print(f"Fetching the eur->pln rate: {fetch_rate_from_eur('pln')}")
#
# query = "eur"
# print(f"Getting currencies containing '{query}': {get_currencies(query)}")