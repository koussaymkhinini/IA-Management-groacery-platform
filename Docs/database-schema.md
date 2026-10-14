# Database Schema

## 1. Overview

The project uses a relational dataset consisting of seven interconnected tables:

- `countries`
- `cities`
- `customers`
- `employees`
- `categories`
- `products`
- `sales`

The intended database management system is PostgreSQL.

## 2. Relationships

- `cities.CountryID` references `countries.CountryID`.
- `customers.CityID` references `cities.CityID`.
- `employees.CityID` references `cities.CityID`.
- `products.CategoryID` references `categories.CategoryID`.
- `sales.SalesPersonID` references `employees.EmployeeID`.
- `sales.CustomerID` references `customers.CustomerID`.
- `sales.ProductID` references `products.ProductID`.

These relationships are based on the supplied dataset documentation and must be verified against the original schema and CSV files before implementation.

## 3. Intended Analytical Capabilities

The schema may support the following analyses:

- Revenue and sales trends over time.
- Best-selling products and product categories.
- Sales by employee.
- Customer purchasing activity.
- Geographic sales analysis by city and country.
- Transaction counts and average transaction value.

The exact calculations will be defined after validating the meanings of `TotalPrice`, `Discount`, and `TransactionNumber`.

## 4. Items to Validate

- Meaning of `cities.Zipcode`.
- Precision and scale of `products.Price`.
- Data type and allowed values of `products.IsAllergic`.
- Meaning of `products.VitalityDays`.
- Whether `sales.TotalPrice` is the final amount for a sale line.
- Whether `sales.Discount` is a monetary amount or a percentage.
- Whether multiple sales rows can share one `TransactionNumber`.
- Actual column names, nullability, key constraints, and data types.

## 5. Implementation Principle

The PostgreSQL schema must be based on the verified source data. Any necessary type conversions, cleaning rules, or data transformations must be documented and reproducible.