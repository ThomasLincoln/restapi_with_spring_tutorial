package com.thomaslincoln.todosimple.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.thomaslincoln.todosimple.models.Task;
import com.thomaslincoln.todosimple.models.User;
import com.thomaslincoln.todosimple.models.enums.ProfileEnum;
import com.thomaslincoln.todosimple.models.projection.TaskProjection;
import com.thomaslincoln.todosimple.repositories.TaskRepository;
import com.thomaslincoln.todosimple.security.UserSpringSecurity;
import com.thomaslincoln.todosimple.services.exceptions.AuthorizationException;
import com.thomaslincoln.todosimple.services.exceptions.DataBindingViolationException;
import com.thomaslincoln.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private UserService userService;

  public Task findById(Long id) {
    Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
        "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName()));
    UserSpringSecurity userSpringSecurity = UserService.authenticated();

    if (Objects.isNull(userSpringSecurity)
        || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
      throw new AuthorizationException("Acesso negado!");
    return task;
  }

  public List<TaskProjection> findAllByUser() {
    UserSpringSecurity userSpringSecurity = UserService.authenticated();
    if (Objects.isNull(userSpringSecurity))
      throw new AuthorizationException("Acesso negado!");
    List<TaskProjection> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
    return tasks;
  }

  @Transactional
  public Task create(Task obj) {
    UserSpringSecurity userSpringSecurity = UserService.authenticated();
    if (Objects.isNull(userSpringSecurity))
      throw new AuthorizationException("Acesso negado!");
    User user = this.userService.findById(userSpringSecurity.getId());
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
      throw new DataBindingViolationException("Não é possível excluir essa Task");
    }
  }

  private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task) {
    return task.getUser().getId().equals(userSpringSecurity.getId());
  }
}
