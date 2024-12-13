package it.exercise.java.spring.mvc.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	private Long id;
	
	//@NotNull (message = "category name must have a name")
	//@NotBlank
	private String name;
	
	@OneToMany(mappedBy = "category")
	private List <Ticket> ticket;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Ticket> getTickets() {
		return ticket;
	}

	public void setTickets(List<Ticket> ticket) {
		this.ticket = ticket;
	}
	
	

}
