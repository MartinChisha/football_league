package com.backspacestudios.league_management.core.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backspacestudios.league_management.core.entity.SuperAdmin;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, UUID> {
}
