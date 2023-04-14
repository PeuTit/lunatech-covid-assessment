CREATE TABLE IF NOT EXISTS countries (
  id              INTEGER CONSTRAINT PK_COUNTRIES PRIMARY KEY,
  code            VARCHAR(10) NOT NULL,
  name            VARCHAR(255) NOT NULL,
  population      INTEGER NOT NULL,
  continent       VARCHAR(10) NOT NULL,
  wikipedia_link  VARCHAR(255),
  keywords        VARCHAR(100),
  CONSTRAINT UNQ_CODE UNIQUE(code),
  CONSTRAINT UNQ_NAME UNIQUE(name)
);

CREATE TABLE IF NOT EXISTS cases (
  recorded_date         DATE,
  infections            INTEGER NOT NULL,
  deaths                INTEGER NOT NULL,
  iso_country           VARCHAR(10),
  PRIMARY KEY (recorded_date, iso_country),
  FOREIGN KEY (iso_country) REFERENCES countries(code)
);

CREATE TABLE IF NOT EXISTS vaccinations (
  recorded_date             DATE,
  daily_vaccinations_raw    INTEGER,
  daily_vaccinations        INTEGER,
  iso_country               VARCHAR(10),
  PRIMARY KEY (recorded_date, iso_country),
  FOREIGN KEY (iso_country) REFERENCES countries(code)
);
