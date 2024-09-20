package com.thomaslincoln.todosimple.services;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thomaslincoln.todosimple.models.Task;
import com.thomaslincoln.todosimple.models.User;
import com.thomaslincoln.todosimple.repositories.TaskRepository;

import jakarta.transaction.Transactional;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private UserService userService;

  public Task findById(Long id) {
    Optional<Task> task = this.taskRepository.findById(id);
    return task
        .orElseThrow(() -> new RuntimeException("Task não encontrada! Id:" + id + ", Tipo: " + Task.class.getName()));
  }

  public List<Task> findAllByUserId(Long userId) {
    List<Task> tasks = this.taskRepository.findByUser_Id(userId);
    return tasks;
  }

  @Transactional
  public Task create(Task obj) {
    User user = this.userService.findById(obj.getUser().getId());
    obj.setId(null);
    obj.setUser(user);
    obj = this.taskRepository.save(obj);
    return obj;
  }

  @Transactional
  public Task update(Task obj) {
    Task newObj = findById(obj.getId());
    newObj.setDescription(obj.getDescription());
    return this.taskRepository.save(newObj);
  }

  public void delete(Long id) {
    Task obj = findById(id);
    try {
      this.taskRepository.delete(obj);
    } catch (Exception e) {
      throw new RuntimeException("Não é possível excluir essa Task");
    }
  }
}
