package com.learnwithiftekhar.auth_demo.repository;

import com.learnwithiftekhar.auth_demo.entity.Job;
import com.learnwithiftekhar.auth_demo.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByActiveTrue();

    List<Job> findByCompany(Company company);
}
