package com.example.service;

import java.util.List;

import com.example.dto.StudentDto;

public interface StudentService {
    List<StudentDto> getAllStudents();

    StudentDto getStudentById(Long id);
    StudentDto addStudent(StudentDto studentDto);
    StudentDto updateStudent(StudentDto studentDto, Long id);

    void deleteStudent(Long id);
}