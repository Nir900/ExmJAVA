package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.entity.Student;
import com.example.repository.StudentRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private final StudentRepository studentRepository;

    @Autowired
    public DataInitializer(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void run(String... args) {
        if (studentRepository.count() == 0) {
            // Initial data
            studentRepository.save(
                new Student(
                    null,
                    "John",
                    "Doe",
                    21.5,
                    "john.doe@example.com"
                )
            );
            studentRepository.save(
                new Student(
                    null,
                    "Jane",
                    "Smith",
                    22.3,
                    "jane.smith@example.com"
                )
            );

            System.out.println("Data initialization completed. Created 2 student records!");
        } else {
            System.out.println("Database already contains records. Skipping initialization");
        }
    }
}


