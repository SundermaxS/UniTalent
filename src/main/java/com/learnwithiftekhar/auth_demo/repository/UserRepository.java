package com.learnwithiftekhar.auth_demo.repository;

import com.learnwithiftekhar.auth_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import com.learnwithiftekhar.auth_demo.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByRole(Role role, Pageable pageable);

    @Query(
            value = """
        SELECT * FROM users u
        WHERE u.role = 'USER'
          AND (:programClass IS NULL OR lower(u.program_class) = lower(:programClass))
          AND (:gpaMin IS NULL OR CAST(u.gpa AS double precision) >= :gpaMin)
          AND (:gpaMax IS NULL OR CAST(u.gpa AS double precision) <= :gpaMax)
        """,
            countQuery = """
        SELECT count(*) FROM users u
        WHERE u.role = 'USER'
          AND (:programClass IS NULL OR lower(u.program_class) = lower(:programClass))
          AND (:gpaMin IS NULL OR CAST(u.gpa AS double precision) >= :gpaMin)
          AND (:gpaMax IS NULL OR CAST(u.gpa AS double precision) <= :gpaMax)
        """,
            nativeQuery = true
    )
    Page<User> findStudentsFiltered(
            @Param("programClass") String programClass,
            @Param("gpaMin") Double gpaMin,
            @Param("gpaMax") Double gpaMax,
            Pageable pageable
    );

}
