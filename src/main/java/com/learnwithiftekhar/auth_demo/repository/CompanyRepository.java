package com.learnwithiftekhar.auth_demo.repository;

import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByBin(String bin);

    Optional<Company> findByUser(User user);

    Optional<Company> findByUserId(Long userId);

    List<Company> findByApprovedFalse();
}
