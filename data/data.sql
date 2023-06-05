CREATE TABLE IF NOT EXISTS CREDITY_ANALYSIS (
    id UUID PRIMARY KEY,
    approved BOOLEAN,
    approved_limit DOUBLE PRECISION,
    withdraw DOUBLE PRECISION,
    monthly_income DOUBLE PRECISION,
    requested_amount DOUBLE PRECISION,
    annual_interest DOUBLE PRECISION,
    client_id UUID NOT NULL,
    date TIMESTAMP
);
