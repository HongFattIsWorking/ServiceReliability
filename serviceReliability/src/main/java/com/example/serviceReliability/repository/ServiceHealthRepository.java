package com.example.serviceReliability.repository;

import com.example.serviceReliability.entity.ServiceHealth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceHealthRepository extends JpaRepository<ServiceHealth,Long> {

    @Query("""
    SELECT sh FROM ServiceHealth sh
    WHERE sh.id = (
        SELECT MAX(s2.id)
        FROM ServiceHealth s2
        WHERE s2.name = sh.name AND s2.environment = sh.environment
    )
    ORDER BY sh.name, sh.environment
""")
    List<ServiceHealth> getLatestHealth();

}
