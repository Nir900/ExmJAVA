package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.dto.StudentDto;
import com.example.entity.Student;

@Component
public class StudentMapper {
    public Student toEntity(StudentDto dto) {
        if (dto == null) {
            return null;
        }

        Student student = new Student();
        student.setId(dto.getId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setAge(dto.getAge());
        student.setEmail(dto.getEmail());

        return student;
    }

    public StudentDto toDto(Student entity) {
        if (entity == null) {
            return null;
        }

        StudentDto dto = new StudentDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setAge(entity.getAge());
        dto.setEmail(entity.getEmail());

        return dto;
    }
    
    public void updateEntityFromDto(Student entity, StudentDto dto) {
        if (entity == null || dto == null) {
            return;
        }

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setAge(dto.getAge());
        entity.setEmail(dto.getEmail());
    }
}
