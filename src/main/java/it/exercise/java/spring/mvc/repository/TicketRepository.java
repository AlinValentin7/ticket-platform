package it.exercise.java.spring.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.Status;
import it.exercise.java.spring.mvc.model.Ticket;
import it.exercise.java.spring.mvc.model.User;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByTitleContaining(String keyword);
    List<Ticket> findByUser(User user);
    List<Ticket> findByTitleContainingAndUser(String keyword, User user);
    List<Ticket> findByStatus(Status status);
    List<Ticket> findByCategoryName(String categoryName);
    
    // Rimuovi il metodo errato
    // List<Ticket> findByNameContaing(String key);
}
