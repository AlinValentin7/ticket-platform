package it.exercise.java.spring.mvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.Note;

public interface NoteRepository extends JpaRepository <Note, Long> {

}
