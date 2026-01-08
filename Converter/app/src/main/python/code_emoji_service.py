def get_emoji_from_code(code):
    match code:
        case "uah":
           return "\uD83C\uDDEA\uD83C\uDDFA"
        case _:
             return "!"

def get_flag_dynamic(currency_code):
    # Take the first two letters (e.g., "USD" -> "US")
    country_code = currency_code[:2].upper()

    # Offset to convert "A" to Regional Indicator Symbol "A"
    OFFSET = 127397
    return chr(ord(country_code[0]) + OFFSET) + chr(ord(country_code[1]) + OFFSET)
