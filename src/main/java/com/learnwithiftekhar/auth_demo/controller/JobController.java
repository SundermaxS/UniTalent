package com.learnwithiftekhar.auth_demo.controller;

import com.learnwithiftekhar.auth_demo.dto.*;
import com.learnwithiftekhar.auth_demo.service.JobApplicationService;
import com.learnwithiftekhar.auth_demo.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final JobApplicationService jobApplicationService;

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestBody JobCreateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(jobService.createJob(request, principal));
    }

    @GetMapping("/my")
    public ResponseEntity<List<JobResponse>> getMyJobs(
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(jobService.getMyJobs(principal));
    }

    @GetMapping("/my/applications")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsForMyJobs(
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(jobApplicationService.getApplicationsForMyJobs(principal));
    }

    @GetMapping
    public ResponseEntity<List<JobResponse>> getAllActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @PostMapping("/{jobId}/apply")
    public ResponseEntity<JobApplicationResponse> apply(
            @PathVariable Long jobId,
            @RequestBody(required = false) JobApplicationCreateRequest request,
            @AuthenticationPrincipal UserDetails principal
    ) {
        if (request == null) {
            request = new JobApplicationCreateRequest();
        }
        request.setJobId(jobId);
        return ResponseEntity.ok(jobApplicationService.apply(request, principal));
    }

    @GetMapping("/applications/my")
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserDetails principal
    ) {
        return ResponseEntity.ok(jobApplicationService.getMyApplications(principal));
    }
}
