CREATE TABLE IF NOT EXISTS player (
    player_id UUID NOT NULL,
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255),
    oauth_type VARCHAR(30) DEFAULT 'POL',
    oauth_id VARCHAR(255),
    locked BOOLEAN DEFAULT FALSE,
    oauth_access_token VARCHAR(255),
    refresh_token VARCHAR(255),
    exp INT DEFAULT 0,
    created_at DATETIME(0),
    updated_at DATETIME(0),
    PRIMARY KEY (player_id),
    CONSTRAINT uk_player_name UNIQUE (name),
    CONSTRAINT uk_player_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS player_authority (
    player_id UUID NOT NULL,
    authority VARCHAR(80) NOT NULL,
    CONSTRAINT uk_player_authority UNIQUE (player_id, authority),
    CONSTRAINT fk_player_authority_player
        FOREIGN KEY (player_id) REFERENCES player (player_id)
);
