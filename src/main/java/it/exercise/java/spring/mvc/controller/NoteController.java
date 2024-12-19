package it.exercise.java.spring.mvc.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.exercise.java.spring.mvc.model.Note;
import it.exercise.java.spring.mvc.model.Ticket;
import it.exercise.java.spring.mvc.model.User;
import it.exercise.java.spring.mvc.repository.NoteRepository;
import it.exercise.java.spring.mvc.repository.TicketRepository;
import it.exercise.java.spring.mvc.repository.UserRepository;

@Controller
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRep;

    @Autowired
    private TicketRepository ticketRep;

    @Autowired
    private UserRepository userRep;

    /**
     * Aggiunge una Nota a un Ticket.
     * Solo ADMIN e OPERATOR assegnati al ticket possono aggiungere note.
     */
    @PostMapping("/add")
    public String addNote(@RequestParam Long ticketId, 
                          @RequestParam String text, 
                          Principal principal, 
                          RedirectAttributes redirectAttributes) {
        Ticket ticket = ticketRep.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id: " + ticketId));

        // Recupera l'utente corrente
        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperatorAssigned = ticket.getUsers().equals(currentUser);

        if (!isAdmin && !isOperatorAssigned) {
            throw new AccessDeniedException("You are not allowed to add notes to this ticket");
        }

        Note note = new Note();
        note.setText(text);
        note.setDate(LocalDate.now());
        note.setTicket(ticket);
        note.setAuthor(currentUser.getUsername());

        noteRep.save(note);

        redirectAttributes.addFlashAttribute("scsMs", "Note added successfully.");
        return "redirect:/tickets/detail/" + ticketId;
    }

    /**
     * Modifica una Nota.
     * Solo l'autore della nota o un ADMIN possono modificare una nota.
     */
    @PostMapping("/edit/{id}")
    public String editNote(@PathVariable Long id, 
                           @RequestParam String text, 
                           Principal principal, 
                           RedirectAttributes redirectAttributes) {
        Note note = noteRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note Id: " + id));

        // Recupera l'utente corrente
        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isAuthor = note.getAuthor().equals(currentUser.getUsername());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not allowed to edit this note");
        }

        // Aggiorna il testo e la data della nota
        note.setText(text);
        note.setDate(LocalDate.now());

        noteRep.save(note);

        redirectAttributes.addFlashAttribute("scsMs", "Note updated successfully.");
        return "redirect:/tickets/detail/" + note.getTicket().getId();
    }

    /**
     * Elimina una Nota.
     * Solo l'autore della nota o un ADMIN possono eliminare una nota.
     */
    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, 
                             Principal principal, 
                             RedirectAttributes redirectAttributes) {
        Note note = noteRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note Id: " + id));

        // Recupera l'utente corrente
        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isAuthor = note.getAuthor().equals(currentUser.getUsername());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You are not allowed to delete this note");
        }

        Long ticketId = note.getTicket().getId();
        noteRep.delete(note);

        redirectAttributes.addFlashAttribute("scsMs", "Note deleted successfully.");
        return "redirect:/tickets/detail/" + ticketId;
    }
}
