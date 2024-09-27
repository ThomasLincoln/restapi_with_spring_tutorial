package com.thomaslincoln.todosimple.services;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thomaslincoln.todosimple.models.User;
import com.thomaslincoln.todosimple.models.dto.UserCreateDTO;
import com.thomaslincoln.todosimple.models.dto.UserUpdateDTO;
import com.thomaslincoln.todosimple.models.enums.ProfileEnum;
import com.thomaslincoln.todosimple.repositories.UserRepository;
import com.thomaslincoln.todosimple.security.UserSpringSecurity;
import com.thomaslincoln.todosimple.services.exceptions.AuthorizationException;
import com.thomaslincoln.todosimple.services.exceptions.DataBindingViolationException;
import com.thomaslincoln.todosimple.services.exceptions.ObjectNotFoundException;

import jakarta.validation.Valid;

@Service
public class UserService {

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserRepository userRepository;

  public User findById(Long id) {
    UserSpringSecurity userSpringSecurity = authenticated();
    // verificamos se ele está logado e só permitimos quem está logado e está
    // buscando o próprio ID ou se for admin
    if (!Objects.nonNull(userSpringSecurity)
        || userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId())) {
      throw new AuthorizationException("Acesso negado");
    }
    Optional<User> user = userRepository.findById(id);
    return user.orElseThrow(() -> new ObjectNotFoundException(
        "Usuário não encontrado! Id:" + id + ", Tipo: " + User.class.getName()));
  }

  @Transactional
  public User create(User obj) {
    obj.setId(null);
    obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
    obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
    obj = this.userRepository.save(obj);
    return obj;
  }

  public User update(User obj) {
    User newObj = findById(obj.getId());
    newObj.setPassword(obj.getPassword());
    newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
    return this.userRepository.save(newObj);
  }

  public void delete(Long id) {
    findById(id);
    try {
      this.userRepository.deleteById(id);
    } catch (Exception e) {
      throw new DataBindingViolationException(
          "Não é possível excluir esse usuário, pois há outras entidades relacionadas");
    }
  }

  public static UserSpringSecurity authenticated() {
    try {
      return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    } catch (Exception e) {
      return null;
    }
  }

  public User fromDTO(@Valid UserCreateDTO obj) {
    User user = new User();
    user.setUsername(obj.getUsername());
    user.setPassword(obj.getPassword());
    return user;
  }

  public User fromDTO(@Valid UserUpdateDTO obj) {
    User user = new User();
    user.setId(obj.getId());
    user.setPassword(obj.getPassword());
    return user;
  }
}
