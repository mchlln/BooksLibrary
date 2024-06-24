CREATE TABLE IF NOT EXISTS "BOOKS" (
    "ID" INTEGER PRIMARY KEY,
    "TITLE" VARCHAR(255) NOT NULL,
    "AUTHOR" VARCHAR(255) NOT NULL,
    "PUBLISHER" VARCHAR(255) NOT NULL,
    "PUBLICATION_YEAR" INTEGER NOT NULL,
    "SYNOPSIS" VARCHAR(255)
    );