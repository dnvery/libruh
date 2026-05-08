ALTER TABLE books ADD COLUMN epub_file_size BIGINT NULL AFTER cover_image_path;
ALTER TABLE books ADD COLUMN azw8_file_size BIGINT NULL AFTER epub_file_size;