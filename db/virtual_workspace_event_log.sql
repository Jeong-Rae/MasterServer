CREATE TABLE IF NOT EXISTS virtual_workspace_event_log (
    id CHAR(36) NOT NULL,
    workspace_id CHAR(36) NOT NULL,
    player_id CHAR(36) NOT NULL,
    stage_id BIGINT NOT NULL,
    stage_image VARCHAR(30) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    result VARCHAR(20) NOT NULL,
    occurred_at DATETIME(0) NOT NULL,
    idempotency_key VARCHAR(180) NOT NULL,
    failure_reason VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_virtual_workspace_event_workspace
        FOREIGN KEY (workspace_id) REFERENCES virtual_workspace (id),
    CONSTRAINT uk_virtual_workspace_event_idempotency UNIQUE (idempotency_key)
);

-- Deletion event types are split by initiator:
-- MANUAL_DELETION_STARTED, MANUAL_DELETION_COMPLETED, MANUAL_DELETION_FAILED
-- SYSTEM_RECLAIM_STARTED, SYSTEM_RECLAIM_COMPLETED, SYSTEM_RECLAIM_FAILED

CREATE INDEX idx_virtual_workspace_event_workspace
    ON virtual_workspace_event_log (workspace_id, occurred_at);

CREATE INDEX idx_virtual_workspace_event_lookup
    ON virtual_workspace_event_log (player_id, stage_id, occurred_at);
