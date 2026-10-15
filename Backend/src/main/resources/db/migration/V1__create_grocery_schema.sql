
CREATE TABLE countries (
    country_id   INTEGER PRIMARY KEY,
    country_name VARCHAR(100) NOT NULL,
    country_code VARCHAR(10) NOT NULL
);

CREATE TABLE categories (
    category_id   INTEGER PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL
);

CREATE TABLE cities (
    city_id    INTEGER PRIMARY KEY,
    city_name  VARCHAR(150) NOT NULL,
    zipcode    VARCHAR(20),
    country_id INTEGER NOT NULL,
    CONSTRAINT fk_cities_country
        FOREIGN KEY (country_id) REFERENCES countries(country_id)
);

CREATE TABLE employees (
    employee_id    INTEGER PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL,
    middle_initial VARCHAR(10),
    last_name      VARCHAR(100) NOT NULL,
    birth_date     TIMESTAMP,
    gender         VARCHAR(20),
    city_id        INTEGER NOT NULL,
    hire_date      TIMESTAMP,
    CONSTRAINT fk_employees_city
        FOREIGN KEY (city_id) REFERENCES cities(city_id)
);

CREATE TABLE customers (
    customer_id    INTEGER PRIMARY KEY,
    first_name     VARCHAR(100) NOT NULL,
    middle_initial VARCHAR(10),
    last_name      VARCHAR(100) NOT NULL,
    city_id        INTEGER NOT NULL,
    address        VARCHAR(255),
    CONSTRAINT fk_customers_city
        FOREIGN KEY (city_id) REFERENCES cities(city_id)
);

CREATE TABLE products (
    product_id    INTEGER PRIMARY KEY,
    product_name  VARCHAR(255) NOT NULL,
    price         NUMERIC(6,4),
    category_id   INTEGER NOT NULL,
    product_class VARCHAR(50),
    modify_date   TIMESTAMP,
    resistant     VARCHAR(50),
    is_allergic   VARCHAR(20),
    vitality_days INTEGER,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE sales (
    sales_id           BIGINT PRIMARY KEY,
    salesperson_id     INTEGER NOT NULL,
    customer_id        INTEGER NOT NULL,
    product_id         INTEGER NOT NULL,
    quantity            INTEGER NOT NULL,
    discount            NUMERIC(3,2),
    total_price         NUMERIC(14,4),
    sales_date          TIMESTAMP,
    transaction_number  VARCHAR(50),
    CONSTRAINT fk_sales_employee
        FOREIGN KEY (salesperson_id) REFERENCES employees(employee_id),
    CONSTRAINT fk_sales_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_sales_product
        FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE INDEX idx_sales_salesperson_id ON sales(salesperson_id);
CREATE INDEX idx_sales_customer_id ON sales(customer_id);
CREATE INDEX idx_sales_product_id ON sales(product_id);
CREATE INDEX idx_sales_sales_date ON sales(sales_date);
CREATE INDEX idx_customers_city_id ON customers(city_id);
CREATE INDEX idx_employees_city_id ON employees(city_id);
CREATE INDEX idx_products_category_id ON products(category_id);
