package com.surgee.stage_2.DAO;

import com.surgee.stage_2.model.Organization;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationRepository extends JpaRepository <Organization, String> {

    @Query(value = "SELECT o FROM Organization o JOIN o.users u WHERE u.userId = ?1")
    Set<Organization> findAllByUserId(long userId);

}
