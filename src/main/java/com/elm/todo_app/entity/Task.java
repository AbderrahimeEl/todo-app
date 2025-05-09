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
public class Task {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING) private Statut statut = Statut.TODO;

    @ManyToOne
    private User userAssigne;
}
