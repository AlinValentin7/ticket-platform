package it.exercise.java.spring.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import it.exercise.java.spring.mvc.model.Note;
import it.exercise.java.spring.mvc.model.Ticket;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByTicket(Ticket ticket);
}
