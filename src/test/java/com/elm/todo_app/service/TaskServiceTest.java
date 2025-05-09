package com.elm.todo_app.service;

import com.elm.todo_app.entity.Role;
import com.elm.todo_app.entity.Statut;
import com.elm.todo_app.entity.Task;
import com.elm.todo_app.entity.User;
import com.elm.todo_app.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private User otherUser;
    private Task testTask;
    private final String TEST_EMAIL = "test@example.com";
    private final Long TASK_ID = 1L;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(TEST_EMAIL);
        testUser.setNom("Test User");
        testUser.setRole(Role.USER);

        otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");
        otherUser.setNom("Other User");
        otherUser.setRole(Role.USER);

        testTask = new Task();
        testTask.setId(TASK_ID);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatut(Statut.TODO);
        testTask.setUserAssigne(testUser);

        // Set up auth
        Authentication authentication = new UsernamePasswordAuthenticationToken(TEST_EMAIL, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void updateTaskStatus_Success() {
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task updatedTask = taskService.updateTaskStatus(TASK_ID, Statut.IN_PROGRESS);

        assertEquals(Statut.IN_PROGRESS, updatedTask.getStatut());
        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository).save(testTask);
    }

    @Test
    void updateTaskStatus_AccessDenied() {
        // Arrange
        Task otherUserTask = new Task();
        otherUserTask.setId(TASK_ID);
        otherUserTask.setTitle("Other User's Task");
        otherUserTask.setUserAssigne(otherUser);

        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(otherUserTask));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            taskService.updateTaskStatus(TASK_ID, Statut.IN_PROGRESS);
        });
        
        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTaskStatus_TaskNotFound() {
        // Arrange
        when(userService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(taskRepository.findById(TASK_ID)).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            taskService.updateTaskStatus(TASK_ID, Statut.IN_PROGRESS);
        });
        
        assertTrue(exception.getMessage().contains("Tsk not found"));
        verify(taskRepository).findById(TASK_ID);
        verify(taskRepository, never()).save(any(Task.class));
    }
}