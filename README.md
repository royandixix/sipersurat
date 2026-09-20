# SiPersurat

SiPersurat adalah sistem informasi persuratan berbasis web untuk mengelola surat masuk, surat keluar, disposisi, arsip, laporan, pengguna, master data, dan pengaturan sistem.

## Teknologi

- Frontend: SvelteKit 2, Svelte 5, TypeScript, Tailwind CSS, Vite 8
- Backend: Spring Boot 4.1, Java 21, Spring Security, Spring Data JPA
- Database: PostgreSQL Supabase
- Authentication: Session dan CSRF Protection

## Fitur

- Login dan logout
- Dashboard
- Surat Masuk
- Surat Keluar
- Disposisi dan riwayat disposisi
- Arsip
- Laporan
- Manajemen pengguna
- Master Data
- Pengaturan sistem
- Backup dan restore database
- Reset data operasional

## Struktur

```text
sipersurat/
├── backend/
│   ├── scripts/seed_supabase.sql
│   ├── src/main/java/
│   ├── src/main/resources/
│   ├── .env.example
│   └── pom.xml
├── frontend/
│   ├── src/lib/
│   ├── src/routes/
│   └── package.json
└── README.md
```

## Backend

```bash
cd backend
cp .env.example .env
set -a
source .env
set +a
./mvnw spring-boot:run
```

Backend berjalan pada http://localhost:8081.

## Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend berjalan pada http://localhost:5173.

## Validasi

```bash
cd frontend
npm run check
npm run build
```

```bash
cd backend
./mvnw -DskipTests clean compile
```

## Database

Tabel utama:

- users
- incoming_mails
- outgoing_mails
- dispositions
- disposition_histories
- audit_logs
- master_data
- system_settings

Seed demonstrasi tersedia pada backend/scripts/seed_supabase.sql.

## Keamanan

File backend/.env bersifat lokal dan tidak boleh dimasukkan ke Git. Gunakan backend/.env.example sebagai template konfigurasi.
