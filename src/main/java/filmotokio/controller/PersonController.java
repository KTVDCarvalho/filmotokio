package filmotokio.controller;

import filmotokio.domain.Person;
import filmotokio.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/person/new")
    @PreAuthorize("isAuthenticated()")
    public String newPersonForm(Model model) {
        model.addAttribute("person", new Person());
        return "new-person";
    }

    @PostMapping("/person/save")
    @PreAuthorize("isAuthenticated()")
    public String savePerson(@Valid @ModelAttribute Person person, 
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            redirectAttrs.addFlashAttribute("error", "Invalid person data");
            return "redirect:/person/new";
        }
        
        boolean success = personService.savePerson(person);

        if (success) {
            redirectAttrs.addFlashAttribute("message", "Artist created successfully!");
        } else {
            redirectAttrs.addFlashAttribute("error", "Artist already exists!");
        }
        return "redirect:/person/new";
    }
}

