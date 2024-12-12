package it.exercise.java.spring.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}