// O principal objetivo dessa classe em uma aplicação Spring Security é fornecer uma ponte entre a sua base de dados e o 
// sistema de autenticação do Spring Security. Quando alguém tenta fazer login, o Spring Security precisa de uma forma de
//  carregar os dados do usuário para verificar as credenciais e determinar os privilégios (roles/perfis) do usuário.

package com.thomaslincoln.todosimple.services;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.thomaslincoln.todosimple.models.User;
import com.thomaslincoln.todosimple.repositories.UserRepository;
import com.thomaslincoln.todosimple.security.UserSpringSecurity;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsername(username);
    if(Objects.isNull(user)){
      throw new UsernameNotFoundException(username);
    }
    return new UserSpringSecurity(user.getId(), user.getUsername(), user.getPassword(), user.getProfiles());
  }

}
