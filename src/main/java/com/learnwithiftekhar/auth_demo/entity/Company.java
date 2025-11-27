package com.learnwithiftekhar.auth_demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies",
        uniqueConstraints = {
                @UniqueConstraint(name = "company_bin_unique", columnNames = "bin")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "bin", nullable = false, unique = true, length = 12)
    private String bin;

    @Column(name = "companyName", nullable = false)
    private String companyName;

    @Column(name = "website")
    private String website;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "approved")
    private boolean approved;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_company_user"))
    private User user;
}
