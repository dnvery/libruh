CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    author VARCHAR(500) NOT NULL,
    genre VARCHAR(255),
    description TEXT,
    publication_date DATE,
    language VARCHAR(10),
    fb2_file_path VARCHAR(1000) NOT NULL,
    epub_file_path VARCHAR(1000),
    azw8_file_path VARCHAR(1000),
    conversion_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') NOT NULL DEFAULT 'PENDING',
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_genre ON books(genre);
CREATE INDEX idx_books_user_id ON books(user_id);
CREATE INDEX idx_books_conversion_status ON books(conversion_status);