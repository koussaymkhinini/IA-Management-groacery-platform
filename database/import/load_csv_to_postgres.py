
import os
from pathlib import Path

import psycopg


# Directory containing the seven CSV files and this script.
IMPORT_DIR = Path(__file__).resolve().parent

# Import order respects the foreign-key dependencies.
TABLES = [
    (
        "countries.csv",
        "countries",
        ["country_id", "country_name", "country_code"],
    ),
    (
        "categories.csv",
        "categories",
        ["category_id", "category_name"],
    ),
    (
        "cities.csv",
        "cities",
        ["city_id", "city_name", "zipcode", "country_id"],
    ),
    (
        "employees.csv",
        "employees",
        [
            "employee_id",
            "first_name",
            "middle_initial",
            "last_name",
            "birth_date",
            "gender",
            "city_id",
            "hire_date",
        ],
    ),
    (
        "customers.csv",
        "customers",
        [
            "customer_id",
            "first_name",
            "middle_initial",
            "last_name",
            "city_id",
            "address",
        ],
    ),
    (
        "products.csv",
        "products",
        [
            "product_id",
            "product_name",
            "price",
            "category_id",
            "product_class",
            "modify_date",
            "resistant",
            "is_allergic",
            "vitality_days",
        ],
    ),
    (
        "sales.csv",
        "sales",
        [
            "sales_id",
            "salesperson_id",
            "customer_id",
            "product_id",
            "quantity",
            "discount",
            "total_price",
            "sales_date",
            "transaction_number",
        ],
    ),
]


def get_connection_settings():
    """Read database connection settings from environment variables."""
    password = os.environ.get("DB_PASSWORD")

    if not password:
        raise RuntimeError(
            "DB_PASSWORD is not set. Set it in your PowerShell session first."
        )

    return {
        "host": "localhost",
        "port": 5433,
        "dbname": "grocery_ai",
        "user": "app_user",
        "password": password,
        "connect_timeout": 10,
    }


def import_csv(cur, filename, table, columns):
    """Import one CSV file and return the number of rows in its table."""
    file_path = IMPORT_DIR / filename

    if not file_path.is_file():
        raise FileNotFoundError(f"CSV file not found: {file_path}")

    print(f"\nLoading {filename} into {table}...")

    column_list = ", ".join(columns)

    copy_sql = (
        f"COPY {table} ({column_list}) "
        "FROM STDIN WITH (FORMAT CSV, HEADER TRUE, NULL '')"
    )

    with file_path.open(
        "r",
        encoding="utf-8-sig",
        newline="",
    ) as csv_file:
        with cur.copy(copy_sql) as copy:
            while True:
                chunk = csv_file.read(1024 * 1024)

                if not chunk:
                    break

                copy.write(chunk)

    # Copy.rowcount is not available on psycopg's Copy object.
    # Query PostgreSQL to obtain the actual number of rows.
    cur.execute(f"SELECT COUNT(*) FROM {table}")
    loaded_rows = cur.fetchone()[0]

    print(f"Loaded {loaded_rows:,} rows into {table}.")

    return loaded_rows


def main():
    """Import all seven CSV files in a single database transaction."""
    connection_settings = get_connection_settings()

    print("Connecting to PostgreSQL...")

    total_rows = 0

    # The connection context commits on success and rolls back on error.
    with psycopg.connect(**connection_settings) as conn:
        with conn.cursor() as cur:
            print("Connected successfully.")

            # WARNING: This removes existing rows from all seven tables.
            # Appropriate for the initial import while the tables are empty.
            print("\nClearing existing table data...")

            cur.execute("""
                TRUNCATE TABLE
                    sales,
                    products,
                    customers,
                    employees,
                    cities,
                    categories,
                    countries
            """)

            for filename, table, columns in TABLES:
                loaded_rows = import_csv(
                    cur,
                    filename,
                    table,
                    columns,
                )

                total_rows += loaded_rows

    print("\n" + "=" * 50)
    print("IMPORT COMPLETED SUCCESSFULLY")
    print("=" * 50)
    print(f"Total rows imported: {total_rows:,}")
    print("\nRow counts by table:")

    # Display final counts after the transaction has committed.
    with psycopg.connect(**connection_settings) as conn:
        with conn.cursor() as cur:
            for _, table, _ in TABLES:
                cur.execute(f"SELECT COUNT(*) FROM {table}")
                count = cur.fetchone()[0]
                print(f"  {table}: {count:,}")


if __name__ == "__main__":
    try:
        main()
    except (psycopg.Error, OSError, RuntimeError) as error:
        print(f"\nIMPORT FAILED: {error}")
        print(
            "Check the error above. The transaction should roll back "
            "if the import fails before committing."
        )
        raise
