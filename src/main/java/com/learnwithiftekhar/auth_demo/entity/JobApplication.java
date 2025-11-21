package com.learnwithiftekhar.auth_demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_job_candidate",
                        columnNames = {"job_id", "candidate_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_job"))
    private Job job;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_application_candidate"))
    private User candidate;

    // На будущее — URL резюме в S3 (пока можно хранить null)
    @Column
    private String resumeUrl;

    @Column(length = 4000)
    private String coverLetter;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    public enum ApplicationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
