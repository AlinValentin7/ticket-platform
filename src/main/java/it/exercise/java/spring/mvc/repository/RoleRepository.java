package it.exercise.java.spring.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
