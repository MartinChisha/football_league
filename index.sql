-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('team_manager', 'referee', 'super_admin', 'league_admin')),
    phone_number VARCHAR(20),
    date_of_birth DATE,
    user_status VARCHAR(20) DEFAULT 'active' CHECK (user_status IN ('active', 'suspended', 'inactive', 'locked')),
    failed_login_attempts INT DEFAULT 0,
    last_login_at TIMESTAMP,
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Super admins table
CREATE TABLE IF NOT EXISTS super_admins (
    user_id UUID PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    system_permissions JSONB,
    can_impersonate BOOLEAN DEFAULT FALSE,
    system_access_level VARCHAR(20) DEFAULT 'full' CHECK (system_access_level IN ('full', 'audit_only')),
    emergency_contact VARCHAR(255),
    assigned_by UUID REFERENCES users(user_id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- Leagues table
CREATE TABLE IF NOT EXISTS leagues (
    league_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_name VARCHAR(255) NOT NULL,
    league_code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    country_code VARCHAR(3) NOT NULL,
    region VARCHAR(100),
    league_type VARCHAR(20) DEFAULT 'amateur',
    overall_structure VARCHAR(20) DEFAULT 'flat',
    status VARCHAR(20) DEFAULT 'active',
    founded_year INT,
    logo_url VARCHAR(500),
    website VARCHAR(500),
    contact_email VARCHAR(255),
    global_config JSONB,
    created_by UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- League admins table
CREATE TABLE IF NOT EXISTS league_admins (
    user_id UUID PRIMARY KEY,
    league_id UUID NOT NULL,
    admin_permissions JSONB,
    can_manage_referees BOOLEAN DEFAULT TRUE,
    can_manage_teams BOOLEAN DEFAULT TRUE,
    can_schedule_fixtures BOOLEAN DEFAULT TRUE,
    financial_access_level VARCHAR(10) DEFAULT 'none',
    assigned_by UUID,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(user_id)
);
ALTER TABLE team.teams ADD COLUMN division_id UUID;
-- Optional: add a foreign key for data integrity (but it will point to league.divisions)
ALTER TABLE team.teams ADD CONSTRAINT fk_team_division
    FOREIGN KEY (division_id) REFERENCES league.divisions(division_id) ON DELETE SET NULL;
-- Create enum for manager type and status
CREATE TYPE team_manager_type AS ENUM ('head_manager', 'assistant_manager');
CREATE TYPE team_manager_status AS ENUM ('pending', 'active', 'rejected');
;
-- Drop the custom enum type if it exists
DROP TYPE IF EXISTS division_status CASCADE;


CREATE TABLE IF NOT EXISTS league.divisions (
    division_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    league_id UUID NOT NULL,
    parent_division_id UUID,
    division_name VARCHAR(255) NOT NULL,
    division_code VARCHAR(50) NOT NULL,
    division_level INT DEFAULT 1,
    description TEXT,
    promotion_spots INT DEFAULT 0,
    relegation_spots INT DEFAULT 0,
    max_teams INT DEFAULT 20,
    min_teams INT DEFAULT 10,
    sorting_rules JSONB,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'inactive')),
    division_config JSONB,
    created_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (league_id) REFERENCES league.leagues(league_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_division_id) REFERENCES league.divisions(division_id) ON DELETE SET NULL,
    UNIQUE(league_id, division_code)
);
-- Recreate table with VARCHAR columns and CHECK constraints
CREATE TABLE IF NOT EXISTS team.team_managers (
    user_id UUID PRIMARY KEY,
    team_id UUID NOT NULL,
    manager_type VARCHAR(20) DEFAULT 'head_manager' CHECK (manager_type IN ('head_manager', 'assistant_manager')),
    can_manage_roster BOOLEAN DEFAULT TRUE,
    can_view_financials BOOLEAN DEFAULT FALSE,
    can_communicate_league BOOLEAN DEFAULT TRUE,
    contract_expiry_date DATE,
    assigned_by UUID,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'active', 'rejected')),
    approved_by UUID,
    approved_at TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team.teams(team_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS player.player_contracts (
    contract_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    player_id UUID NOT NULL REFERENCES team.players(player_id) ON DELETE CASCADE,
    team_id UUID NOT NULL REFERENCES team.teams(team_id),
    contract_type VARCHAR(20) DEFAULT 'amateur' CHECK (contract_type IN ('professional', 'academy', 'amateur', 'loan')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    salary_amount DECIMAL(12,2),
    salary_currency VARCHAR(3) DEFAULT 'USD',
    contract_status VARCHAR(20) DEFAULT 'active' CHECK (contract_status IN ('active', 'expired', 'terminated')),
    is_loan BOOLEAN DEFAULT FALSE,
    loan_from_team_id UUID REFERENCES team.teams(team_id),
    registration_status VARCHAR(20) DEFAULT 'pending' CHECK (registration_status IN ('registered', 'pending', 'unregistered')),
    squad_number INT,
    contract_terms JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,   -- team manager who created the request
    approved_by UUID,  -- league admin who approved
    approved_at TIMESTAMP
);
CREATE TABLE IF NOT EXISTS player.players (
    player_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200),
    date_of_birth DATE NOT NULL,
    place_of_birth VARCHAR(100),
    nationality VARCHAR(3) NOT NULL,
    nationality_secondary VARCHAR(3),
    preferred_foot VARCHAR(10) DEFAULT 'right' CHECK (preferred_foot IN ('right', 'left', 'both')),
    primary_position VARCHAR(20) NOT NULL CHECK (primary_position IN ('goalkeeper', 'defender', 'midfielder', 'forward')),
    secondary_positions JSONB,
    player_agent VARCHAR(255),
    international_status VARCHAR(10) DEFAULT 'none' CHECK (international_status IN ('none', 'youth', 'senior')),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'injured', 'suspended', 'retired', 'free_agent')),
    profile_image_url VARCHAR(500),
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

=========================================
=========================================
==================Referee Domain=========
========================================
=========================================

CREATE TABLE referee.referee_branches (
    branch_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_name VARCHAR(255) NOT NULL,
    branch_code VARCHAR(50) UNIQUE NOT NULL,
    district VARCHAR(100),
    professional_level VARCHAR(20) CHECK (professional_level IN ('elite', 'regular', 'youth')),
    mother_body_id UUID,  -- references referee.mother_bodies (future)
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE referee.referees (
    referee_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES core.users(user_id) ON DELETE CASCADE,
    referee_code VARCHAR(50) UNIQUE NOT NULL,
    current_class CHAR(1) NOT NULL CHECK (current_class IN ('D', 'C', 'B', 'A')),
    date_of_birth DATE,  -- denormalised from user for quick access
    nationality VARCHAR(3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE referee.referee_registration_requests (
    request_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES core.users(user_id) ON DELETE CASCADE,
    branch_id UUID NOT NULL REFERENCES referee.referee_branches(branch_id) ON DELETE CASCADE,
    requested_class CHAR(1) NOT NULL CHECK (requested_class IN ('D', 'C', 'B', 'A')),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    approved_by_user_id UUID REFERENCES core.users(user_id),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, branch_id)  -- one pending/approved request per user+branch
);

CREATE TABLE referee.referee_branch_memberships (
    membership_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    referee_id UUID NOT NULL REFERENCES referee.referees(referee_id) ON DELETE CASCADE,
    branch_id UUID NOT NULL REFERENCES referee.referee_branches(branch_id) ON DELETE CASCADE,
    certificate_url VARCHAR(500),  -- link to stored PDF/image
    joined_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'suspended', 'inactive')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(referee_id, branch_id)
);
CREATE TABLE referee.branch_admin_assignments (
    assignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES core.users(user_id) ON DELETE CASCADE,
    branch_id UUID NOT NULL REFERENCES referee.referee_branches(branch_id) ON DELETE CASCADE,
    assigned_by UUID REFERENCES core.users(user_id),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, branch_id)
);
CREATE TABLE referee.branch_league_division_links (
    link_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES referee.referee_branches(branch_id) ON DELETE CASCADE,
    league_id UUID NOT NULL REFERENCES league.leagues(league_id) ON DELETE CASCADE,
    division_id UUID REFERENCES league.divisions(division_id) ON DELETE CASCADE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(branch_id, league_id, division_id)
);

========================================
========================================
=========MarketPlace====================
========================================
========================================
CREATE SCHEMA IF NOT EXISTS marketplace;

-- Store application (request to become a seller)
CREATE TABLE marketplace.store_applications (
    application_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,                     -- references core.users (logical)
    store_name VARCHAR(255) NOT NULL,
    store_description TEXT,
    store_category VARCHAR(50) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    tax_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected', 'info_requested')),
    reviewed_by_user_id UUID,
    review_notes TEXT,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Approved stores
CREATE TABLE marketplace.stores (
    store_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    store_name VARCHAR(255) NOT NULL,
    store_description TEXT,
    store_category VARCHAR(50) NOT NULL,
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    tax_id VARCHAR(100),
    logo_image_url VARCHAR(500),
    banner_image_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    commission_rate DECIMAL(5,2) DEFAULT 5.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE marketplace.products (
    product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    store_id UUID NOT NULL,                     -- references marketplace.stores
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    tags TEXT[] DEFAULT '{}',
    price DECIMAL(12,2) NOT NULL CHECK (price >= 0),
    stock_quantity INT NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    low_stock_threshold INT DEFAULT 5,
    weight_kg DECIMAL(8,2),
    is_available BOOLEAN DEFAULT TRUE,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product images
CREATE TABLE marketplace.product_images (
    image_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bulk pricing tiers
CREATE TABLE marketplace.bulk_pricing_tiers (
    tier_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    min_quantity INT NOT NULL,
    max_quantity INT,                           -- NULL means unlimited
    discount_percentage DECIMAL(5,2) NOT NULL,
    fixed_price DECIMAL(12,2),                  -- if set overrides unit price
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_products_store_id ON marketplace.products(store_id);
CREATE INDEX idx_products_category ON marketplace.products(category);
CREATE INDEX idx_product_images_product_id ON marketplace.product_images(product_id);
CREATE INDEX idx_bulk_pricing_tiers_product_id ON marketplace.bulk_pricing_tiers(product_id);


CREATE SCHEMA IF NOT EXISTS competition;

CREATE TABLE competition.seasons (
    season_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    division_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    season_year INT NOT NULL,
    start_date DATE,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'draft' CHECK (status IN ('draft', 'fixtures_generated', 'in_progress', 'completed', 'archived')),
    fixture_generation_config JSONB,
    confirmed_at TIMESTAMP,
    confirmed_by UUID,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(division_id, season_year)
);

CREATE TABLE IF NOT EXISTS competition.fixtures (
    fixture_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id UUID NOT NULL,
    match_week INT NOT NULL,
    home_team_id UUID NOT NULL,
    away_team_id UUID NOT NULL,
    scheduled_date DATE,
    scheduled_time TIME,
    venue VARCHAR(255),
    status VARCHAR(20) DEFAULT 'scheduled' CHECK (status IN ('scheduled', 'played', 'postponed', 'cancelled')),
    home_score INT,
    away_score INT,
    match_report TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS competition.fixture_referees (
    assignment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    fixture_id UUID NOT NULL REFERENCES competition.fixtures(fixture_id) ON DELETE CASCADE,
    referee_id UUID NOT NULL REFERENCES referee.referees(referee_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('main', 'assistant1', 'assistant2', 'fourth')),
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID,
    UNIQUE(fixture_id, role)
);

CREATE INDEX idx_fixture_referees_fixture ON competition.fixture_referees(fixture_id);
CREATE INDEX idx_fixture_referees_referee ON competition.fixture_referees(referee_id);