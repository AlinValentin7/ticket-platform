package it.exercise.java.spring.mvc.model;

import java.util.List; 


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ticket {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY) 
private Long id;
@NotBlank
@NotNull(message = "ticket tile cannot be null")
private String title;
@NotBlank
@NotNull
private String text;

@Enumerated(EnumType.STRING)
private Status status;


@OneToMany(mappedBy = "ticket")
private List<Note> notes;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn (name = "user_id", nullable = true)
private User user;

@ManyToOne (fetch = FetchType.EAGER)
@JoinColumn (name = "category_id", nullable = true)
private Category category;

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getTitle() {
	return title;
}

public void setTitle(String title) {
	this.title = title;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public List<Note> getNotes() {
	return notes;
}

public void setNotes(List<Note> notes) {
	this.notes = notes;
}

public User getUsers() {
	return user;
}

public void setUsers(User user) {
	this.user = user;
}

public Category getCategory() {
	return category;
}

public void setCategory(Category category) {
	this.category = category;
}
public Status getStatus() {
    return status;
}

public void setStatus(Status status) {
    this.status = status;
}

}
