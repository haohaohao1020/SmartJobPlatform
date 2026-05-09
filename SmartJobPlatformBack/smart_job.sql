-- ============================================================
-- SmartJobPlatform 招聘平台数据库初始化脚本
-- MySQL 8.0+
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_job DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_job;

-- ============================================================
-- 用户表
-- ============================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    birthday DATE COMMENT '生日',
    city VARCHAR(50) COMMENT '所在城市',
    role VARCHAR(20) NOT NULL DEFAULT 'JOBSEEKER' COMMENT '角色: JOBSEEKER求职者 HR企业 HR ADMIN管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除: 0未删除 1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 企业表
-- ============================================================
DROP TABLE IF EXISTS sys_company;
CREATE TABLE sys_company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '企业ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID(HR)',
    name VARCHAR(100) NOT NULL COMMENT '企业名称',
    short_name VARCHAR(50) COMMENT '企业简称',
    logo VARCHAR(255) COMMENT '企业LOGO',
    license_url VARCHAR(255) COMMENT '营业执照URL',
    industry VARCHAR(50) COMMENT '所属行业',
    scale VARCHAR(50) COMMENT '企业规模: 0-20人 20-99人 100-499人 500-999人 1000人以上',
    type VARCHAR(50) COMMENT '企业类型: 民营 国企 合资 外资 上市公司 事业单位',
    established_date VARCHAR(20) COMMENT '成立时间',
    registered_capital VARCHAR(50) COMMENT '注册资本',
    legal_person VARCHAR(50) COMMENT '法人代表',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区/县',
    address VARCHAR(255) COMMENT '详细地址',
    latitude DECIMAL(10, 7) COMMENT '纬度',
    longitude DECIMAL(10, 7) COMMENT '经度',
    description TEXT COMMENT '企业简介',
    benefits VARCHAR(500) COMMENT '福利待遇(JSON数组)',
    website VARCHAR(255) COMMENT '公司官网',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    job_count INT DEFAULT 0 COMMENT '岗位数量',
    audit_status TINYINT DEFAULT 0 COMMENT '审核状态: 0待审核 1审核通过 2审核拒绝',
    audit_remark VARCHAR(255) COMMENT '审核备注',
    audit_time DATETIME COMMENT '审核时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id),
    INDEX idx_audit_status (audit_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';

-- ============================================================
-- 岗位表
-- ============================================================
DROP TABLE IF EXISTS sys_job;
CREATE TABLE sys_job (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '岗位ID',
    hr_id BIGINT NOT NULL COMMENT 'HR用户ID',
    company_id BIGINT COMMENT '企业ID',
    company_name VARCHAR(100) COMMENT '企业名称',
    company_logo VARCHAR(255) COMMENT '企业LOGO',
    title VARCHAR(100) NOT NULL COMMENT '岗位名称',
    description TEXT COMMENT '岗位职责',
    requirement TEXT COMMENT '任职要求',
    keywords VARCHAR(500) COMMENT '关键词(逗号分隔)',
    industry VARCHAR(50) COMMENT '行业',
    category VARCHAR(50) COMMENT '岗位分类',
    sub_category VARCHAR(50) COMMENT '岗位子分类',
    province VARCHAR(50) COMMENT '省份',
    city VARCHAR(50) COMMENT '城市',
    district VARCHAR(50) COMMENT '区/县',
    address VARCHAR(255) COMMENT '详细地址',
    salary_min INT COMMENT '最低薪资(K)',
    salary_max INT COMMENT '最高薪资(K)',
    salary_type VARCHAR(20) DEFAULT '月' COMMENT '薪资类型: 月 年 日',
    salary_months INT DEFAULT 12 COMMENT '薪资发放月数',
    work_years VARCHAR(20) COMMENT '工作年限: 不限 1-3年 3-5年 5-10年 10年以上',
    education VARCHAR(20) COMMENT '学历要求: 不限 大专 本科 硕士 博士',
    job_type VARCHAR(20) DEFAULT '全职' COMMENT '工作类型: 全职 兼职 实习',
    tags VARCHAR(500) COMMENT '岗位标签(JSON数组)',
    emergency_degree VARCHAR(20) COMMENT '紧急程度',
    hr_name VARCHAR(50) COMMENT 'HR姓名',
    hr_avatar VARCHAR(255) COMMENT 'HR头像',
    hr_title VARCHAR(50) COMMENT 'HR职位',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    apply_count INT DEFAULT 0 COMMENT '投递次数',
    collect_count INT DEFAULT 0 COMMENT '收藏次数',
    is_hot TINYINT DEFAULT 0 COMMENT '是否热门: 0否 1是',
    hot_start_time DATETIME COMMENT '热门开始时间',
    hot_end_time DATETIME COMMENT '热门结束时间',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶: 0否 1是',
    top_start_time DATETIME COMMENT '置顶开始时间',
    top_end_time DATETIME COMMENT '置顶结束时间',
    audit_status TINYINT DEFAULT 1 COMMENT '审核状态: 0待审核 1审核通过 2审核拒绝',
    audit_remark VARCHAR(255) COMMENT '审核备注',
    publish_time DATETIME COMMENT '发布时间',
    expire_time DATETIME COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1上架 0下架',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_hr_id (hr_id),
    INDEX idx_company_id (company_id),
    INDEX idx_city (city),
    INDEX idx_salary_min (salary_min),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_audit_status (audit_status),
    FULLTEXT KEY ft_title_keywords (title, keywords, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位表';

-- ============================================================
-- 简历表
-- ============================================================
DROP TABLE IF EXISTS sys_resume;
CREATE TABLE sys_resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '简历ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
    birthday DATE COMMENT '生日',
    avatar VARCHAR(255) COMMENT '头像',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    current_city VARCHAR(50) COMMENT '当前城市',
    work_status VARCHAR(20) COMMENT '求职状态: 在职-考虑机会 在职-暂不考虑 离职-随时到岗 在校-寻找机会',
    work_years VARCHAR(20) COMMENT '工作年限',
    job_intention VARCHAR(100) COMMENT '求职意向',
    expected_salary_min INT COMMENT '期望最低薪资(K)',
    expected_salary_max INT COMMENT '期望最高薪资(K)',
    expected_city VARCHAR(50) COMMENT '期望城市',
    current_salary VARCHAR(50) COMMENT '当前薪资',
    self_evaluation TEXT COMMENT '自我评价',
    is_public TINYINT DEFAULT 1 COMMENT '是否公开: 1公开 0仅企业可见',
    refresh_time DATETIME COMMENT '刷新时间',
    view_count INT DEFAULT 0 COMMENT '被浏览次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历表';

-- ============================================================
-- 教育经历表
-- ============================================================
DROP TABLE IF EXISTS sys_resume_education;
CREATE TABLE sys_resume_education (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    school_name VARCHAR(100) COMMENT '学校名称',
    major VARCHAR(100) COMMENT '专业名称',
    education VARCHAR(20) COMMENT '学历: 高中 大专 本科 硕士 博士',
    start_date VARCHAR(20) COMMENT '开始时间',
    end_date VARCHAR(20) COMMENT '结束时间',
    is_current TINYINT DEFAULT 0 COMMENT '是否在读: 0否 1是',
    gpa VARCHAR(20) COMMENT 'GPA',
    description TEXT COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教育经历表';

-- ============================================================
-- 工作经历表
-- ============================================================
DROP TABLE IF EXISTS sys_resume_work_experience;
CREATE TABLE sys_resume_work_experience (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    company_name VARCHAR(100) COMMENT '公司名称',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(100) COMMENT '职位',
    start_date VARCHAR(20) COMMENT '开始时间',
    end_date VARCHAR(20) COMMENT '结束时间',
    is_current TINYINT DEFAULT 0 COMMENT '是否在职: 0否 1是',
    salary VARCHAR(50) COMMENT '薪资',
    work_description TEXT COMMENT '工作描述',
    achievements TEXT COMMENT '工作成就',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作经历表';

-- ============================================================
-- 项目经历表
-- ============================================================
DROP TABLE IF EXISTS sys_resume_project_experience;
CREATE TABLE sys_resume_project_experience (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    project_name VARCHAR(100) COMMENT '项目名称',
    role VARCHAR(50) COMMENT '担任角色',
    start_date VARCHAR(20) COMMENT '开始时间',
    end_date VARCHAR(20) COMMENT '结束时间',
    is_current TINYINT DEFAULT 0 COMMENT '是否进行中',
    project_link VARCHAR(255) COMMENT '项目链接',
    project_description TEXT COMMENT '项目描述',
    responsibilities TEXT COMMENT '职责描述',
    achievements TEXT COMMENT '项目成就',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目经历表';

-- ============================================================
-- 技能表
-- ============================================================
DROP TABLE IF EXISTS sys_resume_skill;
CREATE TABLE sys_resume_skill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    skill_name VARCHAR(50) COMMENT '技能名称',
    proficiency VARCHAR(20) COMMENT '熟练度: 入门 熟练 精通 专家',
    years INT COMMENT '使用年限',
    description VARCHAR(255) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能表';

-- ============================================================
-- 证书表
-- ============================================================
DROP TABLE IF EXISTS sys_resume_certificate;
CREATE TABLE sys_resume_certificate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    certificate_name VARCHAR(100) COMMENT '证书名称',
    issuer VARCHAR(100) COMMENT '发证机构',
    obtain_date VARCHAR(20) COMMENT '获得时间',
    expire_date VARCHAR(20) COMMENT '过期时间',
    is_permanent TINYINT DEFAULT 0 COMMENT '是否永久有效',
    certificate_url VARCHAR(255) COMMENT '证书图片',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_resume_id (resume_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书表';

-- ============================================================
-- 投递记录表
-- ============================================================
DROP TABLE IF EXISTS sys_application;
CREATE TABLE sys_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '求职者ID',
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    job_title VARCHAR(100) COMMENT '岗位名称',
    resume_id BIGINT COMMENT '简历ID',
    hr_id BIGINT COMMENT 'HR用户ID',
    hr_name VARCHAR(50) COMMENT 'HR姓名',
    company_id BIGINT COMMENT '企业ID',
    company_name VARCHAR(100) COMMENT '企业名称',
    company_logo VARCHAR(255) COMMENT '企业LOGO',
    salary_min INT COMMENT '最低薪资',
    salary_max INT COMMENT '最高薪资',
    city VARCHAR(50) COMMENT '城市',
    work_years VARCHAR(20) COMMENT '工作年限',
    education VARCHAR(20) COMMENT '学历',
    apply_time VARCHAR(50) COMMENT '投递时间',
    status TINYINT DEFAULT 1 COMMENT '状态: 1待查看 2待面试 3面试中 4待Offer 5已拒绝 6已录用',
    status_name VARCHAR(20) DEFAULT '待查看' COMMENT '状态名称',
    is_read TINYINT DEFAULT 0 COMMENT 'HR是否已读: 0未读 1已读',
    interview_time DATETIME COMMENT '面试时间',
    interview_address VARCHAR(255) COMMENT '面试地址',
    interview_contact VARCHAR(50) COMMENT '面试联系人',
    interview_phone VARCHAR(20) COMMENT '面试联系电话',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_job (user_id, job_id),
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id),
    INDEX idx_hr_id (hr_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='投递记录表';

-- ============================================================
-- 收藏表
-- ============================================================
DROP TABLE IF EXISTS sys_favorite;
CREATE TABLE sys_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    job_id BIGINT NOT NULL COMMENT '岗位ID',
    job_title VARCHAR(100) COMMENT '岗位名称',
    company_name VARCHAR(100) COMMENT '企业名称',
    company_logo VARCHAR(255) COMMENT '企业LOGO',
    salary_min INT COMMENT '最低薪资',
    salary_max INT COMMENT '最高薪资',
    city VARCHAR(50) COMMENT '城市',
    work_years VARCHAR(20) COMMENT '工作年限',
    education VARCHAR(20) COMMENT '学历',
    tags VARCHAR(500) COMMENT '岗位标签',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_user_job (user_id, job_id),
    INDEX idx_user_id (user_id),
    INDEX idx_job_id (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================================
-- 行业表
-- ============================================================
DROP TABLE IF EXISTS sys_industry;
CREATE TABLE sys_industry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    name VARCHAR(50) NOT NULL COMMENT '行业名称',
    code VARCHAR(50) COMMENT '行业代码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行业表';

-- ============================================================
-- 岗位分类表
-- ============================================================
DROP TABLE IF EXISTS sys_job_category;
CREATE TABLE sys_job_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) COMMENT '分类代码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位分类表';

-- ============================================================
-- 公告表
-- ============================================================
DROP TABLE IF EXISTS sys_announcement;
CREATE TABLE sys_announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    title VARCHAR(200) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type TINYINT DEFAULT 1 COMMENT '类型: 1系统公告 2招聘公告 3其他',
    type_name VARCHAR(20) DEFAULT '系统公告' COMMENT '类型名称',
    cover_image VARCHAR(255) COMMENT '封面图片',
    summary VARCHAR(500) COMMENT '摘要',
    author VARCHAR(50) COMMENT '作者',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶: 0否 1是',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    status TINYINT DEFAULT 1 COMMENT '状态: 1发布 0草稿',
    publish_time DATETIME COMMENT '发布时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_type (type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ============================================================
-- 系统配置表
-- ============================================================
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_group VARCHAR(50) DEFAULT 'system' COMMENT '配置分组: system基础 site站点 email邮件 payment支付 other其他',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型: string number boolean json',
    remark VARCHAR(255) COMMENT '备注',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员账号: admin / 123456
INSERT INTO sys_user (username, password, nickname, email, role, status) VALUES
('admin', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '系统管理员', 'admin@smartjob.com', 'ADMIN', 1);

-- HR账号: hr1 / 123456
INSERT INTO sys_user (username, password, nickname, email, phone, role, status) VALUES
('hr1', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', 'HR小明', 'hr1@test.com', '13900139001', 'HR', 1),
('hr2', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', 'HR小红', 'hr2@test.com', '13900139002', 'HR', 1),
('hr3', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', 'HR小刚', 'hr3@test.com', '13900139003', 'HR', 1),
('hr4', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', 'HR小李', 'hr4@test.com', '13900139004', 'HR', 1);

-- 求职者账号: jobseeker1 / 123456
INSERT INTO sys_user (username, password, nickname, email, phone, gender, city, role, status) VALUES
('jobseeker1', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '张三', 'zhangsan@test.com', '13800000001', 1, '北京', 'JOBSEEKER', 1),
('jobseeker2', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '李四', 'lisi@test.com', '13800000002', 2, '上海', 'JOBSEEKER', 1),
('jobseeker3', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '王五', 'wangwu@test.com', '13800000003', 1, '深圳', 'JOBSEEKER', 1),
('jobseeker4', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '赵六', 'zhaoliu@test.com', '13800000004', 1, '杭州', 'JOBSEEKER', 1),
('jobseeker5', '$2a$10$gj5CoGKmK.IOe8y/HTDwp.Lsvlgm3dKPgDp4nkHFSX7sQ7ul3eX4q', '钱七', 'qianqi@test.com', '13800000005', 2, '广州', 'JOBSEEKER', 1);

-- 企业数据
INSERT INTO sys_company (user_id, name, short_name, logo, industry, scale, type, established_date, registered_capital, legal_person, contact_name, contact_phone, contact_email, province, city, district, address, description, benefits, website, view_count, job_count, audit_status, status) VALUES
(2, '阿里巴巴集团', '阿里', '/uploads/logos/alibaba.png', '互联网', '1000人以上', '上市公司', '1999-09-09', '500000万', '马云', 'HR小明', '0571-88888888', 'hr@alibaba.com', '浙江省', '杭州', '余杭区', '文一西路969号阿里巴巴西溪园区', '阿里巴巴集团是全球领先的电子商务公司，业务涵盖电商、云计算、物流、金融等多个领域。我们的使命是"让天下没有难做的生意"。', '["五险一金","年终奖","带薪年假","节日福利","定期体检","股票期权","弹性工作"]', 'https://www.alibaba.com', 5280, 156, 1, 1),
(3, '腾讯科技(深圳)有限公司', '腾讯', '/uploads/logos/tencent.png', '互联网', '1000人以上', '上市公司', '1998-11-11', '650000万', '马化腾', 'HR小红', '0755-88888888', 'hr@tencent.com', '广东省', '深圳', '南山区', '科技园腾讯大厦', '腾讯是中国最大的互联网综合服务提供商之一，也是中国服务用户最多的互联网企业之一。业务覆盖社交、游戏、金融、云计算等。', '["五险一金","年终奖14薪","带薪年假15天","节日福利","免费三餐","班车","健身房"]', 'https://www.tencent.com', 4890, 203, 1, 1),
(4, '字节跳动科技有限公司', '字节跳动', '/uploads/logos/bytedance.png', '互联网', '1000人以上', '民营', '2012-03-12', '100000万', '张一鸣', 'HR小刚', '010-88888888', 'hr@bytedance.com', '北京市', '北京', '海淀区', '知春路甲48号', '字节跳动是全球领先的信息和内容平台，旗下拥有抖音、今日头条、TikTok等知名产品。我们致力于用科技连接人和信息。', '["五险一金","丰厚年终奖","弹性工作","免费三餐","下午茶","健身房","股票期权"]', 'https://www.bytedance.com', 3560, 178, 1, 1),
(5, '美团点评', '美团', '/uploads/logos/meituan.png', '互联网', '1000人以上', '上市公司', '2003-04-01', '50000万', '王兴', 'HR小李', '010-88888888', 'hr@meituan.com', '北京市', '北京', '朝阳区', '望京街10号', '美团是中国领先的生活服务电子商务平台，业务覆盖餐饮外卖、酒店旅游、休闲娱乐等多个领域。', '["五险一金","年终奖","带薪年假","员工旅游","节日福利","定期团建"]', 'https://www.meituan.com', 2890, 145, 1, 1);

-- 岗位数据（大量测试数据）
INSERT INTO sys_job (hr_id, company_id, company_name, company_logo, title, description, requirement, keywords, industry, category, sub_category, city, salary_min, salary_max, salary_months, work_years, education, job_type, tags, hr_name, is_hot, is_top, audit_status, status, publish_time) VALUES
-- 阿里巴巴 岗位
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '高级Java开发工程师', '1. 负责电商平台核心模块的设计与开发\n2. 参与系统架构设计，优化系统性能\n3. 解决复杂的技术难题，保证系统稳定运行\n4. 与产品、测试团队紧密配合，确保高质量交付', '1. 本科及以上学历，计算机相关专业\n2. 5年以上Java开发经验\n3. 精通Spring Boot、Spring Cloud等微服务框架\n4. 熟悉MySQL、Redis等常用存储\n5. 有大型分布式系统开发经验优先', 'Java,Spring Boot,微服务,分布式,电商', '互联网', '技术', '后端开发', '杭州', 25, 45, 16, '5-10年', '本科', '全职', '["五险一金","股票期权","年终奖","弹性工作"]', 'HR小明', 1, 1, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '前端开发工程师', '1. 负责电商平台前端页面的开发\n2. 与后端团队协作完成接口对接\n3. 优化用户体验和页面性能\n4. 参与前端技术选型和架构设计', '1. 本科及以上学历\n2. 3年以上前端开发经验\n3. 精通Vue/React等主流框架\n4. 熟悉Webpack、Node.js等工具\n5. 有移动端适配经验优先', 'Vue,React,JavaScript,TypeScript,Webpack', '互联网', '技术', '前端开发', '杭州', 18, 30, 16, '3-5年', '本科', '全职', '["五险一金","股票期权","年终奖","弹性工作"]', 'HR小明', 1, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '高级产品经理', '1. 负责电商平台核心功能的产品规划\n2. 进行市场调研和用户需求分析\n3. 与开发团队协作推进产品迭代\n4. 跟踪产品数据，持续优化产品体验', '1. 本科及以上学历\n2. 5年以上产品经理经验\n3. 有电商平台产品经验优先\n4. 具备优秀的沟通协调能力\n5. 熟悉Axure、XMind等产品工具', '产品规划,用户研究,需求分析,数据分析', '互联网', '产品', '产品经理', '杭州', 20, 35, 16, '5-10年', '本科', '全职', '["五险一金","股票期权","年终奖"]', 'HR小明', 0, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '高级UI设计师', '1. 负责电商平台的界面设计\n2. 制定设计规范和组件库\n3. 与产品、前端团队紧密协作\n4. 跟踪设计趋势，持续优化视觉体验', '1. 本科及以上学历，设计相关专业\n2. 4年以上UI设计经验\n3. 精通Figma、Sketch等设计工具\n4. 有完整的移动端设计作品\n5. 对用户体验有深入理解', 'UI设计,Figma,Sketch,设计规范,交互设计', '互联网', '设计', 'UI设计', '杭州', 15, 25, 16, '3-5年', '本科', '全职', '["五险一金","股票期权","年终奖","设计沙龙"]', 'HR小明', 0, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '大数据开发工程师', '1. 负责大数据平台的建设和维护\n2. 设计和实现数据处理流程\n3. 优化数据存储和查询性能\n4. 支持业务数据分析需求', '1. 本科及以上学历\n2. 3年以上大数据开发经验\n3. 精通Hadoop、Spark、Hive等\n4. 熟悉Kafka、Flink等流式计算\n5. 有数据仓库建设经验优先', 'Hadoop,Spark,Hive,Kafka,Flink,数据仓库', '互联网', '技术', '大数据', '杭州', 22, 40, 16, '3-5年', '本科', '全职', '["五险一金","股票期权","年终奖","弹性工作"]', 'HR小明', 1, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '测试开发工程师', '1. 负责产品的自动化测试体系建设\n2. 开发测试工具和测试平台\n3. 参与代码评审，保证代码质量\n4. 分析测试数据，提出质量改进建议', '1. 本科及以上学历\n2. 3年以上测试开发经验\n3. 熟悉自动化测试框架\n4. 掌握至少一门编程语言\n5. 有测试平台开发经验优先', '自动化测试,测试开发,Selenium,JUnit,Java/Python', '互联网', '技术', '测试开发', '杭州', 18, 32, 16, '3-5年', '本科', '全职', '["五险一金","股票期权","年终奖"]', 'HR小明', 0, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '运营专家', '1. 负责电商平台的用户运营\n2. 策划和执行运营活动\n3. 分析运营数据，优化运营策略\n4. 与其他部门协作，提升用户活跃度', '1. 本科及以上学历\n2. 3年以上互联网运营经验\n3. 有电商平台运营经验优先\n4. 熟悉数据分析方法\n5. 具备优秀的沟通协调能力', '用户运营,活动策划,数据分析,增长黑客', '互联网', '运营', '用户运营', '杭州', 15, 25, 16, '3-5年', '本科', '全职', '["五险一金","股票期权","年终奖"]', 'HR小明', 0, 0, 1, 1, NOW()),

-- 腾讯 岗位
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '高级Android开发工程师', '1. 负责QQ/微信相关产品的Android开发\n2. 参与技术方案设计和评审\n3. 优化移动端性能和用户体验\n4. 解决复杂技术问题', '1. 本科及以上学历\n2. 4年以上Android开发经验\n3. 精通Java/Kotlin\n4. 熟悉Android系统原理和性能优化\n5. 有大型APP开发经验优先', 'Android,Kotlin,Java,性能优化,移动端', '互联网', '技术', '移动开发', '深圳', 22, 38, 14, '3-5年', '本科', '全职', '["五险一金","14薪","免费三餐","班车"]', 'HR小红', 1, 1, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', 'iOS开发工程师', '1. 负责QQ/微信相关产品的iOS开发\n2. 参与技术方案设计\n3. 优化iOS应用性能\n4. 与产品、设计团队协作', '1. 本科及以上学历\n2. 3年以上iOS开发经验\n3. 精通Swift/Objective-C\n4. 熟悉iOS系统原理\n5. 有完整iOS项目经验', 'iOS,Swift,Objective-C,MVC,MVVM', '互联网', '技术', '移动开发', '深圳', 20, 35, 14, '3-5年', '本科', '全职', '["五险一金","14薪","免费三餐","班车"]', 'HR小红', 1, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '游戏开发工程师', '1. 负责游戏核心功能开发\n2. 参与游戏引擎优化\n3. 与策划、美术团队协作\n4. 解决游戏性能问题', '1. 本科及以上学历\n2. 2年以上游戏开发经验\n3. 精通C#/C++\n4. 熟悉Unity/Unreal Engine\n5. 对游戏开发有热情', 'Unity,C#,C++,游戏开发,3D渲染', '互联网', '技术', '游戏开发', '深圳', 20, 40, 14, '3-5年', '本科', '全职', '["五险一金","14薪","免费三餐","健身房"]', 'HR小红', 1, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '机器学习算法工程师', '1. 负责推荐算法的研究与实现\n2. 优化推荐系统效果\n3. 与业务团队协作，解决实际问题\n4. 跟踪前沿技术', '1. 硕士及以上学历\n2. 3年以上算法经验\n3. 精通机器学习、深度学习\n4. 熟悉TensorFlow/PyTorch\n5. 有推荐系统经验优先', '机器学习,深度学习,推荐算法,Python,TensorFlow', '互联网', '技术', '算法', '深圳', 30, 50, 14, '3-5年', '硕士', '全职', '["五险一金","14薪","免费三餐","技术分享"]', 'HR小红', 1, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', 'NLP算法工程师', '1. 负责NLP相关算法的研发\n2. 应用NLP技术解决业务问题\n3. 优化现有算法效果\n4. 跟踪学术前沿', '1. 硕士及以上学历\n2. 2年以上NLP经验\n3. 精通Python、机器学习\n4. 熟悉Transformer、BERT等模型\n5. 有NLU/NLG经验优先', 'NLP,BERT,Transformer,PyTorch,语义理解', '互联网', '技术', '算法', '深圳', 28, 45, 14, '1-3年', '硕士', '全职', '["五险一金","14薪","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '全栈开发工程师', '1. 负责内部管理系统的全栈开发\n2. 设计和实现Web应用\n3. 与产品团队协作\n4. 优化系统性能', '1. 本科及以上学历\n2. 3年以上全栈经验\n3. 精通前后端技术栈\n4. 熟悉Node.js\n5. 有大型Web系统经验', '全栈,React,Node.js,TypeScript,GraphQL', '互联网', '技术', '全栈开发', '深圳', 20, 35, 14, '3-5年', '本科', '全职', '["五险一金","14薪","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '品牌设计师', '1. 负责品牌视觉设计\n2. 设计品牌宣传物料\n3. 维护品牌视觉规范\n4. 与市场团队协作', '1. 本科及以上学历，设计相关专业\n2. 4年以上品牌设计经验\n3. 精通设计软件\n4. 有完整品牌设计案例\n5. 创意思维能力强', '品牌设计,VI设计,平面设计,Photoshop,Illustrator', '互联网', '设计', '品牌设计', '深圳', 18, 30, 14, '3-5年', '本科', '全职', '["五险一金","14薪","免费三餐","设计沙龙"]', 'HR小红', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '内容运营专员', '1. 负责社区内容运营\n2. 策划和执行内容活动\n3. 维护优质内容生态\n4. 分析内容数据', '1. 本科及以上学历\n2. 2年以上内容运营经验\n3. 熟悉社交媒体平台\n4. 优秀的文字表达能力\n5. 对内容有敏感度', '内容运营,社区运营,文案,活动策划', '互联网', '运营', '内容运营', '深圳', 12, 20, 14, '1-3年', '本科', '全职', '["五险一金","14薪","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),

-- 字节跳动 岗位
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', 'Android架构师', '1. 负责抖音Android端架构设计\n2. 主导核心模块重构\n3. 制定技术规范\n4. 培养团队成员', '1. 本科及以上学历\n2. 6年以上Android经验\n3. 有架构设计经验\n4. 精通性能优化\n5. 有大型APP经验', 'Android,架构设计,Kotlin,性能优化,JNI', '互联网', '技术', '架构师', '北京', 40, 60, 15, '5-10年', '本科', '全职', '["五险一金","丰厚奖金","弹性工作","免费三餐"]', 'HR小刚', 1, 1, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '后端技术专家', '1. 负责抖音后端系统设计\n2. 解决高并发、高可用问题\n3. 参与技术选型\n4. 推动技术创新', '1. 本科及以上学历\n2. 6年以上后端经验\n3. 精通Go/Java\n4. 有分布式系统设计经验\n5. 对技术有热情', 'Go,分布式系统,微服务,高并发,Kubernetes', '互联网', '技术', '架构师', '北京', 40, 60, 15, '5-10年', '本科', '全职', '["五险一金","丰厚奖金","弹性工作","免费三餐"]', 'HR小刚', 1, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '资深前端工程师', '1. 负责抖音Web端开发\n2. 参与前端架构设计\n3. 优化页面性能\n4. 技术创新', '1. 本科及以上学历\n2. 5年以上前端经验\n3. 精通React/Vue\n4. 有前端架构经验\n5. 对性能优化有深入研究', 'React,Vue,TypeScript,Webpack,性能优化', '互联网', '技术', '前端开发', '北京', 25, 40, 15, '3-5年', '本科', '全职', '["五险一金","丰厚奖金","弹性工作","免费三餐"]', 'HR小刚', 1, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '推荐算法工程师', '1. 负责抖音推荐算法\n2. 优化推荐效果\n3. 算法创新\n4. 支持业务需求', '1. 硕士及以上学历\n2. 3年以上推荐算法经验\n3. 精通机器学习\n4. 熟悉Spark/Flink\n5. 有推荐系统经验', '推荐算法,机器学习,Spark,CTR预估,深度学习', '互联网', '技术', '算法', '北京', 35, 55, 15, '3-5年', '硕士', '全职', '["五险一金","丰厚奖金","弹性工作","技术分享"]', 'HR小刚', 1, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '计算机视觉工程师', '1. 负责视频内容理解\n2. 开发CV相关算法\n3. 优化算法效果和性能\n4. 支持产品需求', '1. 硕士及以上学历\n2. 3年以上CV经验\n3. 精通深度学习\n4. 熟悉OpenCV、PyTorch\n5. 有视频分析经验', '计算机视觉,深度学习,OpenCV,PyTorch,目标检测', '互联网', '技术', '算法', '北京', 35, 55, 15, '3-5年', '硕士', '全职', '["五险一金","丰厚奖金","弹性工作","技术分享"]', 'HR小刚', 0, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '数据产品经理', '1. 负责数据产品规划\n2. 与技术团队协作\n3. 分析用户需求\n4. 迭代数据产品', '1. 本科及以上学历\n2. 3年以上数据产品经验\n3. 熟悉数据仓库\n4. 有数据分析能力\n5. 有BI产品经验优先', '数据产品,数据仓库,BI,数据分析,SQL', '互联网', '产品', '数据产品', '北京', 20, 35, 15, '3-5年', '本科', '全职', '["五险一金","丰厚奖金","弹性工作"]', 'HR小刚', 0, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '内容审核专员', '1. 负责视频内容审核\n2. 执行审核标准\n3. 及时反馈问题\n4. 配合团队优化流程', '1. 大专及以上学历\n2. 1年以上审核经验优先\n3. 熟悉互联网内容\n4. 有责任心，细致认真\n5. 能接受排班', '内容审核,视频审核,风险防控', '互联网', '运营', '审核', '北京', 6, 10, 12, '1-3年', '大专', '全职', '["五险一金","餐补","交通补贴"]', 'HR小刚', 0, 0, 1, 1, NOW()),

-- 美团 岗位
(5, 4, '美团点评', '/uploads/logos/meituan.png', '高级Java开发工程师', '1. 负责外卖平台后端开发\n2. 设计高可用系统\n3. 优化系统性能\n4. 支持业务快速迭代', '1. 本科及以上学历\n2. 4年以上Java经验\n3. 精通Spring全家桶\n4. 有高并发经验\n5. 熟悉微服务架构', 'Java,Spring Boot,微服务,Redis,MySQL', '互联网', '技术', '后端开发', '北京', 22, 38, 15, '3-5年', '本科', '全职', '["五险一金","年终奖","弹性工作"]', 'HR小李', 1, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '地图导航工程师', '1. 负责地图导航功能开发\n2. 优化路径规划算法\n3. 提升导航体验\n4. 支持外卖配送', '1. 本科及以上学历\n2. 3年以上导航/地图经验\n3. 熟悉地理信息系统\n4. 有路径规划经验\n5. 掌握C++/Java', '地图导航,GIS,路径规划,地理信息,空间数据', '互联网', '技术', '地图开发', '北京', 25, 40, 15, '3-5年', '本科', '全职', '["五险一金","年终奖","弹性工作"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '搜索算法工程师', '1. 负责搜索算法研发\n2. 优化搜索体验\n3. 提升搜索相关性\n4. 支持业务需求', '1. 硕士及以上学历\n2. 2年以上搜索经验\n3. 熟悉搜索引擎原理\n4. 掌握机器学习\n5. 有ES/Lucene经验', '搜索算法,Elasticsearch,Lucene,相关性,Query理解', '互联网', '技术', '算法', '北京', 28, 45, 15, '1-3年', '硕士', '全职', '["五险一金","年终奖","弹性工作"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '区域运营经理', '1. 负责指定区域的运营管理\n2. 达成业务目标\n3. 维护商家关系\n4. 优化运营策略', '1. 本科及以上学历\n2. 3年以上本地生活运营经验\n3. 有团队管理经验\n4. 优秀的沟通能力\n5. 对O2O行业了解', '区域运营,商家运营,本地生活,O2O,团队管理', '互联网', '运营', '运营管理', '北京', 15, 25, 15, '3-5年', '本科', '全职', '["五险一金","年终奖","绩效奖金"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '商务拓展经理', '1. 负责新商家拓展\n2. 维护现有商家关系\n3. 达成招商目标\n4. 收集市场信息', '1. 本科及以上学历\n2. 2年以上BD经验\n3. 有餐饮行业资源优先\n4. 优秀的谈判能力\n5. 能接受出差', '商务拓展,BD,招商,客户关系,销售', '互联网', '市场', '商务拓展', '北京', 12, 20, 15, '1-3年', '本科', '全职', '["五险一金","高提成","绩效奖金"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '人力资源专员', '1. 负责招聘、培训等HR工作\n2. 协助制定HR政策\n3. 维护员工关系\n4. 支持业务部门', '1. 本科及以上学历，人力资源相关专业\n2. 2年以上HR经验\n3. 熟悉劳动法律法规\n4. 优秀的沟通能力\n5. 细心、有责任心', 'HR,招聘,培训,员工关系,绩效', '互联网', '人事', 'HR专员', '北京', 10, 18, 15, '1-3年', '本科', '全职', '["五险一金","年终奖","员工福利"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '财务分析师', '1. 负责财务分析工作\n2. 编制财务报表\n3. 协助预算编制\n4. 支持业务决策', '1. 本科及以上学历，财务相关专业\n2. 2年以上财务分析经验\n3. 熟悉财务软件\n4. 持有CPA/CFA优先\n5. 细心、严谨', '财务分析,报表,预算,Excel,CPA', '互联网', '财务', '财务分析', '北京', 12, 22, 15, '1-3年', '本科', '全职', '["五险一金","年终奖","定期培训"]', 'HR小李', 0, 0, 1, 1, NOW()),

-- 更多城市的岗位
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', 'Java开发工程师(北京)', '1. 负责北京区域的技术项目\n2. 参与核心业务开发\n3. 与团队紧密协作', '1. 本科及以上学历\n2. 2年以上Java经验\n3. 熟悉Spring全家桶\n4. 有Web开发经验', 'Java,Spring,Web开发,MySQL', '互联网', '技术', '后端开发', '北京', 18, 30, 16, '1-3年', '本科', '全职', '["五险一金","年终奖","弹性工作"]', 'HR小明', 0, 0, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '前端开发工程师(上海)', '1. 负责上海区域的前端项目\n2. 参与产品开发\n3. 优化用户体验', '1. 本科及以上学历\n2. 2年以上前端经验\n3. 熟悉React/Vue\n4. 掌握CSS/JS', 'React,Vue,JavaScript,CSS,HTML', '互联网', '技术', '前端开发', '上海', 16, 28, 16, '1-3年', '本科', '全职', '["五险一金","年终奖","弹性工作"]', 'HR小明', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '产品经理(北京)', '1. 负责北京区域的产品\n2. 产品规划和迭代\n3. 与开发团队协作', '1. 本科及以上学历\n2. 2年以上产品经验\n3. 有互联网产品经验\n4. 熟悉产品工具', '产品经理,需求分析,原型设计,Axure', '互联网', '产品', '产品经理', '北京', 18, 32, 14, '1-3年', '本科', '全职', '["五险一金","14薪","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', 'UI设计师(上海)', '1. 负责界面设计\n2. 与产品团队协作\n3. 维护设计规范', '1. 本科及以上学历，设计相关专业\n2. 2年以上UI设计经验\n3. 精通设计工具\n4. 有设计作品集', 'UI设计,Figma,Sketch,设计规范', '互联网', '设计', 'UI设计', '上海', 14, 22, 14, '1-3年', '本科', '全职', '["五险一金","14薪","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '后端工程师(上海)', '1. 负责后端服务开发\n2. 参与系统设计\n3. 保证系统稳定', '1. 本科及以上学历\n2. 3年以上后端经验\n3. 精通Go/Java\n4. 有分布式经验', 'Go,Java,后端,分布式,微服务', '互联网', '技术', '后端开发', '上海', 20, 35, 15, '3-5年', '本科', '全职', '["五险一金","弹性工作","免费三餐"]', 'HR小刚', 0, 0, 1, 1, NOW()),
(4, 3, '字节跳动科技有限公司', '/uploads/logos/bytedance.png', '前端工程师(深圳)', '1. 负责Web前端开发\n2. 参与产品迭代\n3. 优化性能', '1. 本科及以上学历\n2. 2年以上前端经验\n3. 熟悉React\n4. 掌握TS', 'React,TypeScript,前端,Web开发', '互联网', '技术', '前端开发', '深圳', 18, 30, 15, '1-3年', '本科', '全职', '["五险一金","弹性工作","免费三餐"]', 'HR小刚', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '运营经理(上海)', '1. 负责上海区域运营\n2. 制定运营策略\n3. 达成业务目标', '1. 本科及以上学历\n2. 3年以上运营经验\n3. 有O2O经验优先\n4. 数据分析能力强', '运营,数据,策略,O2O,本地生活', '互联网', '运营', '运营管理', '上海', 15, 25, 15, '3-5年', '本科', '全职', '["五险一金","年终奖","绩效奖金"]', 'HR小李', 0, 0, 1, 1, NOW()),
(5, 4, '美团点评', '/uploads/logos/meituan.png', '产品经理(深圳)', '1. 负责深圳区域产品\n2. 产品规划\n3. 推动产品落地', '1. 本科及以上学历\n2. 2年以上产品经验\n3. 有本地生活经验优先\n4. 执行力强', '产品经理,需求分析,本地生活', '互联网', '产品', '产品经理', '深圳', 16, 28, 15, '1-3年', '本科', '全职', '["五险一金","年终奖","绩效奖金"]', 'HR小李', 0, 0, 1, 1, NOW()),

-- 不同薪资档位的岗位
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '技术总监', '1. 负责技术团队管理\n2. 制定技术战略\n3. 带领团队完成目标\n4. 培养团队成员', '1. 本科及以上学历\n2. 8年以上技术经验\n3. 5年以上团队管理经验\n4. 有大型项目经验\n5. 有架构设计能力', '技术管理,架构设计,团队管理,战略规划', '互联网', '技术', 'CTO/技术总监', '杭州', 50, 80, 16, '10年以上', '本科', '全职', '["五险一金","股票期权","丰厚年终奖","弹性工作"]', 'HR小明', 1, 1, 1, 1, NOW()),
(2, 1, '阿里巴巴集团', '/uploads/logos/alibaba.png', '技术主管', '1. 负责技术小组管理\n2. 技术方案设计\n3. 协调开发进度\n4. 保证交付质量', '1. 本科及以上学历\n2. 5年以上技术经验\n3. 3年以上管理经验\n4. 有后端经验\n5. 责任心强', '技术主管,团队管理,Java,后端', '互联网', '技术', '技术主管', '杭州', 35, 55, 16, '5-10年', '本科', '全职', '["五险一金","股票期权","年终奖","弹性工作"]', 'HR小明', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '实习后端工程师', '1. 参与后端开发\n2. 学习技术栈\n3. 协助完成任务', '1. 本科及以上在读学生\n2. 熟悉Java/Python\n3. 对技术有热情\n4. 能实习3个月以上', '实习,Java,Python,后端', '互联网', '技术', '后端开发', '深圳', 4, 6, 12, '不限', '本科', '实习', '["实习补贴","转正机会","导师指导","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW()),
(3, 2, '腾讯科技(深圳)有限公司', '/uploads/logos/tencent.png', '实习UI设计师', '1. 参与UI设计\n2. 协助制作设计稿\n3. 学习设计规范', '1. 设计相关专业在读\n2. 熟悉设计工具\n3. 有设计作品\n4. 能实习3个月以上', '实习,UI设计,设计,Figma', '互联网', '设计', 'UI设计', '深圳', 4, 6, 12, '不限', '本科', '实习', '["实习补贴","转正机会","导师指导","免费三餐"]', 'HR小红', 0, 0, 1, 1, NOW());

-- 公告数据
INSERT INTO sys_announcement (title, content, type, type_name, author, is_top, view_count, status, publish_time) VALUES
('欢迎使用SmartJob招聘平台', '欢迎使用SmartJob招聘平台！这是一个专业的招聘服务平台，为求职者和企业HR提供便捷的求职招聘服务。', 1, '系统公告', '系统管理员', 1, 1250, 1, NOW()),
('平台功能升级公告', '平台功能升级说明：\n1. 新增岗位智能推荐功能\n2. 优化简历编辑体验\n3. 新增企业信用评级展示\n4. 支持更多城市筛选', 1, '系统公告', '系统管理员', 1, 890, 1, NOW()),
('春季招聘会通知', 'SmartJob春季招聘会将于2024年3月举办，届时将有超过100家知名企业参与，提供5000+岗位机会。详情请关注后续通知。', 2, '招聘公告', '运营团队', 0, 2350, 1, NOW()),
('用户隐私政策更新说明', '为更好地保护用户隐私，我们对用户隐私政策进行了更新。请登录账号查看详细内容。如有疑问，请联系客服。', 1, '系统公告', '法务团队', 0, 560, 1, NOW()),
('新春招聘季活动', '新春招聘季活动火热进行中！企业发布岗位享受8折优惠，求职者投递简历有机会赢取iPhone大奖！', 2, '招聘公告', '运营团队', 0, 3200, 1, NOW());

-- 系统配置数据
INSERT INTO sys_config (config_name, config_key, config_value, config_group, config_type, remark, sort, status) VALUES
('站点名称', 'site.name', 'SmartJob招聘平台', 'site', 'string', '站点名称', 1, 1),
('站点描述', 'site.description', '专业的互联网招聘平台', 'site', 'string', '站点描述', 2, 1),
('站点Logo', 'site.logo', '/uploads/logo.png', 'site', 'string', '站点Logo', 3, 1),
('站点域名', 'site.domain', 'http://localhost:8080', 'site', 'string', '站点域名', 4, 1),
('备案号', 'site.icp', '京ICP备12345678号', 'site', 'string', 'ICP备案号', 5, 1),
('客服电话', 'site.phone', '400-123-4567', 'site', 'string', '客服电话', 6, 1),
('客服邮箱', 'site.email', 'service@smartjob.com', 'site', 'string', '客服邮箱', 7, 1),
('工作时间', 'site.work_time', '周一至周五 9:00-18:00', 'site', 'string', '工作时间', 8, 1),
('是否开放注册', 'site.register_open', 'true', 'site', 'boolean', '是否开放用户注册', 9, 1),
('是否开启维护', 'site.maintenance', 'false', 'site', 'boolean', '是否开启维护模式', 10, 1),
('JWT密钥', 'jwt.secret', 'SmartJobPlatformSecretKey2024', 'system', 'string', 'JWT加密密钥', 1, 1),
('JWT过期时间', 'jwt.expiration', '86400000', 'system', 'number', 'JWT过期时间(毫秒)', 2, 1),
('文件存储路径', 'upload.path', '/uploads', 'system', 'string', '文件存储路径', 1, 1),
('允许的文件类型', 'upload.allowed_types', 'jpg,jpeg,png,gif,pdf,doc,docx', 'system', 'string', '允许的文件类型', 2, 1),
('最大文件大小', 'upload.max_size', '10485760', 'system', 'number', '最大文件大小(字节)', 3, 1),
('默认分页大小', 'page.default_size', '10', 'system', 'number', '默认分页大小', 1, 1),
('最大分页大小', 'page.max_size', '100', 'system', 'number', '最大分页大小', 2, 1);

-- 行业数据
INSERT INTO sys_industry (name, code, parent_id, sort, status) VALUES
('互联网', 'internet', 0, 1, 1),
('计算机软件', 'software', 0, 2, 1),
('电子商务', 'ecommerce', 0, 3, 1),
('金融', 'finance', 0, 4, 1),
('教育', 'education', 0, 5, 1),
('医疗健康', 'healthcare', 0, 6, 1),
('房地产', 'realestate', 0, 7, 1),
('制造业', 'manufacturing', 0, 8, 1),
('零售', 'retail', 0, 9, 1),
('物流', 'logistics', 0, 10, 1),
('游戏', 'game', 1, 1, 1),
('社交', 'social', 1, 2, 1),
('云计算', 'cloud', 1, 3, 1);

-- 岗位分类数据
INSERT INTO sys_job_category (name, code, parent_id, sort, status) VALUES
('技术', 'tech', 0, 1, 1),
('产品', 'product', 0, 2, 1),
('设计', 'design', 0, 3, 1),
('运营', 'operation', 0, 4, 1),
('市场', 'marketing', 0, 5, 1),
('人事', 'hr', 0, 6, 1),
('财务', 'finance', 0, 7, 1),
('其他', 'other', 0, 8, 1),
('后端开发', 'backend', 1, 1, 1),
('前端开发', 'frontend', 1, 2, 1),
('移动开发', 'mobile', 1, 3, 1),
('测试开发', 'test', 1, 4, 1),
('大数据', 'bigdata', 1, 5, 1),
('算法', 'algorithm', 1, 6, 1),
('架构师', 'architect', 1, 7, 1),
('游戏开发', 'game', 1, 8, 1),
('全栈开发', 'fullstack', 1, 9, 1),
('地图开发', 'map', 1, 10, 1),
('产品经理', 'pm', 2, 1, 1),
('数据产品', 'data_pm', 2, 2, 1),
('UI设计', 'ui', 3, 1, 1),
('UX设计', 'ux', 3, 2, 1),
('品牌设计', 'brand', 3, 3, 1),
('平面设计', 'graphic', 3, 4, 1),
('用户运营', 'user_op', 4, 1, 1),
('内容运营', 'content_op', 4, 2, 1),
('运营管理', 'op_manager', 4, 3, 1),
('审核', 'audit', 4, 4, 1),
('商务拓展', 'bd', 5, 1, 1),
('HR专员', 'hr_spec', 6, 1, 1),
('财务分析', 'fin_analyst', 7, 1, 1);

-- ============================================================
-- 数据库初始化完成
-- 默认账号密码:
-- 管理员: admin / 123456
-- HR: hr1 / 123456, hr2 / 123456, hr3 / 123456, hr4 / 123456
-- 求职者: jobseeker1 / 123456, jobseeker2 / 123456 等
-- ============================================================