package com.learnwithiftekhar.auth_demo.mapper;

import com.learnwithiftekhar.auth_demo.dto.JobApplicationResponse;
import com.learnwithiftekhar.auth_demo.entity.JobApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobApplicationMapper {

    @Mapping(target = "jobId", source = "job.id")
    @Mapping(target = "jobTitle", source = "job.title")
    @Mapping(target = "candidateId", source = "candidate.id")
    @Mapping(target = "candidateEmail", source = "candidate.email")
    @Mapping(target = "status", expression = "java(application.getStatus().name())")
    JobApplicationResponse toDto(JobApplication application);
}
