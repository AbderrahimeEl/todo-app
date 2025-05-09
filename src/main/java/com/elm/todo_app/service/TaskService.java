package com.elm.todo_app.service;

import com.elm.todo_app.entity.Role;
import com.elm.todo_app.entity.Statut;
import com.elm.todo_app.entity.Task;
import com.elm.todo_app.entity.User;
import com.elm.todo_app.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;


    public Task createTask(Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        task.setUserAssigne(user);
        task.setStatut(Statut.TODO);

        return taskRepository.save(task);
    }

    public List<Task> getUserTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());

        return taskRepository.findByUserAssigne(user);
    }

    public List<Task> getAllTasks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        if (user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only ADMIN users can access all tasks");
        }
        
        return taskRepository.findAll();
    }

    public Task updateTaskStatus(Long taskId, Statut newStatus) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(authentication.getName());
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        if (!task.getUserAssigne().getId().equals(user.getId())) {
            throw new AccessDeniedException("Only the assigned user can update the task status");
        }
        task.setStatut(newStatus);
        
        return taskRepository.save(task);
    }
}