ALTER TABLE assets
ADD COLUMN owner_id BIGINT;

ALTER TABLE assets
ADD CONSTRAINT fk_asset_owner
FOREIGN KEY (owner_id)
REFERENCES users(id);