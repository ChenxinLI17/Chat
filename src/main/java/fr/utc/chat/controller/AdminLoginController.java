package fr.utc.chat.controller;

import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.service.UserService;
import fr.utc.chat.util.AesUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@Controller
@RequestMapping()
public class AdminLoginController {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Resource
    private UserService userService;
    @GetMapping("/login")
        public String getLogin(Model model) {
            model.addAttribute("user", new User());
            return "login";
    }

    @PostMapping("/login")
    public String postLogin(@Valid @ModelAttribute User user, BindingResult bindingResult, Model model, HttpSession session, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            System.out.println("Validation errors: " + bindingResult.getAllErrors());
            return "login";
        }

        String mail = user.getMail();
        String password = user.getPassword();
        User u = userService.getUserByMail(mail);

        if (u != null && passwordEncoder.matches(password, u.getPassword())) {
            if (!u.isActive()) {
                model.addAttribute("error", "Your account is not active.");
                return "login";
            }

            if (!u.isAdmin()) {
                model.addAttribute("error", "You are not an administrator and cannot log in.");
                return "login";
            }

            // Jetons de session cryptés
            String sessionToken = session.getId();
            String encryptedSessionToken = AesUtil.encrypt(u.getId() + ":" + sessionToken);

            // Stockage des jetons de session dans les cookies
            Cookie cookie = new Cookie("X-Session-Token", encryptedSessionToken);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60); // Fixe la durée de validité du cookie, 1 heure
            response.addCookie(cookie);


            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "redirect:/admin/user_list";

        } else {
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }
}
