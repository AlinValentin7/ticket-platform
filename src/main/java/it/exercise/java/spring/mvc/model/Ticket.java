package it.exercise.java.spring.mvc.model;

import java.util.List; 

import jakarta.persistence.Entity;
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

@OneToMany(mappedBy = "ticket")
private List<Note> notes;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn (name = "user_id", nullable = false)
private User users;

@ManyToOne (fetch = FetchType.EAGER)
@JoinColumn (name = "category_id", nullable = false)
private Category categories;

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
	return users;
}

public void setUsers(User users) {
	this.users = users;
}

public Category getCategories() {
	return categories;
}

public void setCategories(Category categories) {
	this.categories = categories;
}

}
