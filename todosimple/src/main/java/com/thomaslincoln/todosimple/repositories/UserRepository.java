package com.thomaslincoln.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.thomaslincoln.todosimple.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Transactional(readOnly = true)
  User findByUsername(String username);
}
