![Libruh](https://img.shields.io/badge/Libruh-Book%20Library-blue)

**Libruh** is a self-hosted personal book library that stores FB2 files and auto-converts them to EPUB3 and AZW8 via [fb2cng](https://github.com/rupor-github/fb2cng), with an in-browser EPUB reader.

---

## Features

| | |
|---|---|
| 📚 | Upload FB2 or FB2.zip — metadata extracted automatically |
| 🔄 | Automatic FB2 → EPUB3 + AZW8 conversion via fb2cng |
| 📖 | In-browser EPUB reader with themes and reading progress |
| ⚙️ | Full fb2cng config editor with named presets |
| 🔍 | Search across title, author, genre, and sequence |
| 📁 | Download in any format or read directly in the browser |
| 🔖 | Reading position remembered per book |

---

## Quick Start

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & [Docker Compose](https://docs.docker.com/compose/install/)
- fb2cng binary

### Step 1: Environment

```bash
cp .env.example .env
```

Edit `.env` and set `FBC_HOST_PATH` to your fb2cng binary location.

### Step 2: Launch

```bash
docker compose up -d
```

Open **[http://localhost](http://localhost)**, register, and start uploading books.

---

## Development

### Backend

```bash
cd backend
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3, Java 21 |
| Frontend | Vue 3, Vite, Pinia, Tailwind CSS |
| Database | MariaDB 11, Flyway |
| Reader | [epub.js](https://github.com/futurepress/epubjs) |
| Conversion | [fb2cng](https://github.com/rupor-github/fb2cng) |
