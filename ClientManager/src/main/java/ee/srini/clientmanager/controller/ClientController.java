package ee.srini.clientmanager.controller;

import ee.srini.clientmanager.model.ClientDTO;
import ee.srini.clientmanager.service.ClientManagerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientController {

    Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientManagerService clientManagerService;

    @GetMapping("/")
    public String listClients(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("clients", clientManagerService.getClientsByUser(username));
        return "index";
    }

    @GetMapping("/client")
    public String showAddClientForm(Model model) {
        model.addAttribute("client", new ClientDTO());
        model.addAttribute("countries", clientManagerService.getAllCountries());
        model.addAttribute("isEdit", false);
        return "client";
    }

    @GetMapping("/client/{id}")
    public String showEditClientForm(@PathVariable Long id, Model model, Authentication authentication) {
        if (authentication == null) {
            logger.error("Authentication is null. Redirecting to login.");
            return "redirect:/login";
        }
        String username = authentication.getName();
        ClientDTO client = clientManagerService.getClientByIdAndUser(id, username);
        if (client == null) {
            return "redirect:/login";
        }
        model.addAttribute("client", client);
        model.addAttribute("countries", clientManagerService.getAllCountries());
        model.addAttribute("isEdit", true);
        return "client";
    }

    @PostMapping("/client")
    public String saveClient(@Valid @ModelAttribute("client") ClientDTO clientDTO,
                             BindingResult bindingResult,
                             Model model,
                             Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("countries", clientManagerService.getAllCountries());
            model.addAttribute("isEdit", clientDTO.getId() != null);
            return "client";
        }
        String username = authentication.getName();
        clientManagerService.saveClient(clientDTO, username);
        return "redirect:/";
    }
}