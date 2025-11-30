package com.learnwithiftekhar.auth_demo.mapper;

import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.Company;
import com.learnwithiftekhar.auth_demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bin", source = "dto.bin")
    @Mapping(target = "companyName", source = "dto.companyName")
    @Mapping(target = "website", source = "dto.website")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "user", source = "user")
    Company toEntity(EmployerRegisterRequest dto, User user);
}
