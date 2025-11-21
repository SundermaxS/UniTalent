package com.learnwithiftekhar.auth_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 4000)
    private String description;

    @Column
    private String location;

    @Column
    private String employmentType;

    @Column
    private Integer minSalary;

    @Column
    private Integer maxSalary;

    @Column
    private LocalDateTime createdAt;

    @Column
    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_job_company"))
    private Company company;
}
