CREATE TABLE IF NOT EXISTS request_log (
    id SERIAL PRIMARY KEY,
    request VARCHAR(255),
    response VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
