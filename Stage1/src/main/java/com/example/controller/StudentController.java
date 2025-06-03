package com.example.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.dto.StudentDto;
import com.example.response.StandardResponse;
import com.example.service.StudentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }
    
    @GetMapping()
    public ResponseEntity<StandardResponse> getAllStudents() {
        List<StudentDto> students = studentService.getAllStudents();
        StandardResponse response = new StandardResponse("success", students, null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getStudent
    (@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        StandardResponse response = new StandardResponse("success", student, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<StandardResponse> addStudent
    (@Valid @RequestBody StudentDto studentDto) {
        StudentDto added = studentService.addStudent(studentDto);

        URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(added.getId())
                    .toUri();
        StandardResponse response = new StandardResponse("success", added, null);
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse> updateStudent
    (@Valid @RequestBody StudentDto studentDto, @PathVariable Long id) {
        StudentDto updated = studentService.updateStudent(studentDto, id);
        StandardResponse response = new StandardResponse("success", updated, null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}