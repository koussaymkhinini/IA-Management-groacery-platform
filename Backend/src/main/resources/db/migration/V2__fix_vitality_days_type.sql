ALTER TABLE products
ALTER COLUMN vitality_days TYPE NUMERIC
USING vitality_days::NUMERIC;