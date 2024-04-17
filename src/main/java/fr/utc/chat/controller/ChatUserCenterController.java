package fr.utc.chat.controller;

import fr.utc.chat.service.UserService;
import fr.utc.chat.util.Response;
import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.util.SanitizationUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat/user_center")
public class ChatUserCenterController {
    private static final Logger logger = LoggerFactory.getLogger(ChatUserCenterController.class);
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Resource
    private UserService userService;
    @GetMapping
    public Response getUserInfo(HttpSession session){
        logger.info("getInfo");
        User user = (User) session.getAttribute("loggedInUser");
        JSONObject json = new JSONObject();
        json.put("firstName",user.getFirstName());
        json.put("lastName",user.getLastName());
        json.put("mail",user.getMail());
        return new Response("200","success",json);
    }

    @PostMapping
    public Response updateUser(@RequestParam("firstName") String firstName,
                               @RequestParam("lastName") String lastName,
                               @RequestParam("password") String password, HttpSession session) {
        logger.info("updateUser");
        User user = (User) session.getAttribute("loggedInUser");
        user.setFirstName(SanitizationUtil.sanitize(firstName));
        user.setLastName(SanitizationUtil.sanitize(lastName));
        user.setPassword(passwordEncoder.encode(password));

//        userRepository.save(user);
        userService.userSave(user);
        return new Response("200","update success");
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public Response handleOptionsRequest() {
        return new Response("200","success");
    }

}
