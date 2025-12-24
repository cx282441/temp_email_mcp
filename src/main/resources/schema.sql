CREATE TABLE IF NOT EXISTS email_records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    email_address TEXT NOT NULL UNIQUE,
    jwt TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_id ON email_records(user_id);
CREATE INDEX IF NOT EXISTS idx_email_address ON email_records(email_address);