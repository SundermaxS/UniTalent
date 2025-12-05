package com.learnwithiftekhar.auth_demo.mapper;

import com.learnwithiftekhar.auth_demo.dto.StudentSummaryResponse;
import com.learnwithiftekhar.auth_demo.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentSummaryResponse toDto(User user);
}
