CREATE INDEX IF NOT EXISTS idx_sales_latest
ON sales (sales_date DESC NULLS LAST, sales_id DESC);