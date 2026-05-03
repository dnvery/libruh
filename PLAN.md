# Libruh — Book Library Application: Implementation Plan

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.3 (Java 21), Spring Security, Spring Data JPA |
| Frontend | Vue 3 + Vite + Pinia + Vue Router + Tailwind CSS |
| Database | MariaDB 11 |
| Auth | JWT (access token 15 min + refresh token 7 days) |
| Conversion | `fbc` (fb2cng CLI) — converts FB2 → EPUB3 + AZW8 |
| Infra | Docker Compose |

## Key Decisions

- **fbc binary**: Mounted read-only from host into Docker container
- **Book visibility**: Public library — all authenticated users can browse/search all books; only the uploader can edit/delete their own
- **Java version**: 21 (Spring Boot 3.x)
- **Conversion formats**: EPUB3 + AZW8 via `fbc` CLI
- **Monorepo** with Docker Compose
- **Tailwind CSS** for frontend
- **JWT**: Access token (15 min) + Refresh token (7 days, stored in DB)

## fb2cng (`fbc`) CLI Reference

The executable is named `fbc`. Key commands:
```bash
# Single file conversion
fbc convert --to epub3 book.fb2 /output/dir/
fbc convert --to azw8 book.fb2 /output/dir/

# With overwrite
fbc convert --to epub3 --ow book.fb2 /output/dir/

# With custom config
fbc -c myconfig.yaml convert --to epub3 book.fb2

# Debug mode
fbc -d convert --to epub3 book.fb2
```
Self-contained Go binary — no runtime dependencies.

Supported output formats: `epub2`, `epub3`, `kepub`, `kfx`, `azw8`

---

## 1. Project Structure (Monorepo)

```
libruh/
├── backend/                                # Spring Boot
│   ├── src/main/java/com/libruh/
│   │   ├── LibruhApplication.java         # Main entry point
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        # Spring Security + JWT filter chain
│   │   │   ├── CorsConfig.java           # CORS for frontend
│   │   │   ├── JwtConfig.java             # JWT properties from application.yml
│   │   │   ├── AsyncConfig.java           # @Async thread pool for conversions
│   │   │   └── WebConfig.java             # Static resources, MVC config
│   │   ├── controller/
│   │   │   ├── AuthController.java        # POST /api/auth/register|login|refresh|logout
│   │   │   ├── BookController.java        # CRUD /api/books/** + search + download
│   │   │   └── UserController.java        # GET /api/users/me
│   │   ├── dto/
│   │   │   ├── auth/
│   │   │   │   ├── LoginRequest.java      # username + password
│   │   │   │   ├── RegisterRequest.java   # username + email + password
│   │   │   │   ├── AuthResponse.java      # accessToken + refreshToken
│   │   │   │   └── RefreshRequest.java    # refreshToken
│   │   │   ├── book/
│   │   │   │   ├── BookResponse.java      # Full book DTO
│   │   │   │   ├── BookListResponse.java  # Paginated list DTO
│   │   │   │   ├── BookCreateRequest.java # Metadata (auto-extracted from FB2)
│   │   │   │   └── BookUpdateRequest.java # Partial update DTO
│   │   │   └── user/
│   │   │       └── UserResponse.java      # User DTO
│   │   ├── entity/
│   │   │   ├── User.java                  # id, username, email, password, role, createdAt
│   │   │   ├── Book.java                  # id, title, author, genre, description, ...
│   │   │   └── RefreshToken.java          # id, token, user, expiryDate
│   │   ├── enums/
│   │   │   ├── Role.java                  # USER, ADMIN
│   │   │   └── ConversionStatus.java      # PENDING, PROCESSING, COMPLETED, FAILED
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   ├── FileUploadException.java
│   │   │   └── ConversionException.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── BookRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   ├── security/
│   │   │   ├── JwtAuthFilter.java         # OncePerRequestFilter for JWT validation
│   │   │   └── JwtTokenProvider.java      # Create + validate JWT tokens
│   │   └── service/
│   │       ├── AuthService.java           # register, login, refresh, logout
│   │       ├── BookService.java           # CRUD, search, file management
│   │       ├── ConversionService.java     # Async FB2 → EPUB3/AZW8 via fbc
│   │       └── Fb2MetadataParser.java     # Extract metadata from FB2 XML
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   │       ├── V1__create_users_table.sql
│   │       ├── V2__create_books_table.sql
│   │       └── V3__create_refresh_tokens_table.sql
│   ├── src/test/java/com/libruh/
│   │   └── ...                            # Integration tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                               # Vue 3 + Vite
│   ├── public/
│   │   └── favicon.ico
│   ├── src/
│   │   ├── assets/
│   │   │   └── main.css                   # Tailwind imports
│   │   ├── components/
│   │   │   ├── Navbar.vue                  # Top nav with auth state
│   │   │   ├── BookCard.vue               # Individual book card component
│   │   │   ├── SearchBar.vue              # Search input with field selector
│   │   │   ├── FileUpload.vue             # FB2 file drop zone
│   │   │   ├── Pagination.vue             # Page navigation
│   │   │   └── ProtectedRoute.vue         # Route guard wrapper
│   │   ├── views/
│   │   │   ├── LoginView.vue
│   │   │   ├── RegisterView.vue
│   │   │   ├── BookListView.vue           # Home page — paginated book grid
│   │   │   ├── BookDetailView.vue         # Metadata + download links + status
│   │   │   ├── BookUploadView.vue         # Upload FB2 form
│   │   │   ├── BookEditView.vue           # Edit book metadata
│   │   │   └── ProfileView.vue            # User's uploaded books
│   │   ├── router/
│   │   │   └── index.js                   # Vue Router config with guards
│   │   ├── stores/
│   │   │   ├── auth.js                    # Pinia auth store (tokens, user)
│   │   │   └── books.js                   # Pinia books store (list, current)
│   │   ├── services/
│   │   │   ├── api.js                     # Axios instance with interceptors
│   │   │   ├── authService.js             # Login, register, refresh, logout
│   │   │   └── bookService.js             # CRUD, search, download
│   │   ├── App.vue
│   │   └── main.js
│   ├── Dockerfile
│   ├── index.html
│   ├── package.json
│   ├── tailwind.config.js
│   ├── postcss.config.js
│   └── vite.config.js
├── docker-compose.yml
├── .env.example
└── PLAN.md                                 # This file
```

---

## 2. Database Schema (MariaDB + Flyway)

### V1__create_users_table.sql
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### V2__create_books_table.sql
```sql
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
```

### V3__create_refresh_tokens_table.sql
```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
```

---

## 3. API Design

### Auth Endpoints

| Method | Endpoint | Auth? | Description |
|--------|----------|-------|-------------|
| POST | `/api/auth/register` | No | Create account (username, email, password) |
| POST | `/api/auth/login` | No | Returns `{ accessToken, refreshToken }` |
| POST | `/api/auth/refresh` | No | Exchange refresh token for new access token |
| POST | `/api/auth/logout` | Yes | Invalidate refresh token |

### Book Endpoints

| Method | Endpoint | Auth? | Description |
|--------|----------|-------|-------------|
| GET | `/api/books?page=0&size=20` | Yes | Paginated book list |
| GET | `/api/books/{id}` | Yes | Book details |
| POST | `/api/books` | Yes | Upload FB2 file (multipart `file` + optional JSON metadata) |
| PUT | `/api/books/{id}` | Yes (owner) | Update book metadata |
| DELETE | `/api/books/{id}` | Yes (owner) | Delete book + associated files |
| GET | `/api/books/search?query=...&field=title\|author\|genre` | Yes | Search books |
| GET | `/api/books/{id}/download/epub` | Yes | Download converted EPUB3 file |
| GET | `/api/books/{id}/download/azw8` | Yes | Download converted AZW8 file |

### User Endpoints

| Method | Endpoint | Auth? | Description |
|--------|----------|-------|-------------|
| GET | `/api/users/me` | Yes | Current user profile |

---

## 4. Authentication Flow

### Registration
1. POST `/api/auth/register` with `{ username, email, password }`
2. Validate uniqueness of username + email
3. Hash password with BCrypt
4. Create User entity with role `USER`
5. Return `{ accessToken, refreshToken }` (auto-login)

### Login
1. POST `/api/auth/login` with `{ username, password }`
2. Validate credentials
3. Generate JWT access token (15 min expiry)
4. Generate refresh token (7 day expiry), store in `refresh_tokens` table
5. Return both tokens

### Access Token
- Contains claims: `userId`, `username`, `role`
- Sent in `Authorization: Bearer <token>` header
- Validated by `JwtAuthFilter` on every request

### Refresh Token
- Random UUID stored in DB
- POST `/api/auth/refresh` with `{ refreshToken }`
- Validate token exists and not expired
- Generate new access token
- Optionally rotate refresh token

### Logout
- POST `/api/auth/logout`
- Delete refresh token from DB
- Client discards both tokens

---

## 5. FB2 Upload & Conversion Flow

### Upload Process
1. POST `/api/books` with multipart form (file + optional metadata)
2. Validate file is `.fb2` extension and non-empty
3. Generate UUID for file storage
4. Save original FB2 to `{STORAGE_DIR}/fb2/{uuid}.fb2`
5. Parse FB2 XML to extract metadata:
   - `<title-info>` → `<book-title>` → title
   - `<title-info>` → `<author>` → first-name, last-name
   - `<title-info>` → `<genre>` → genre
   - `<title-info>` → `<annotation>` → description
   - `<title-info>` → `<date>` → publication_date
   - `<title-info>` → `<lang>` → language
6. Create `Book` entity with extracted metadata, `conversion_status = PENDING`
7. Trigger async conversion
8. Return `BookResponse` with `conversion_status = PENDING`

### Conversion Process (Async)
```
ConversionService.convertAsync(bookId):
  1. Set status → PROCESSING
  2. Run in parallel:
     a. fbc convert --to epub3 {uuid}.fb2 /storage/epub/
     b. fbc convert --to azw8 {uuid}.fb2 /storage/azw8/
  3. fbc names the output file based on book metadata (title-author)
     → We rename output files to {uuid}.epub / {uuid}.azw8 for consistency
  4. On success:
     - Update epub_file_path = "/storage/epub/{uuid}.epub"
     - Update azw8_file_path = "/storage/azw8/{uuid}.azw8"
     - Set status → COMPLETED
  5. On failure:
     - Set status → FAILED
     - Log error details
```

### Download Process
1. GET `/api/books/{id}/download/epub`
2. Check book exists and `conversion_status == COMPLETED`
3. Stream file from `epub_file_path`
4. Set `Content-Disposition: attachment; filename="{title}.epub"`

---

## 6. Frontend Architecture

### Pinia Stores

**auth.js**
```
state: { user, accessToken, refreshToken }
actions: login(), register(), logout(), refreshAccessToken()
getters: isAuthenticated, username
Persistence: tokens in localStorage
```

**books.js**
```
state: { books[], currentBook, pagination, searchQuery, searchField, conversionStatus }
actions: fetchBooks(page, size), fetchBook(id), uploadBook(formData), updateBook(id, data), deleteBook(id), searchBooks(query, field), downloadBook(id, format)
```

### Axios Interceptors
- **Request interceptor**: Attach `Authorization: Bearer {accessToken}` to every request
- **Response interceptor**: On 401, attempt token refresh via `/api/auth/refresh`, then retry original request. On refresh failure, redirect to `/login`.

### Vue Router Guards
- `meta: { requiresAuth: true }` on all authenticated routes
- Redirect to `/login` if not authenticated
- After login, redirect to originally requested page

### Pages

| Route | View | Description |
|-------|------|-------------|
| `/login` | LoginView | Email/password form, link to register |
| `/register` | RegisterView | Username/email/password form |
| `/` | BookListView | Grid of book cards, search bar, pagination |
| `/books/:id` | BookDetailView | Metadata, download buttons, conversion status badge, owner actions |
| `/books/new` | BookUploadView | FB2 file drop zone, metadata preview |
| `/books/:id/edit` | BookEditView | Edit title, author, genre, description |
| `/profile` | ProfileView | Current user's uploaded books |

---

## 7. Docker Compose

```yaml
services:
  mariadb:
    image: mariadb:11
    environment:
      MARIADB_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MARIADB_DATABASE: libruh
    volumes:
      - mariadb_data:/var/lib/mysql
    ports:
      - "3306:3306"
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mariadb://mariadb:3306/libruh
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: ${DB_ROOT_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_ACCESS_EXPIRATION: 900000          # 15 min in ms
      JWT_REFRESH_EXPIRATION: 604800000        # 7 days in ms
      STORAGE_DIR: /app/storage
      FBC_PATH: /usr/local/bin/fbc
    volumes:
      - book_storage:/app/storage
      - ${FBC_HOST_PATH:-/usr/local/bin/fbc}:/usr/local/bin/fbc:ro
    depends_on:
      mariadb:
        condition: service_healthy

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mariadb_data:
  book_storage:
```

### .env.example
```
DB_ROOT_PASSWORD=change_me_in_production
JWT_SECRET=change_me_to_a_long_random_string_at_least_256_bits
FBC_HOST_PATH=/usr/local/bin/fbc
```

---

## 8. Backend pom.xml Dependencies

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.0</version>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <!-- Web -->
    spring-boot-starter-web

    <!-- Security -->
    spring-boot-starter-security

    <!-- JPA -->
    spring-boot-starter-data-jpa

    <!-- Validation -->
    spring-boot-starter-validation

    <!-- MariaDB -->
    org.mariadb.jdbc:mariadb-java-client

    <!-- Flyway -->
    flyway-core
    flyway-mariadb

    <!-- JWT -->
    io.jsonwebtoken:jjwt-api:0.12.5
    io.jsonwebtoken:jjwt-impl:0.12.5
    io.jsonwebtoken:jjwt-jackson:0.12.5

    <!-- XML Parsing (for FB2 metadata) -->
    jakarta.xml.bind:jakarta.xml.bind-api          (via spring-boot-starter-web)
    com.sun.xml.bind:jaxb-impl                      (runtime)

    <!-- Test -->
    spring-boot-starter-test
    spring-security-test
    h2 (test scope)
</dependencies>
```

---

## 9. Backend application.yml

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mariadb://localhost:3306/libruh}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:change_me}
    driver-class-name: org.mariadb.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
  flyway:
    enabled: true
    locations: classpath:db/migration
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

jwt:
  secret: ${JWT_SECRET:change_me_to_a_long_random_string_at_least_256_bits}
  access-expiration: ${JWT_ACCESS_EXPIRATION:900000}
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}

storage:
  dir: ${STORAGE_DIR:/app/storage}
  fb2-subdir: fb2
  epub-subdir: epub
  azw8-subdir: azw8

fbc:
  path: ${FBC_PATH:/usr/local/bin/fbc}
```

---

## 10. FB2 Metadata Extraction (Fb2MetadataParser)

FB2 files are XML. Key elements to parse:

```xml
<FictionBook>
  <description>
    <title-info>
      <genre>fiction</genre>
      <author>
        <first-name>Leo</first-name>
        <last-name>Tolstoy</last-name>
      </author>
      <book-title>War and Peace</book-title>
      <annotation>
        <p>Book description...</p>
      </annotation>
      <date>1869</date>
      <lang>ru</lang>
    </title-info>
  </description>
  <body>...</body>
  <binary id="cover.jpg" content-type="image/jpeg">...</binary>
</FictionBook>
```

Parser extracts:
- `book-title` → `title`
- `author/first-name` + `author/last-name` → `author`
- `genre` → `genre`
- `annotation` → `description` (extract text content, strip tags)
- `date` → `publicationDate`
- `lang` → `language`

---

## 11. ConversionService Implementation

```java
@Service
public class ConversionService {

    @Async
    public void convertAsync(Long bookId) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(...);

        book.setConversionStatus(ConversionStatus.PROCESSING);
        bookRepository.save(book);

        try {
            Path fb2Path = Path.of(book.getFb2FilePath());
            Path epubDir = storageDir.resolve("epub");
            Path azw8Dir = storageDir.resolve("azw8");

            // Run conversions in parallel
            Process epubProcess = runFbc("--to", "epub3", fb2Path.toString(), epubDir.toString());
            Process azw8Process = runFbc("--to", "azw8", fb2Path.toString(), azw8Dir.toString());

            int epubExit = epubProcess.waitFor();
            int azw8Exit = azw8Process.waitFor();

            if (epubExit != 0 || azw8Exit != 0) {
                throw new ConversionException("fbc conversion failed");
            }

            // Find output files and rename to {uuid}.epub / {uuid}.azw8
            String uuid = extractUuidFromPath(fb2Path);
            Path epubOutput = findOutputFile(epubDir, uuid, "epub");
            Path azw8Output = findOutputFile(azw8Dir, uuid, "azw8");

            book.setEpubFilePath(epubOutput.toString());
            book.setAzw8FilePath(azw8Output.toString());
            book.setConversionStatus(ConversionStatus.COMPLETED);
        } catch (Exception e) {
            book.setConversionStatus(ConversionStatus.FAILED);
            log.error("Conversion failed for book {}", bookId, e);
        }

        bookRepository.save(book);
    }

    private Process runFbc(String... args) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(fbcPath);
        command.add("convert");
        command.addAll(Arrays.asList(args));

        return new ProcessBuilder(command)
            .redirectErrorStream(true)
            .start();
    }
}
```

**Important**: `fbc` generates output filenames from book metadata (e.g., `Tolstoy Leo - War and Peace.epub`). The service needs to:
1. List files in the output directory after conversion
2. Find the newly created file (most recent, or by pattern matching)
3. Rename to `{uuid}.epub` / `{uuid}.azw8` for consistent storage

---

## 12. Frontend Key Components

### api.js (Axios Instance)
```javascript
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
})

// Request interceptor: attach access token
api.interceptors.request.use(config => {
  const auth = useAuthStore()
  if (auth.accessToken) {
    config.headers.Authorization = `Bearer ${auth.accessToken}`
  }
  return config
})

// Response interceptor: auto-refresh on 401
api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      const auth = useAuthStore()
      try {
        await auth.refreshAccessToken()
        error.config.headers.Authorization = `Bearer ${auth.accessToken}`
        return api(error.config)
      } catch {
        auth.logout()
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default api
```

### BookListView.vue (Key Layout)
- Top: SearchBar component (input + field selector: title/author/genre)
- Grid: 4-column responsive layout of BookCard components
- Bottom: Pagination component
- BookCard: Cover placeholder, title, author, genre badge, conversion status indicator
- Status badges: PENDING (yellow), PROCESSING (blue spinner), COMPLETED (green check), FAILED (red X)

### BookDetailView.vue (Key Layout)
- Full metadata display
- Download buttons for EPUB3 and AZW8 (disabled if conversion not COMPLETE)
- "Edit" and "Delete" buttons visible only to book owner
- Conversion status badge with auto-refresh (poll every 5s while PENDING/PROCESSING)

### BookUploadView.vue (Key Layout)
- Drag-and-drop zone for .fb2 files
- File validation (only .fb2 extension, max 50MB)
- Upload progress bar
- Display extracted metadata after upload

---

## 13. Dockerfiles

### Backend Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mkdir -p /app/storage/fb2 /app/storage/epub /app/storage/azw8
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile
```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### nginx.conf
```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        client_max_body_size 50M;
    }
}
```

---

## 14. Implementation Order

| Phase | Tasks | Files |
|-------|-------|-------|
| **1. Skeleton** | Initialize Spring Boot project, Vue 3 project, docker-compose, MariaDB config, Flyway migrations, base configs | pom.xml, application.yml, V1-V3 migrations, docker-compose.yml, .env.example, package.json, vite.config.js, tailwind.config.js |
| **2. Backend Auth** | User entity, RefreshToken entity, JwtTokenProvider, JwtAuthFilter, SecurityConfig, AuthController, AuthService, DTOs | All entity/*, security/*, controller/AuthController, service/AuthService, dto/auth/* |
| **3. Backend Books CRUD** | Book entity, BookController, BookService, file upload/download handling | entity/Book.java, controller/BookController, service/BookService, dto/book/*, enums/* |
| **4. Backend Conversion** | ConversionService (ProcessBuilder + async), Fb2MetadataParser | service/ConversionService.java, service/Fb2MetadataParser.java, config/AsyncConfig.java |
| **5. Backend Search & Downloads** | Search endpoint, file download endpoints | BookController additions, BookRepository with JPA queries |
| **6. Frontend Auth** | Login/Register views, Pinia auth store, Axios interceptors, route guards | views/LoginView.vue, views/RegisterView.vue, stores/auth.js, services/api.js, services/authService.js, router/index.js, components/ProtectedRoute.vue |
| **7. Frontend Book UI** | Book list, detail, upload, edit views with Tailwind | views/BookListView.vue, views/BookDetailView.vue, views/BookUploadView.vue, views/BookEditView.vue, components/BookCard.vue, components/FileUpload.vue, stores/books.js, services/bookService.js |
| **8. Frontend Search** | Search bar with field selector | components/SearchBar.vue, updates to BookListView |
| **9. Docker & Polish** | Dockerfiles, final docker-compose, error handling, validation, edge cases | Dockerfiles, nginx.conf, final docker-compose.yml, exception handling improvements |

---

## 15. Security Considerations

- **JWT Secret**: Must be at least 256-bit, loaded from environment variable
- **CORS**: Restrict to frontend origin in production
- **File Upload**: Validate `.fb2` extension, limit file size (50MB), store outside web root
- **Path Traversal**: Use UUID-based filenames, never trust user-supplied filenames
- **SQL Injection**: JPA/Hibernate parameterized queries
- **XSS**: Vue's template system auto-escapes; backend returns JSON
- **Password**: BCrypt with strength 12
- **Refresh Token**: One-time use with rotation on refresh; delete on logout
- **File Download**: Only allow authenticated users; stream files from storage directory (never expose storage path to client)

---

## 16. Error Handling

| Scenario | HTTP Status | Response |
|----------|-------------|----------|
| Invalid credentials | 401 | `{ error: "Invalid username or password" }` |
| Expired/invalid JWT | 401 | `{ error: "Token expired" }` |
| Book not found | 404 | `{ error: "Book not found" }` |
| Non-owner trying to edit/delete | 403 | `{ error: "You can only modify your own books" }` |
| Invalid file type | 400 | `{ error: "Only .fb2 files are accepted" }` |
| File too large | 413 | `{ error: "File size exceeds 50MB limit" }` |
| Conversion failed | 200 (book returned with `conversion_status: FAILED`) | |
| Username/email already exists | 409 | `{ error: "Username already taken" }` |