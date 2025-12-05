package com.learnwithiftekhar.auth_demo.repository;

import com.learnwithiftekhar.auth_demo.entity.Job;
import com.learnwithiftekhar.auth_demo.entity.JobApplication;
import com.learnwithiftekhar.auth_demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findByCandidate(User candidate);

    List<JobApplication> findByJob(Job job);

    List<JobApplication> findByJobIn(List<Job> jobs);

    boolean existsByJobAndCandidate(Job job, User candidate);

}
