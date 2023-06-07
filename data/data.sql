CREATE TABLE IF NOT EXISTS CREDITY_ANALYSIS (
    id UUID PRIMARY KEY,
    approved BOOLEAN,
    approved_limit DECIMAL(10, 2),
    withdraw DECIMAL(10, 2),
    monthly_income DECIMAL(10, 2),
    requested_amount DECIMAL(10, 2),
    annual_interest DECIMAL(5, 2),
    client_id UUID NOT NULL,
    date DATE
);
