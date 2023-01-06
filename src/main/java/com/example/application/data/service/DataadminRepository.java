package com.example.application.data.service;

import com.example.application.data.entity.Dataadmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DataadminRepository extends JpaRepository<Dataadmin, Long>, JpaSpecificationExecutor<Dataadmin> {

}
