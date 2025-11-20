package com.learnwithiftekhar.auth_demo.mapper;

import com.learnwithiftekhar.auth_demo.dto.EmployerRegisterRequest;
import com.learnwithiftekhar.auth_demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)      // шифруем в сервисе
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "role", constant = "USER")      // ← вот здесь меняем
    User fromEmployerRegister(EmployerRegisterRequest dto);
}
