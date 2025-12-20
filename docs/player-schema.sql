-- Player BC schema migration guide (manual apply).

-- 1) Extend legacy player table with UUID-based PlayerId.
ALTER TABLE player
  ADD COLUMN player_uuid CHAR(36) NULL;

-- Backfill existing rows if needed.
-- NOTE: execute once on existing data set.
UPDATE player SET player_uuid = UUID() WHERE player_uuid IS NULL;

ALTER TABLE player
  MODIFY player_uuid CHAR(36) NOT NULL UNIQUE;

-- 2) Profile aggregate.
CREATE TABLE player_profile (
  player_uuid CHAR(36) NOT NULL,
  nickname VARCHAR(50) NOT NULL,
  created_date DATETIME(0),
  modified_date DATETIME(0),
  PRIMARY KEY (player_uuid),
  UNIQUE KEY idx_player_profile_nickname (nickname)
);

-- 3) Rating aggregate.
CREATE TABLE player_rating (
  player_uuid CHAR(36) NOT NULL,
  point BIGINT NOT NULL DEFAULT 0,
  created_date DATETIME(0),
  modified_date DATETIME(0),
  PRIMARY KEY (player_uuid)
);

-- 4) Authority aggregate.
CREATE TABLE player_authority (
  player_uuid CHAR(36) NOT NULL,
  created_date DATETIME(0),
  modified_date DATETIME(0),
  PRIMARY KEY (player_uuid)
);

CREATE TABLE player_authority_role (
  player_uuid CHAR(36) NOT NULL,
  role VARCHAR(64) NOT NULL,
  PRIMARY KEY (player_uuid, role)
);

CREATE TABLE player_authority_permission (
  player_uuid CHAR(36) NOT NULL,
  permission VARCHAR(64) NOT NULL,
  PRIMARY KEY (player_uuid, permission)
);

-- TODO: existing player records should be migrated into profile/rating/authority tables.
