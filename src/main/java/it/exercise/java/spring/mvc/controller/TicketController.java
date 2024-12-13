package it.exercise.java.spring.mvc.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping
	public String index(Model model, @RequestParam(name = "keyword", required = false) String key) {
		List<Ticket> allTickets;

		if (key != null && !key.isBlank()) {
			allTickets = ticketRep.findByTitleContaining(key);
			model.addAttribute(key, key);
		} else {
			allTickets = ticketRep.findAll();
		}
		model.addAttribute("tickets", allTickets);

		return "tickets/index";
	}

	@GetMapping("/detail/{id}")
	public String show(@PathVariable(name = "id") Long id, @RequestParam(name = "key", required = false) String key,
			Model model) {

		Optional<Ticket> ticketOpt = ticketRep.findById(id);

		if (ticketOpt.isPresent()) {
			model.addAttribute("ticket", ticketOpt.get());
		}
		model.addAttribute("key", key);
		if (key == null || key.isBlank() || key.equals("null")) {
			model.addAttribute("ticketUrl", "/tickets");
		} else {
			model.addAttribute("ticketsUrl", "/tickets?key=" + key);
		}

		return "tickets/detail";
	}

	@GetMapping("/create")
	public String create(Model model) {
		model.addAttribute("ticket", new Ticket());
		model.addAttribute("allCategories", categoryRep.findAll());
		return "tickets/create";
	}

	@PostMapping("/create")
	public String store(@ModelAttribute("ticket") Ticket formTickets, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("allCategories", categoryRep.findAll());
			return "tickets/create";
		}
		Category cat = categoryRep.findById(formTickets.getCategory().getId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
		formTickets.setCategory(cat);

		User adminUser = userRep.findByUsername("admin");

		formTickets.setUsers(adminUser);

		formTickets.setStatus(Status.TODO);
		ticketRep.save(formTickets);

		redirectAttributes.addFlashAttribute("scsMs", "New Ticket was create");
		return "redirect:/tickets";
	}

	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		Ticket ticket = ticketRep.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
		model.addAttribute("ticket", ticket);
		model.addAttribute("allCategories", categoryRep.findAll());
		return "tickets/edit";
	}

	@PostMapping("edit/{id}")
	public String update(@PathVariable Long id, @Valid @ModelAttribute("ticket") Ticket formTicket,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "/tickets/edit";
		}

		Ticket existingTicket = ticketRep.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));

		formTicket.setUsers(existingTicket.getUsers());

		ticketRep.save(formTicket);
		return "redirect:/tickets";
	}

	@PostMapping("/delete/{id}")
	public String deleteTicket(@PathVariable("id") Long id) {

		noteRep.deleteById(id);
		ticketRep.deleteById(id);

		return "redirect:/tickets";
	}

	@PostMapping("/updateStatus/{id}")
	public String updateStatus(@PathVariable Long id, @RequestParam Status status) {
		Ticket ticket = ticketRep.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
		ticket.setStatus(status);
		ticketRep.save(ticket);
		return "redirect:/tickets";
	}

	@PostMapping("/addNote/{id}")
	public String addNote(@PathVariable Long id, @RequestParam String text) {
		Ticket ticket = ticketRep.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Invalid ticket Id:" + id));
		Note note = new Note();
		note.setText(text);
		note.setDate(LocalDate.now());
		note.setTickets(ticket);
		noteRep.save(note);
		return "redirect:/tickets";
	}

}
