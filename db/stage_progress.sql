-- Stage Catalog BC + Solving Progress BC schema draft.
-- Target: MySQL 8.1 / InnoDB / utf8mb4.
--
-- Design notes:
-- - IDs are UUIDv7 values stored as BINARY(16).
-- - Question identity is the natural composite key (stage_id, question_index).
-- - Answer uses one table for the common 1:1 answer row.
-- - Repeating or type-specific values are split into child tables.
-- - player_id belongs to the Player BC. This file stores it as BINARY(16)
--   but intentionally does not define a FK to the player table.
-- - Denormalized fields are allowed where they support natural query paths:
--   stage.question_count, player_stage_progress.current_question_index,
--   player_stage_progress.completed_question_count, question_attempt.answer_type,
--   question_attempt.is_correct.
-- - Attempt payloads are kept in one table. Type-specific nullable columns are
--   validated by the domain/application layer.

CREATE TABLE IF NOT EXISTS stage (
    id BINARY(16) NOT NULL,
    title VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    stage_group VARCHAR(40) NOT NULL,
    difficulty_level VARCHAR(20) NOT NULL,
    stage_image VARCHAR(40) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    question_count INT NOT NULL DEFAULT 0,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_stage_status
        CHECK (status IN ('DRAFT', 'APPROVED', 'ARCHIVED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS question (
    stage_id BINARY(16) NOT NULL,
    question_index INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (stage_id, question_index),
    CONSTRAINT fk_question_stage
        FOREIGN KEY (stage_id) REFERENCES stage (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS answer (
    stage_id BINARY(16) NOT NULL,
    question_index INT NOT NULL,
    answer_type VARCHAR(30) NOT NULL,
    match_policy VARCHAR(30),
    requires_compose BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (stage_id, question_index),
    CONSTRAINT fk_answer_question
        FOREIGN KEY (stage_id, question_index)
        REFERENCES question (stage_id, question_index),
    CONSTRAINT ck_answer_type
        CHECK (answer_type IN ('MULTIPLE_CHOICE', 'SHORT_ANSWER', 'PRACTICAL', 'DESCRIPTION')),
    CONSTRAINT ck_answer_match_policy
        CHECK (
            match_policy IS NULL
            OR match_policy IN ('EXACT', 'CASE_INSENSITIVE', 'NORMALIZED')
        )
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS answer_option (
    stage_id BINARY(16) NOT NULL,
    question_index INT NOT NULL,
    option_index INT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    correct_option_key TINYINT
        GENERATED ALWAYS AS (CASE WHEN is_correct THEN 1 ELSE NULL END) STORED,
    PRIMARY KEY (stage_id, question_index, option_index),
    CONSTRAINT fk_answer_option_answer
        FOREIGN KEY (stage_id, question_index)
        REFERENCES answer (stage_id, question_index),
    CONSTRAINT uk_answer_option_single_correct
        UNIQUE (stage_id, question_index, correct_option_key)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS short_answer_value (
    stage_id BINARY(16) NOT NULL,
    question_index INT NOT NULL,
    answer_index INT NOT NULL,
    answer_text VARCHAR(255) NOT NULL,
    PRIMARY KEY (stage_id, question_index, answer_index),
    CONSTRAINT fk_short_answer_value_answer
        FOREIGN KEY (stage_id, question_index)
        REFERENCES answer (stage_id, question_index),
    CONSTRAINT uk_short_answer_value_text
        UNIQUE (stage_id, question_index, answer_text)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS player_stage_progress (
    player_id BINARY(16) NOT NULL,
    stage_id BINARY(16) NOT NULL,
    status VARCHAR(30) NOT NULL,
    current_question_index INT,
    completed_question_count INT NOT NULL DEFAULT 0,
    started_at DATETIME(6),
    last_solved_at DATETIME(6),
    completed_at DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (player_id, stage_id),
    CONSTRAINT fk_progress_stage
        FOREIGN KEY (stage_id) REFERENCES stage (id),
    CONSTRAINT fk_progress_current_question
        FOREIGN KEY (stage_id, current_question_index)
        REFERENCES question (stage_id, question_index),
    CONSTRAINT ck_progress_status
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'FAILED'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS question_attempt (
    id BINARY(16) NOT NULL,
    player_id BINARY(16) NOT NULL,
    stage_id BINARY(16) NOT NULL,
    question_index INT NOT NULL,
    answer_type VARCHAR(30) NOT NULL,
    selected_option_index INT,
    submitted_text VARCHAR(255),
    external_execution_id VARCHAR(100),
    result_code VARCHAR(60),
    failure_reason VARCHAR(255),
    is_correct BOOLEAN NOT NULL,
    attempted_at DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_question_attempt_question
        UNIQUE (id, stage_id, question_index),
    CONSTRAINT fk_attempt_progress
        FOREIGN KEY (player_id, stage_id)
        REFERENCES player_stage_progress (player_id, stage_id),
    CONSTRAINT fk_attempt_question
        FOREIGN KEY (stage_id, question_index)
        REFERENCES question (stage_id, question_index),
    CONSTRAINT fk_attempt_selected_option
        FOREIGN KEY (stage_id, question_index, selected_option_index)
        REFERENCES answer_option (stage_id, question_index, option_index),
    CONSTRAINT ck_attempt_answer_type
        CHECK (answer_type IN ('MULTIPLE_CHOICE', 'SHORT_ANSWER', 'PRACTICAL', 'DESCRIPTION'))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE INDEX idx_stage_group_status
    ON stage (stage_group, status);

CREATE INDEX idx_question_stage_created
    ON question (stage_id, question_index, created_at);

CREATE INDEX idx_answer_stage_type
    ON answer (stage_id, answer_type);

CREATE INDEX idx_progress_player_status
    ON player_stage_progress (player_id, status);

CREATE INDEX idx_progress_stage_completed
    ON player_stage_progress (stage_id, completed_at);

CREATE INDEX idx_attempt_player_stage_question
    ON question_attempt (player_id, stage_id, question_index, attempted_at);

CREATE INDEX idx_attempt_stage_answer_type
    ON question_attempt (stage_id, answer_type, attempted_at);
