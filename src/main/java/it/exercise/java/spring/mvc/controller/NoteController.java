
package it.exercise.java.spring.mvc.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import it.exercise.java.spring.mvc.model.Note;
import it.exercise.java.spring.mvc.repository.NoteRepository;

@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRep;

    
    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id) {
        Note note = noteRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + id));
        Long ticketId = note.getTickets().getId();
        noteRep.delete(note);
        
        return "redirect:/tickets/detail/" + ticketId;
    }

    
    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable Long id, @RequestParam String text) {
        Note note = noteRep.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + id));
        Long ticketId = note.getTickets().getId();
        
        note.setText(text);
        
        note.setDate(LocalDate.now());
        
        noteRep.save(note);
        
        return "redirect:/tickets/detail/" + ticketId;
    }
}
