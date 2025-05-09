package com.elm.todo_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity

public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String nom;
    @Column(unique = true) private String email;
    private String password;
    @Enumerated(EnumType.STRING) private Role role;
}
