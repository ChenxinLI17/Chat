package fr.utc.chat.controller;

import fr.utc.chat.service.UserService;
import fr.utc.chat.util.Response;
import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.util.SanitizationUtil;
import jakarta.annotation.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;

@RestController
@RequestMapping("/chat/user_create")
public class ChatUserCreateController {
    @Resource
    private UserService userService;
    @Resource
    private JavaMailSender mailSender;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private void sendPasswordEmail(String to, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre mot de passe temporaire");
        message.setText("Voici votre mot de passe temporaire : " + password + "\nVeuillez le changer lors de votre première connexion.");
        mailSender.send(message);
    }

    private String generateTempPassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }

    @PostMapping
    public Response createUser(@RequestParam("mail") String mail,
                               @RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName) {
//        User existingUser = userRepository.findByMail(mail);
        User existingUser = userService.getUserByMail(mail);
        if (existingUser != null) {
            return new Response("400", "This email is already registered. Please use another email.");
        }

        User user = new User();
        user.setFirstName(SanitizationUtil.sanitize(firstName));
        user.setLastName(SanitizationUtil.sanitize(lastName));
        user.setMail(SanitizationUtil.sanitize(mail));

        String tempPassword = generateTempPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setActive(true);

//        userRepository.save(user);
        userService.userSave(user);

        sendPasswordEmail(user.getMail(), tempPassword);

        return new Response("200", "User created successfully. An email containing a temporary password has been sent to the user.");
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public Response handleOptionsRequest() {
        return new Response("200","success");
    }
}
