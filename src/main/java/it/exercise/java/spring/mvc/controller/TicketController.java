package it.exercise.java.spring.mvc.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.exercise.java.spring.mvc.model.Category;
import it.exercise.java.spring.mvc.model.Note;
import it.exercise.java.spring.mvc.model.Status;
import it.exercise.java.spring.mvc.model.Ticket;
import it.exercise.java.spring.mvc.model.User;
import it.exercise.java.spring.mvc.repository.CategoryRepository;
import it.exercise.java.spring.mvc.repository.NoteRepository;
import it.exercise.java.spring.mvc.repository.TicketRepository;
import it.exercise.java.spring.mvc.repository.UserRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private TicketRepository ticketRep;

    @Autowired
    private CategoryRepository categoryRep;

    @Autowired
    private UserRepository userRep;

    @Autowired
    private NoteRepository noteRep;

    /**
     * Lista dei Ticket.
     * - ADMIN vede tutti i ticket.
     * - OPERATOR vede solo i ticket a lui assegnati.
     */
    @GetMapping
    public String index(Authentication authentication,
                        Model model, 
                        @RequestParam(name = "keyword", required = false) String key, 
                        Principal principal) {
        List<Ticket> allTickets;

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (isAdmin) {
            if (key != null && !key.isBlank()) {
                allTickets = ticketRep.findByTitleContaining(key);
                model.addAttribute("key", key);
            } else {
                allTickets = ticketRep.findAll();
            }
        } else {
            // OPERATOR vede solo i suoi ticket
            if (key != null && !key.isBlank()) {
                allTickets = ticketRep.findByTitleContainingAndUser(key, currentUser);
                model.addAttribute("key", key);
            } else {
                allTickets = ticketRep.findByUser(currentUser);
            }
        }

        model.addAttribute("tickets", allTickets);
        return "tickets/index";
    }

    /**
     * Mostra i dettagli di un Ticket.
     * - ADMIN può vedere qualsiasi ticket.
     * - OPERATOR può vedere solo i ticket a lui assegnati.
     */
    @GetMapping("/detail/{id}")
    public String show(@PathVariable Long id, 
                       @RequestParam(name = "key", required = false) String key,
                       Model model, 
                       Principal principal) {
        List<Ticket> allTickets;

        Ticket ticket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("OPERATOR"));

        if (isOperator && !ticket.getUsers().equals(currentUser)) {
            throw new AccessDeniedException("You are not allowed to see this ticket");
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("key", key);

        if (key != null && !key.isBlank() && !key.equals("null")) {
            allTickets = ticketRep.findByTitleContaining(key);
            model.addAttribute("key", key);
        } else {
            allTickets = ticketRep.findAll();
        }

        model.addAttribute("tickets", allTickets);

        return "tickets/detail";
    }

    /**
     * Mostra il form per creare un nuovo Ticket.
     * Solo ADMIN può accedere.
     */
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("ticket", new Ticket());
        model.addAttribute("allCategories", categoryRep.findAll());
        return "tickets/create";
    }

    /**
     * Salva un nuovo Ticket.
     * Solo ADMIN può creare ticket.
     */
    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("ticket") Ticket formTicket, 
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes, 
                       Model model, 
                       Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryRep.findAll());
            return "tickets/create";
        }

        Category cat = categoryRep.findById(formTicket.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        formTicket.setCategory(cat);

        User adminUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        formTicket.setUsers(adminUser); // Assicurati che il metodo setter sia corretto (setUser)
        formTicket.setStatus(Status.TODO);
        ticketRep.save(formTicket);

        redirectAttributes.addFlashAttribute("scsMs", "New Ticket was created");
        return "redirect:/tickets";
    }

    /**
     * Mostra il form per modificare un Ticket.
     * - ADMIN può modificare qualsiasi ticket.
     * - OPERATOR può modificare solo i ticket a lui assegnati.
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, 
                       Model model, 
                       Principal principal) {
        Ticket ticket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("OPERATOR"));

        if (isOperator && !ticket.getUsers().equals(currentUser)) {
            throw new AccessDeniedException("You are not allowed to edit this ticket");
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("allCategories", categoryRep.findAll());
        return "tickets/edit";
    }

    /**
     * Aggiorna un Ticket.
     * - ADMIN può aggiornare qualsiasi ticket.
     * - OPERATOR può aggiornare solo i ticket a lui assegnati.
     */
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, 
                         @Valid @ModelAttribute("ticket") Ticket formTicket,
                         BindingResult bindingResult, 
                         Model model, 
                         Principal principal) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allCategories", categoryRep.findAll());
            return "tickets/edit";
        }

        Ticket existingTicket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("OPERATOR"));

        if (isOperator && !existingTicket.getUsers().equals(currentUser)) {
            throw new AccessDeniedException("You are not allowed to update this ticket");
        }

        // Mantieni l'utente assegnato
        formTicket.setUsers(existingTicket.getUsers());

        // Imposta la categoria
        Category cat = categoryRep.findById(formTicket.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        formTicket.setCategory(cat);

        // Mantieni lo status originale se non è stato modificato
        if (formTicket.getStatus() == null) {
            formTicket.setStatus(existingTicket.getStatus());
        }

        ticketRep.save(formTicket);
        return "redirect:/tickets";
    }

    /**
     * Elimina un Ticket.
     * Solo ADMIN può eliminare ticket.
     */
    @PostMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Long id, Principal principal) {

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));

        if (!isAdmin) {
           throw new AccessDeniedException("Only admin can delete tickets");
       }

        Ticket ticket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        noteRep.deleteAll(ticket.getNotes());

        ticketRep.delete(ticket);

        return "redirect:/tickets";
    }

    /**
     * Aggiorna lo stato di un Ticket.
     * - ADMIN può aggiornare qualsiasi ticket.
     * - OPERATOR può aggiornare solo i ticket a lui assegnati.
     */
    @PostMapping("/updateStatus/{id}")
    public String updateStatus(@PathVariable Long id, 
                               @RequestParam Status status, 
                               Principal principal) {
        Ticket ticket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("OPERATOR"));

        if (isOperator && !ticket.getUsers().equals(currentUser)) {
            throw new AccessDeniedException("You are not allowed to update this ticket");
        }
        ticket.setStatus(status);
        ticketRep.save(ticket);
        return "redirect:/tickets";
    }

    /**
     * Aggiunge una Nota a un Ticket.
     * - ADMIN può aggiungere note a qualsiasi ticket.
     * - OPERATOR può aggiungere note solo ai ticket a lui assegnati.
     */
    @PostMapping("/addNote/{id}")
    public String addNote(@PathVariable Long id, 
                          @RequestParam String text, 
                          Principal principal) {
        Ticket ticket = ticketRep.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

        User currentUser = userRep.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ADMIN"));
        boolean isOperator = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("OPERATOR"));

       if (isOperator && !ticket.getUsers().equals(currentUser)) {
           throw new AccessDeniedException("You are not allowed to add notes to this ticket");
        
       }
        Note note = new Note();
        note.setText(text);
        note.setDate(LocalDate.now());
        note.setTicket(ticket);
        note.setAuthor(currentUser.getUsername()); // Imposta l'autore come username

        noteRep.save(note);
        return "redirect:/tickets/detail/" + id;
    
    }
}
