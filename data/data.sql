CREATE TABLE IF NOT EXISTS CREDITY_ANALYSIS (
    id uuid PRIMARY KEY,
    approved boolean,
    approved_limit DECIMAL(10,2) NOT NULL,
    requested_amount DECIMAL(10, 2) NOT NULL,
    withdraw DECIMAL(10,2) NOT NULL,
    annual_interest DECIMAL(5, 2) NOT NULL,
    client_id uuid NOT NULL,
    date date
);
