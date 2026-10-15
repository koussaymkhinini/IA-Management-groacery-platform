
import csv
from pathlib import Path
from collections import Counter

DATA_DIR = Path("database/import")

def load_ids(filename, column):
    ids = set()
    duplicates = Counter()

    with (DATA_DIR / filename).open(
        "r", encoding="utf-8-sig", newline=""
    ) as file:
        for row in csv.DictReader(file):
            value = row[column].strip()
            if value in ids:
                duplicates[value] += 1
            ids.add(value)

    print(
        f"{filename}: {len(ids):,} distinct {column} values; "
        f"{sum(duplicates.values()):,} duplicate occurrences"
    )
    return ids

print("Loading reference IDs...")

categories = load_ids("categories.csv", "CategoryID")
cities = load_ids("cities.csv", "CityID")
countries = load_ids("countries.csv", "CountryID")
customers = load_ids("customers.csv", "CustomerID")
employees = load_ids("employees.csv", "EmployeeID")
products = load_ids("products.csv", "ProductID")

checks = {
    "cities.CountryID": ("CountryID", countries),
    "customers.CityID": ("CityID", cities),
    "employees.CityID": ("CityID", cities),
    "products.CategoryID": ("CategoryID", categories),
    "sales.SalesPersonID": ("SalesPersonID", employees),
    "sales.CustomerID": ("CustomerID", customers),
    "sales.ProductID": ("ProductID", products),
}

files = {
    "cities.CountryID": ("cities.csv", "CountryID"),
    "customers.CityID": ("customers.csv", "CityID"),
    "employees.CityID": ("employees.csv", "CityID"),
    "products.CategoryID": ("products.csv", "CategoryID"),
    "sales.SalesPersonID": ("sales.csv", "SalesPersonID"),
    "sales.CustomerID": ("sales.csv", "CustomerID"),
    "sales.ProductID": ("sales.csv", "ProductID"),
}

print("\nChecking foreign-key references...")

for relationship, (filename, column) in files.items():
    referenced_ids = checks[relationship][1]
    missing_count = 0
    examples = []

    with (DATA_DIR / filename).open(
        "r", encoding="utf-8-sig", newline=""
    ) as file:
        for row in csv.DictReader(file):
            value = row[column].strip()

            if value not in referenced_ids:
                missing_count += 1
                if len(examples) < 5:
                    examples.append(value)

    print(
        f"{relationship}: {missing_count:,} invalid references"
        + (f"; examples: {examples}" if examples else "")
    )
