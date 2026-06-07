-- 流浪狗救助站数据库初始化脚本

CREATE TABLE IF NOT EXISTS dogs (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    gender VARCHAR(10),
    age INTEGER,
    health_status VARCHAR(200),
    description TEXT,
    image_urls TEXT,
    status VARCHAR(50) DEFAULT '待领养',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS adoptions (
    id BIGSERIAL PRIMARY KEY,
    dog_id BIGINT,
    applicant_name VARCHAR(100),
    phone VARCHAR(50),
    address TEXT,
    housing_type VARCHAR(50),
    has_experience BOOLEAN DEFAULT FALSE,
    reason TEXT,
    status VARCHAR(50) DEFAULT '待审核',
    remark TEXT,
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    audit_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS donations (
    id BIGSERIAL PRIMARY KEY,
    donor_name VARCHAR(100),
    amount DECIMAL(10,2),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(200),
    status VARCHAR(50) DEFAULT '待支付',
    donate_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stories (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200),
    content TEXT,
    cover_image TEXT,
    author VARCHAR(100),
    publish_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) DEFAULT 'admin',
    last_login TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS volunteers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    phone VARCHAR(50),
    email VARCHAR(100),
    skills VARCHAR(200),
    available_time VARCHAR(200),
    status VARCHAR(50) DEFAULT '待审核',
    apply_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS lost_found (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50),
    title VARCHAR(200),
    description TEXT,
    location VARCHAR(200),
    contact_name VARCHAR(100),
    contact_phone VARCHAR(50),
    image_urls TEXT,
    status VARCHAR(50) DEFAULT '进行中',
    event_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 默认管理员账号 admin / 123456
INSERT INTO users (username, password_hash, role) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 'admin')
ON CONFLICT (username) DO NOTHING;
