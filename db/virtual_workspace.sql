CREATE TABLE IF NOT EXISTS virtual_workspace (
    id UUID NOT NULL,
    player_id UUID NOT NULL,
    stage_id UUID NOT NULL,
    stage_image VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    failure_reason VARCHAR(255),
    created_at DATETIME(0),
    updated_at DATETIME(0),
    PRIMARY KEY (id)
);

CREATE INDEX idx_virtual_workspace_stage_lookup
    ON virtual_workspace (player_id, stage_id, status, updated_at);

CREATE INDEX idx_virtual_workspace_image_lookup
    ON virtual_workspace (player_id, stage_image, status, updated_at);
