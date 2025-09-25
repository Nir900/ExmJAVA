package com.jjj.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "roles", indexes = {@Index(name = "idx_roles_name", columnList = "name")})
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    public Role() {}

    public Role(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

    public Role(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        
        if (!(obj instanceof Role))
            return false;
        
        Role role = (Role)obj;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}