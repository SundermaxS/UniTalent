package com.learnwithiftekhar.auth_demo.mapper;

import com.learnwithiftekhar.auth_demo.dto.JobCreateRequest;
import com.learnwithiftekhar.auth_demo.dto.JobResponse;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "location", source = "dto.location")
    @Mapping(target = "employmentType", source = "dto.employmentType")
    @Mapping(target = "minSalary", source = "dto.minSalary")
    @Mapping(target = "maxSalary", source = "dto.maxSalary")
    @Mapping(target = "company", source = "company")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "active", constant = "true")
    Job toEntity(JobCreateRequest dto, Company company);

    @Mapping(target = "companyName", source = "company.companyName")
    @Mapping(target = "companyBin", source = "company.bin")
    JobResponse toDto(Job job);
}
