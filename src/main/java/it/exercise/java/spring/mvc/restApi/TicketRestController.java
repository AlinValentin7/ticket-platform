package it.exercise.java.spring.mvc.restApi;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.exercise.java.spring.mvc.model.Ticket;
import it.exercise.java.spring.mvc.model.Status;
import it.exercise.java.spring.mvc.repository.TicketRepository;
import it.exercise.java.spring.mvc.responsemodel.Payload;

@RestController
@CrossOrigin
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private TicketRepository ticketRepo;

    /**
     * Visualizza l’elenco di tutti i ticket.
     */
    @GetMapping()
    public ResponseEntity<Payload<List<Ticket>>> all() {
        List<Ticket> tickets = ticketRepo.findAll();
        return ResponseEntity.ok(new Payload<List<Ticket>>("OK", "200", tickets));
    }

    /**
     * Filtra l’elenco dei ticket per stato.
     */
    @GetMapping("/status/{statusTicket}")
    public ResponseEntity<Payload<List<Ticket>>> findByStatus(@PathVariable String statusTicket) {
        try {
            Status status = Status.valueOf(statusTicket.toUpperCase());
            List<Ticket> byStatus = ticketRepo.findByStatus(status);

            if (!byStatus.isEmpty()) {
                return ResponseEntity.ok(new Payload<>("OK", "200", byStatus));
            }

            return new ResponseEntity<>(
                new Payload<List<Ticket>>("No ticket found by status " + statusTicket, "404", null),
                HttpStatus.NOT_FOUND
            );

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(
                new Payload<List<Ticket>>("Status ticket not valid " + statusTicket, "400", null),
                HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Filtra l’elenco dei ticket per categoria.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Payload<List<Ticket>>> findByCategory(@PathVariable String category) {
        List<Ticket> byCategory = ticketRepo.findByCategoryName(category);

        if (!byCategory.isEmpty()) {
            return ResponseEntity.ok(new Payload<>("OK", "200", byCategory));
        }

        return new ResponseEntity<>(
            new Payload<List<Ticket>>("No ticket found by category" + category, "404", null),
            HttpStatus.NOT_FOUND
        );
    }

    /**
     * Visualizza un ticket specifico per ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payload<Ticket>> getTicketById(@PathVariable Long id) {
        Optional<Ticket> ticketOpt = ticketRepo.findById(id);
        return ticketOpt.map(ticket -> ResponseEntity.ok(new Payload<>("OK", "200", ticket)))
                       .orElseGet(() -> new ResponseEntity<>(
                           new Payload<Ticket>("Ticket not found by id " + id, "404", null),
                           HttpStatus.NOT_FOUND
                       ));
    }

    // Altri metodi API (creazione, aggiornamento, eliminazione) possono essere aggiunti qui
}
