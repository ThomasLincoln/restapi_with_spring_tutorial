package com.thomaslincoln.todosimple.services;

import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thomaslincoln.todosimple.models.User;
import com.thomaslincoln.todosimple.repositories.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public User findById(Long id){
    Optional<User> user = userRepository.findById(id);
    return user.orElseThrow(() -> new RuntimeException(
      "Usuário não encontrado! Id:" + id + ", Tipo: " + User.class.getName()
      ));
  }

  @Transactional
  public User create(User obj){
    obj.setId(null);
    obj = this.userRepository.save(obj);
    return obj;
  }

  public User update(User obj){
    User newObj = findById(obj.getId());
    newObj.setPassword(obj.getPassword());
    return this.userRepository.save(newObj);
  }

  public void delete(Long id){
    findById(id);
    try {
      this.userRepository.deleteById(id);
    } catch (Exception e){
      throw new RuntimeException("Não é possível excluir esse usuário, pois há outras entidades relacionadas");
    }
  }
}
