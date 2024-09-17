package com.thomaslincoln.todosimple.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thomaslincoln.todosimple.models.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>{

  // Essa função tem que seguir uma regrinha de automação para funcionar, por exemplo, findByUser (encontrar usando o usuário ) _Id (usar a variável id)
  List<Task> findByUser_Id(Long id);

  // Aqui nós usamos uma query automatizada pelo spring
  // @Query(value = "SELECT t FROM Task t WHERE t.user.id = :id")
  // List<Task> findByUserId(@Param("id") Long id);

}
