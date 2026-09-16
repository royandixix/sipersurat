CREATE TABLE IF NOT EXISTS incoming_mails(
    id BIGSERIAL PRIMARY KEY,
    agenda_number VARCHAR(100) UNIQUE NOT NULL,
    letter_number VARCHAR(255) NOT NULL,
    letter_date DATE NOT NULL,
    received_date DATE NOT NULL,
    sender VARCHAR(255) NOT NULL,
    subject TEXT NOT NULL,
    category VARCHAR(150),
    priority VARCHAR(50),
    target_unit VARCHAR(150),
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'RECEIVED',
    file_name VARCHAR(255),
    file_type VARCHAR(150),
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS outgoing_mails(
    id BIGSERIAL PRIMARY KEY,
    agenda_number VARCHAR(100) UNIQUE NOT NULL,
    letter_number VARCHAR(255) NOT NULL,
    letter_date DATE NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject TEXT NOT NULL,
    category VARCHAR(150),
    priority VARCHAR(50),
    source_unit VARCHAR(150),
    signer VARCHAR(255),
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    sent_date DATE,
    file_name VARCHAR(255),
    file_type VARCHAR(150),
    file_size BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS dispositions(
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    incoming_mail_id BIGINT NOT NULL REFERENCES incoming_mails(id) ON DELETE CASCADE,
    target_type VARCHAR(50) NOT NULL,
    target_id VARCHAR(100),
    target_name VARCHAR(255) NOT NULL,
    instruction TEXT NOT NULL,
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    due_date DATE,
    notes TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'WAITING',
    created_by VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS audit_logs(
    id BIGSERIAL PRIMARY KEY,
    actor_name VARCHAR(255) NOT NULL,
    actor_initials VARCHAR(10) NOT NULL,
    action VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    value VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS users(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    department VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_incoming_mails_status ON incoming_mails(status);
CREATE INDEX IF NOT EXISTS idx_incoming_mails_created_at ON incoming_mails(created_at);
CREATE INDEX IF NOT EXISTS idx_outgoing_mails_status ON outgoing_mails(status);
CREATE INDEX IF NOT EXISTS idx_outgoing_mails_created_at ON outgoing_mails(created_at);
CREATE INDEX IF NOT EXISTS idx_dispositions_status ON dispositions(status);
CREATE INDEX IF NOT EXISTS idx_dispositions_priority ON dispositions(priority);
CREATE INDEX IF NOT EXISTS idx_dispositions_created_at ON dispositions(created_at);
CREATE INDEX IF NOT EXISTS idx_audit_logs_created_at ON audit_logs(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);