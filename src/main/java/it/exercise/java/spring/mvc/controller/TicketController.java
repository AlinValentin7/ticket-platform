package it.exercise.java.spring.mvc.controller;

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

import it.exercise.java.spring.mvc.model.Ticket;
import it.exercise.java.spring.mvc.repository.CategoryRepository;
import it.exercise.java.spring.mvc.repository.TicketRepository;



@Controller
@RequestMapping("/tickets")
public class TicketController {
	
	@Autowired
	private TicketRepository ticketRep;
	
	@Autowired
	private CategoryRepository categoryRep;
	
	public String index( Model model, @RequestParam(name = "keyword", required = false) String key) 
	{
		List<Ticket> allTickets;
		
		if(key != null && !key.isBlank()) {
			allTickets = ticketRep.findByTitleContaing(key);
			model.addAttribute(key, key);
		}else {
				allTickets = ticketRep.findAll();
			}
		model.addAttribute("tickets", allTickets);
		
	return "tickets/index";
			}
	
	@GetMapping("/detail/{id}")
	public String show (@PathVariable (name = "id") Long id,
			@RequestParam(name = "key", required = false) String key, Model model ) {
		
		Optional<Ticket> ticketOpt = ticketRep.findById(id);
		
		if(ticketOpt.isPresent()) {
			model.addAttribute("ticket", ticketOpt.get());
		}
		model.addAttribute("key", key);
		if (key == null || key.isBlank() || key.equals("null")) {
			model.addAttribute("ticketUrl", "/tickets");
		}else {
			model.addAttribute("ticketsUrl", "/tickets?key=" + key);
		}
		
		return "tickets/detail";
	}
	
	@GetMapping("/create")
	public String create (Model model) {
		model.addAttribute("ticket", new Ticket());
		model.addAttribute("allCategories", categoryRep.findAll());
		return "tickets/create";
	}
	
	@PostMapping("/create")
	public String store (@ModelAttribute("ticket") Ticket formTickets, BindingResult bindingResult, RedirectAttributes redirectAttributes,
			Model model) {
		if(bindingResult.hasErrors()) {
			return "tickets/create";
			}
		ticketRep.save(formTickets);
		redirectAttributes.addFlashAttribute("scsMs", "New Ticket was create");
		return "redirect:/tickets";
		}
	
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		model.addAttribute("tickets", ticketRep.findById(id).get());
		model.addAttribute("allCategories", categoryRep.findAll());
		
		return "tickets/edit";
	}
	
	@PostMapping("edit/{id}")
	public String update (@PathVariable(name = "id") Long id, BindingResult bindingResult,
			@ModelAttribute("ticket") Ticket formTicket) {
		if (bindingResult.hasErrors()) {
			return "/tickets/edit";
		}
		
		ticketRep.save(formTicket);
		return "redirect:/tickets";
	}
	
	@PostMapping("/delete/{id}")
	public String delete (@PathVariable("id") Long id) {
		ticketRep.deleteById(id);
		return "redierct:/tickets";
	}

	
}
	
	
	

	


