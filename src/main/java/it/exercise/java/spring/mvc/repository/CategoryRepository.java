package it.exercise.java.spring.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
