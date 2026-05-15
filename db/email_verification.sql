CREATE TABLE IF NOT EXISTS email_verification (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    code VARCHAR(64) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    type VARCHAR(30) NOT NULL,
    created_date DATETIME(0),
    modified_date DATETIME(0),
    PRIMARY KEY (id)
);

CREATE INDEX idx_email_verification_lookup
    ON email_verification (email, used, type, created_date);
