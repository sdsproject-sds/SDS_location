create EXTENSION IF NOT EXISTS postgis;

create TABLE IF NOT EXISTS tbl_country
(
    id            BIGSERIAL UNIQUE,
    iso2          CHAR(2) PRIMARY KEY,
    iso3          CHAR(3) UNIQUE NOT NULL,
    name          TEXT           NOT NULL,
    currency_code CHAR(3),

    status        varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by    varchar(200),
    updated_by    varchar(200)
);
insert into tbl_country (iso2, iso3, name, currency_code)
values ('KE', 'KEN', 'Kenya', 'KES');


create TABLE IF NOT EXISTS tbl_country_divisions
(
    id            BIGSERIAL UNIQUE,
    country_iso2  CHAR(2) NOT NULL,
    division      TEXT    NOT NULL,
    division_code CHAR(3) PRIMARY KEY, -- e.g. NBO
    geom          GEOMETRY(POLYGON, 4326),
    supported     boolean DEFAULT true,

    status               VARCHAR(20) NOT NULL default 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by    varchar(200),
    updated_by    varchar(200),

    CONSTRAINT fk_country
        FOREIGN KEY (country_iso2)
            REFERENCES tbl_country (iso2)
);

create TABLE IF NOT EXISTS country_sub_divisions
(
    id                   BIGSERIAL PRIMARY KEY,
    division_code        TEXT NOT NULL,
    country_sub_division TEXT NOT NULL,
    geom                 GEOMETRY(POLYGON, 4326) NOT NULL,

    status               VARCHAR(20) NOT NULL default 'ACTIVE',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by           varchar(200),
    updated_by           varchar(200),

    CONSTRAINT fk_division
        FOREIGN KEY (division_code)
            REFERENCES tbl_country_divisions (division_code)

);
create index idx_subdivisions_division on country_sub_divisions (division_code);
create index idx_subdivisions_geom on country_sub_divisions USING GIST (geom);
