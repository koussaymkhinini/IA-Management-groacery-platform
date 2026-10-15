
import csv
from collections import Counter
from decimal import Decimal, InvalidOperation
from pathlib import Path

DATA_DIR = Path("database/import")

for filename in sorted(DATA_DIR.glob("*.csv")):
    row_count = 0
    empty_values = Counter()
    numeric_stats = {}
    value_counts = {
        "Discount": Counter(),
        "TotalPrice": Counter(),
        "Price": Counter(),
    }

    print(f"\n{'=' * 50}")
    print(f"File: {filename.name}")

    with filename.open("r", encoding="utf-8-sig", newline="") as file:
        reader = csv.DictReader(file)
        print("Columns:", reader.fieldnames)

        for row in reader:
            row_count += 1

            for column, value in row.items():
                if value is None or value.strip() == "":
                    empty_values[column] += 1

            for column in value_counts:
                raw_value = row.get(column)

                if raw_value is None or raw_value.strip() == "":
                    continue

                if column not in reader.fieldnames:
                    continue

                value_counts[column][raw_value.strip()] += 1

                try:
                    number = Decimal(raw_value.strip())
                except InvalidOperation:
                    continue

                if column not in numeric_stats:
                    numeric_stats[column] = {
                        "min": number,
                        "max": number,
                        "max_decimal_places": 0,
                    }

                stats = numeric_stats[column]
                stats["min"] = min(stats["min"], number)
                stats["max"] = max(stats["max"], number)
                decimal_places = max(0, -number.as_tuple().exponent)
                stats["max_decimal_places"] = max(
                    stats["max_decimal_places"], decimal_places
                )

    print("Rows:", row_count)
    print("Empty values:", dict(empty_values) or "None")

    for column, stats in numeric_stats.items():
        print(f"{column} statistics: {stats}")

    for column, counts in value_counts.items():
        if counts:
            print(f"{column} most common values: {counts.most_common(10)}")
