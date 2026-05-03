CREATE
EXTENSION postgis;

CREATE TABLE tbl_country
(
    iso2          CHAR(2) PRIMARY KEY,
    iso3          CHAR(3) UNIQUE NOT NULL,
    name          TEXT           NOT NULL,
    currency_code CHAR(3),
    id            BIGSERIAL UNIQUE,
    created_at    TIMESTAMP DEFAULT now(),
    updated_at    TIMESTAMP DEFAULT now()
);
INSERT INTO tbl_country (iso2, iso3, name, currency_code)
VALUES ('KE', 'KEN', 'Kenya', 'KES');


CREATE TABLE tbl_country_divisions
(
    id            BIGSERIAL UNIQUE,
    country_iso2  CHAR(2) NOT NULL,
    division      TEXT    NOT NULL,
    division_code CHAR(3) PRIMARY KEY, -- e.g. NBO
    geom          GEOMETRY(POLYGON, 4326),
    bool supported DEFAULT true,
    created_at    TIMESTAMP DEFAULT now(),
    updated_at    TIMESTAMP DEFAULT now(),

    CONSTRAINT fk_country
        FOREIGN KEY (country_iso2)
            REFERENCES tbl_country (iso2)
);

CREATE TABLE country_sub_divisions
(
    id                   BIGSERIAL PRIMARY KEY,
    division_code        TEXT NOT NULL,
    country_sub_division TEXT NOT NULL,
    geom                 GEOMETRY(POLYGON, 4326) NOT NULL,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_division
        FOREIGN KEY (division_code)
            REFERENCES tbl_country_divisions (division_code)

);
CREATE INDEX idx_subdivisions_division ON country_sub_divisions (division_code);
CREATE INDEX idx_subdivisions_geom ON country_sub_divisions USING GIST (geom);
