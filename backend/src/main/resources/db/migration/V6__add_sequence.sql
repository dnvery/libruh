ALTER TABLE books ADD COLUMN sequence_name VARCHAR(255) NULL AFTER language;
ALTER TABLE books ADD COLUMN sequence_number VARCHAR(10) NULL AFTER sequence_name;