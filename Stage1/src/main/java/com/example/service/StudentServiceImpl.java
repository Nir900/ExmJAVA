package com.example.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.StudentDto;
import com.example.entity.Student;
import com.example.exception.AlreadyExists;
import com.example.exception.NotExists;
import com.example.exception.StudentIdAndIdMismatch;
import com.example.mapper.StudentMapper;
import com.example.repository.StudentRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentServiceImpl(
        StudentRepository studentRepository,
        StudentMapper studentMapper
    ) {
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository
        .findAll()
        .stream()
        .map(studentMapper::toDto)
        .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository
        .findById(id)
        .orElseThrow(() -> new NotExists(
            "Student with id " + id + " does not exist"
        ));
                
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional
    public StudentDto addStudent(StudentDto studentDto) {
        if (studentRepository.findByEmail(studentDto.getEmail()).
        isPresent()) {
            throw new AlreadyExists(
                "Student with email " + studentDto.getEmail() + " already exists"
            );
        }

        Student student = studentMapper.toEntity(studentDto);

        Student added = studentRepository.save(student);
        return studentMapper.toDto(added);
    }

    @Override
    @Transactional
    public StudentDto updateStudent(StudentDto studentDto, Long id) {
        if (studentDto.getId() != null && !studentDto.getId().equals(id)) {
            throw new StudentIdAndIdMismatch("Path ID " + id + " does not match body ID " + studentDto.getId());
        }

        Student existingStudent = studentRepository.findById(id)
        .orElseThrow(() -> new NotExists("Student with id " + id + " does not exist"));

        studentRepository.findByEmail(studentDto.getEmail())
                .ifPresent(student -> {
                    if (!student.getId().equals(id)) {
                        throw new AlreadyExists(
                            "Email " 
                            + studentDto.getEmail()
                            + " is already in use"
                        );
                    }
                });
        studentMapper.updateEntityFromDto(existingStudent, studentDto);
        
        Student updated = studentRepository.save(existingStudent);
        return studentMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotExists(
                "Student with id "
                + id 
                + " does not exist"
            );
        }
        
        studentRepository.deleteById(id);
    }
}
