package fr.utc.chat.controller;

import fr.utc.chat.component.MailSenderService;
import fr.utc.chat.model.User;
import fr.utc.chat.service.UserService;
import fr.utc.chat.util.SanitizationUtil;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("create_user")
public class AdminCreateUserController {
    @Resource
    private UserService userService;
    @Resource
    private MailSenderService mailSenderService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @GetMapping
    public String getNewUser(Model model) {
        model.addAttribute("user", new User());
        return "create_user";
    }

    @PostMapping
    public String postNewUser(@ModelAttribute User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "create_user";
        }

        user.setFirstName(SanitizationUtil.sanitize(user.getFirstName()));
        user.setLastName(SanitizationUtil.sanitize(user.getLastName()));
        user.setMail(SanitizationUtil.sanitize(user.getMail()));

        // Check if the email already exists
        if (userService.getUserByMail(user.getMail()) != null) {
            model.addAttribute("emailError", "This email is already registered. Please use another email.");
            return "create_user";
        }

        // Générez un mot de passe temporaire
        String tempPassword = mailSenderService.generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setAdmin(true);
        user.setActive(true);

        // Enregistrez l'utilisateur
        userService.userSave(user);

        // Envoyez un e-mail avec le mot de passe temporaire
        mailSenderService.sendPasswordEmail(user.getMail(), tempPassword);

        model.addAttribute("message", "An email containing a temporary password has been sent to the user.");

        return "login";
    }
}
