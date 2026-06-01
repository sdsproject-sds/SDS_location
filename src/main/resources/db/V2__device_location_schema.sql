create TABLE IF NOT EXISTS device_locations
(
    device_id  TEXT      PRIMARY KEY       NOT NULL,
    user_id    VARCHAR(200)           NOT NULL,

    lat        NUMERIC  NOT NULL,
    lon        NUMERIC  NOT NULL,
    location   GEOGRAPHY(POINT, 4326) NOT NULL,
    notification_token VARCHAR(500) NULL,
    device_type VARCHAR(50) NULL,
    accuracy NUMERIC NULL,
    status VARCHAR(30)  NULL,
    metadata jsonb NULL,
    supported_services TEXT[] NULL,

    created_at TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ      NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_user_device UNIQUE (device_id, user_id)
);