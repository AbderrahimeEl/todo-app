package com.elm.todo_app.repository;

import com.elm.todo_app.entity.Task;
import com.elm.todo_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserAssigne(User user);
}
