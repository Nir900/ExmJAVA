package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "students")
@Data
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor 
@ToString
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required!")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable=false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required!")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable=false, length = 50)
    private String lastName;

    @Min(value = 0, message = "Age must be a positive number!")
    @Column(nullable = false)
    private double age;

    @NotBlank(message = "Email is required!")
    @Email(message = "Email should be valid!")
    @Column(unique = true, nullable = false, length = 100)
    private String email;
}
