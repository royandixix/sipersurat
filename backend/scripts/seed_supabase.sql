BEGIN;

-- =========================================
-- 1. MASTER DATA
-- =========================================
INSERT INTO master_data(type,name,code,description,status)
VALUES
('ROLE','Super Administrator','SUPER_ADMIN','Akses penuh ke seluruh sistem.','ACTIVE'),
('ROLE','Admin Persuratan','ADMIN_PERSURATAN','Mengelola proses administrasi persuratan.','ACTIVE'),
('ROLE','Pimpinan','PIMPINAN','Menerima dan memberikan disposisi.','ACTIVE'),
('ROLE','Staff','STAFF','Pengguna operasional SiPersurat.','ACTIVE'),

('UNIT_KERJA','Administrator','ADMINISTRATOR','Unit administrasi sistem.','ACTIVE'),
('UNIT_KERJA','Tata Usaha','TATA_USAHA','Unit pengelolaan administrasi persuratan.','ACTIVE'),
('UNIT_KERJA','Pimpinan','PIMPINAN','Unit pimpinan instansi.','ACTIVE'),
('UNIT_KERJA','Sekretariat','SEKRETARIAT','Unit sekretariat.','ACTIVE'),
('UNIT_KERJA','Keuangan','KEUANGAN','Unit pengelolaan keuangan.','ACTIVE'),
('UNIT_KERJA','Kepegawaian','KEPEGAWAIAN','Unit administrasi kepegawaian.','ACTIVE'),
('UNIT_KERJA','Umum','UMUM','Unit pelayanan umum.','ACTIVE'),

('KATEGORI_SURAT','Undangan','UNDANGAN','Surat undangan kegiatan atau rapat.','ACTIVE'),
('KATEGORI_SURAT','Permohonan','PERMOHONAN','Surat permohonan resmi.','ACTIVE'),
('KATEGORI_SURAT','Pemberitahuan','PEMBERITAHUAN','Surat pemberitahuan resmi.','ACTIVE'),
('KATEGORI_SURAT','Internal','INTERNAL','Surat internal instansi.','ACTIVE'),
('KATEGORI_SURAT','Eksternal','EKSTERNAL','Surat eksternal instansi.','ACTIVE'),

('SIFAT_SURAT','Biasa','BIASA','Prioritas normal.','ACTIVE'),
('SIFAT_SURAT','Penting','PENTING','Membutuhkan perhatian khusus.','ACTIVE'),
('SIFAT_SURAT','Rahasia','RAHASIA','Akses terbatas.','ACTIVE'),
('SIFAT_SURAT','Sangat Rahasia','SANGAT_RAHASIA','Tingkat kerahasiaan tertinggi.','ACTIVE')
ON CONFLICT(type,code) DO NOTHING;


-- =========================================
-- 2. SYSTEM SETTINGS
-- =========================================
INSERT INTO system_settings(key,value)
VALUES (
'application',
'{
  "institution": {
    "name": "SiPersurat",
    "shortName": "SiPersurat",
    "address": "Makassar, Sulawesi Selatan",
    "city": "Makassar",
    "province": "Sulawesi Selatan",
    "postalCode": "90111",
    "phone": "0411-000000",
    "email": "admin@sipersurat.local",
    "website": "",
    "headName": "Pimpinan Instansi",
    "headPosition": "Kepala Instansi",
    "logoName": "",
    "logoData": ""
  },
  "numbering": {
    "incomingPrefix": "SM",
    "outgoingPrefix": "SK",
    "dispositionPrefix": "DSP",
    "archivePrefix": "ARS",
    "activeYear": 2026,
    "separator": "-",
    "padding": 4,
    "resetYearly": true,
    "autoAgenda": true
  },
  "agenda": {
    "dateFormat": "DD/MM/YYYY",
    "timezone": "Asia/Makassar",
    "workYear": 2026,
    "incomingStartNumber": 1,
    "outgoingStartNumber": 1,
    "dispositionStartNumber": 1
  },
  "notifications": {
    "incomingMail": true,
    "disposition": true,
    "outgoingApproval": true,
    "outgoingApproved": true,
    "dispositionDeadline": true,
    "archive": true,
    "system": true,
    "deadlineReminderDays": 1
  },
  "security": {
    "sessionMinutes": 120,
    "autoLogout": true,
    "minPasswordLength": 8,
    "requireUppercase": true,
    "requireLowercase": true,
    "requireNumber": true,
    "requireSymbol": false,
    "maxLoginAttempts": 5,
    "twoFactorEnabled": false
  },
  "appearance": {
    "applicationName": "SiPersurat",
    "sidebarName": "SiPersurat",
    "footerText": "Sistem Informasi Persuratan",
    "compactSidebar": false,
    "showInstitutionName": true
  },
  "updatedAt": ""
}'
)
ON CONFLICT(key)
DO UPDATE SET
value = EXCLUDED.value,
updated_at = NOW();


-- =========================================
-- 3. TAMBAH PENGGUNA CONTOH
-- Menggunakan password_hash akun admin yang sudah ada
-- =========================================
INSERT INTO users(name,email,password_hash,role,department,status)
SELECT
'Admin Persuratan',
'admin.persuratan@sipersurat.local',
password_hash,
'ADMIN_PERSURATAN',
'Tata Usaha',
'ACTIVE'
FROM users
WHERE email='randiroyandi@gmail.com'
ON CONFLICT(email) DO NOTHING;

INSERT INTO users(name,email,password_hash,role,department,status)
SELECT
'Pimpinan Instansi',
'pimpinan@sipersurat.local',
password_hash,
'PIMPINAN',
'Pimpinan',
'ACTIVE'
FROM users
WHERE email='randiroyandi@gmail.com'
ON CONFLICT(email) DO NOTHING;

INSERT INTO users(name,email,password_hash,role,department,status)
SELECT
'Staff Sekretariat',
'staff@sipersurat.local',
password_hash,
'STAFF',
'Sekretariat',
'ACTIVE'
FROM users
WHERE email='randiroyandi@gmail.com'
ON CONFLICT(email) DO NOTHING;


-- =========================================
-- 4. SURAT MASUK
-- =========================================
INSERT INTO incoming_mails
(
agenda_number,
letter_number,
letter_date,
received_date,
sender,
subject,
category,
priority,
target_unit,
notes,
status,
file_name,
file_type,
file_size
)
VALUES

(
'SM-2026-0001',
'001/DISDIK/IX/2026',
'2026-09-01',
'2026-09-02',
'Dinas Pendidikan Kota Makassar',
'Undangan Rapat Koordinasi Pendidikan',
'Undangan',
'Penting',
'Pimpinan',
'Undangan rapat koordinasi antar instansi.',
'DISPOSITIONED',
'undangan-rapat.pdf',
'application/pdf',
245000
),

(
'SM-2026-0002',
'045/BKD/IX/2026',
'2026-09-03',
'2026-09-04',
'Badan Kepegawaian Daerah',
'Permintaan Data Kepegawaian',
'Permohonan',
'Penting',
'Kepegawaian',
'Permintaan data pegawai terbaru.',
'IN_PROGRESS',
'permintaan-data.pdf',
'application/pdf',
198000
),

(
'SM-2026-0003',
'121/SEK/IX/2026',
'2026-09-05',
'2026-09-05',
'Sekretariat Daerah',
'Pemberitahuan Jadwal Evaluasi',
'Pemberitahuan',
'Biasa',
'Sekretariat',
'Pemberitahuan jadwal evaluasi bulanan.',
'RECEIVED',
'pemberitahuan.pdf',
'application/pdf',
164000
),

(
'SM-2026-0004',
'073/KEU/IX/2026',
'2026-09-07',
'2026-09-08',
'Badan Keuangan Daerah',
'Permintaan Rekapitulasi Anggaran',
'Permohonan',
'Penting',
'Keuangan',
'Perlu ditindaklanjuti oleh bagian keuangan.',
'COMPLETED',
'rekap-anggaran.pdf',
'application/pdf',
210000
),

(
'SM-2026-0005',
'088/UMUM/IX/2026',
'2026-09-09',
'2026-09-10',
'Dinas Umum Kota Makassar',
'Pemberitahuan Pemeliharaan Gedung',
'Pemberitahuan',
'Biasa',
'Umum',
'Surat sudah selesai diproses dan diarsipkan.',
'ARCHIVED',
'pemeliharaan-gedung.pdf',
'application/pdf',
181000
)

ON CONFLICT(agenda_number) DO NOTHING;


-- =========================================
-- 5. SURAT KELUAR
-- =========================================
INSERT INTO outgoing_mails
(
agenda_number,
letter_number,
letter_date,
recipient,
subject,
category,
priority,
source_unit,
signer,
notes,
status,
sent_date,
file_name,
file_type,
file_size
)
VALUES

(
'SK-2026-0001',
'001/SIPERSURAT/IX/2026',
'2026-09-02',
'Dinas Pendidikan Kota Makassar',
'Konfirmasi Kehadiran Rapat Koordinasi',
'Eksternal',
'Penting',
'Sekretariat',
'Kepala Instansi',
'Konfirmasi kehadiran pada rapat koordinasi.',
'SENT',
'2026-09-02',
'konfirmasi-rapat.pdf',
'application/pdf',
175000
),

(
'SK-2026-0002',
'002/SIPERSURAT/IX/2026',
'2026-09-05',
'Badan Kepegawaian Daerah',
'Pengiriman Data Kepegawaian',
'Eksternal',
'Penting',
'Kepegawaian',
'Kepala Instansi',
'Data kepegawaian sesuai permintaan.',
'APPROVED',
NULL,
'data-kepegawaian.pdf',
'application/pdf',
320000
),

(
'SK-2026-0003',
'003/SIPERSURAT/IX/2026',
'2026-09-07',
'Seluruh Unit Kerja',
'Pemberitahuan Rapat Internal',
'Internal',
'Biasa',
'Sekretariat',
'Sekretaris',
'Draft pemberitahuan rapat internal.',
'DRAFT',
NULL,
NULL,
NULL,
0
),

(
'SK-2026-0004',
'004/SIPERSURAT/IX/2026',
'2026-09-10',
'Badan Keuangan Daerah',
'Pengiriman Rekapitulasi Anggaran',
'Eksternal',
'Penting',
'Keuangan',
'Kepala Instansi',
'Surat telah dikirim dan diarsipkan.',
'ARCHIVED',
'2026-09-10',
'rekapitulasi-anggaran.pdf',
'application/pdf',
286000
)

ON CONFLICT(agenda_number) DO NOTHING;


-- =========================================
-- 6. DISPOSISI
-- =========================================
INSERT INTO dispositions
(
code,
incoming_mail_id,
target_type,
target_id,
target_name,
instruction,
priority,
due_date,
notes,
status,
created_by
)
SELECT
'DSP-2026-0001',
id,
'UNIT',
'TATA_USAHA',
'Tata Usaha',
'Pelajari surat dan siapkan bahan rapat.',
'IMPORTANT',
'2026-09-15',
'Segera koordinasikan dengan pimpinan.',
'COMPLETED',
'Super Administrator'
FROM incoming_mails
WHERE agenda_number='SM-2026-0001'
ON CONFLICT(code) DO NOTHING;

INSERT INTO dispositions
(
code,
incoming_mail_id,
target_type,
target_id,
target_name,
instruction,
priority,
due_date,
notes,
status,
created_by
)
SELECT
'DSP-2026-0002',
id,
'UNIT',
'KEPEGAWAIAN',
'Kepegawaian',
'Siapkan dan verifikasi data pegawai.',
'URGENT',
'2026-09-18',
'Pastikan data terbaru.',
'IN_PROGRESS',
'Super Administrator'
FROM incoming_mails
WHERE agenda_number='SM-2026-0002'
ON CONFLICT(code) DO NOTHING;

INSERT INTO dispositions
(
code,
incoming_mail_id,
target_type,
target_id,
target_name,
instruction,
priority,
due_date,
notes,
status,
created_by
)
SELECT
'DSP-2026-0003',
id,
'UNIT',
'SEKRETARIAT',
'Sekretariat',
'Pelajari isi surat dan laporkan kepada pimpinan.',
'NORMAL',
'2026-09-20',
'',
'WAITING',
'Super Administrator'
FROM incoming_mails
WHERE agenda_number='SM-2026-0003'
ON CONFLICT(code) DO NOTHING;


-- =========================================
-- 7. RIWAYAT DISPOSISI
-- =========================================
INSERT INTO disposition_histories
(disposition_id,status,note,created_at)

SELECT id,'WAITING','Disposisi dibuat.',NOW() - INTERVAL '3 days'
FROM dispositions
WHERE code='DSP-2026-0001'
AND NOT EXISTS (
    SELECT 1 FROM disposition_histories dh
    WHERE dh.disposition_id=dispositions.id
    AND dh.status='WAITING'
);

INSERT INTO disposition_histories
(disposition_id,status,note,created_at)

SELECT id,'RECEIVED','Disposisi diterima oleh Tata Usaha.',NOW() - INTERVAL '2 days'
FROM dispositions
WHERE code='DSP-2026-0001'
AND NOT EXISTS (
    SELECT 1 FROM disposition_histories dh
    WHERE dh.disposition_id=dispositions.id
    AND dh.status='RECEIVED'
);

INSERT INTO disposition_histories
(disposition_id,status,note,created_at)

SELECT id,'COMPLETED','Tindak lanjut disposisi telah selesai.',NOW() - INTERVAL '1 day'
FROM dispositions
WHERE code='DSP-2026-0001'
AND NOT EXISTS (
    SELECT 1 FROM disposition_histories dh
    WHERE dh.disposition_id=dispositions.id
    AND dh.status='COMPLETED'
);

INSERT INTO disposition_histories
(disposition_id,status,note,created_at)

SELECT id,'WAITING','Disposisi diterbitkan.',NOW() - INTERVAL '2 days'
FROM dispositions
WHERE code='DSP-2026-0002'
AND NOT EXISTS (
    SELECT 1 FROM disposition_histories dh
    WHERE dh.disposition_id=dispositions.id
    AND dh.status='WAITING'
);

INSERT INTO disposition_histories
(disposition_id,status,note,created_at)

SELECT id,'IN_PROGRESS','Data sedang diverifikasi.',NOW() - INTERVAL '1 day'
FROM dispositions
WHERE code='DSP-2026-0002'
AND NOT EXISTS (
    SELECT 1 FROM disposition_histories dh
    WHERE dh.disposition_id=dispositions.id
    AND dh.status='IN_PROGRESS'
);


-- =========================================
-- 8. AUDIT LOG
-- =========================================
INSERT INTO audit_logs
(actor_name,actor_initials,action,description,value)

SELECT
'Super Administrator',
'SA',
'LOGIN',
'Administrator masuk ke aplikasi.',
'randiroyandi@gmail.com'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs
    WHERE description='Administrator masuk ke aplikasi.'
);

INSERT INTO audit_logs
(actor_name,actor_initials,action,description,value)

SELECT
'Super Administrator',
'SA',
'CREATE',
'Menambahkan data surat masuk.',
'SM-2026-0001'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs
    WHERE value='SM-2026-0001'
);

INSERT INTO audit_logs
(actor_name,actor_initials,action,description,value)

SELECT
'Admin Persuratan',
'AP',
'DISPOSITION',
'Membuat disposisi surat masuk.',
'DSP-2026-0001'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs
    WHERE value='DSP-2026-0001'
);


-- =========================================
-- 9. RAPINKAN SEQUENCE
-- =========================================
SELECT setval(
    pg_get_serial_sequence('incoming_mails','id'),
    COALESCE((SELECT MAX(id) FROM incoming_mails),1)
);

SELECT setval(
    pg_get_serial_sequence('outgoing_mails','id'),
    COALESCE((SELECT MAX(id) FROM outgoing_mails),1)
);

SELECT setval(
    pg_get_serial_sequence('dispositions','id'),
    COALESCE((SELECT MAX(id) FROM dispositions),1)
);

SELECT setval(
    pg_get_serial_sequence('disposition_histories','id'),
    COALESCE((SELECT MAX(id) FROM disposition_histories),1)
);

SELECT setval(
    pg_get_serial_sequence('audit_logs','id'),
    COALESCE((SELECT MAX(id) FROM audit_logs),1)
);

SELECT setval(
    pg_get_serial_sequence('users','id'),
    COALESCE((SELECT MAX(id) FROM users),1)
);

SELECT setval(
    pg_get_serial_sequence('master_data','id'),
    COALESCE((SELECT MAX(id) FROM master_data),1)
);

COMMIT;
