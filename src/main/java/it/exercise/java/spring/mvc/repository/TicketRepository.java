package it.exercise.java.spring.mvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.exercise.java.spring.mvc.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long>{

	public List<Ticket> findByTitleContaining(String title);

}
