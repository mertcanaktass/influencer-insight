ALTER TABLE users ADD COLUMN IF NOT EXISTS social_connection_consent_version VARCHAR(64);
ALTER TABLE users ADD COLUMN IF NOT EXISTS social_connection_consent_at TIMESTAMP WITH TIME ZONE;
