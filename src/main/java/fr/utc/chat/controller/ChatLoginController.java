package fr.utc.chat.controller;

import fr.utc.chat.component.AuthenticationService;
import fr.utc.chat.service.UserService;
import fr.utc.chat.util.Response;
import fr.utc.chat.component.WebSocketServer;
import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/chat/user_login")
public class ChatLoginController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    @Resource
    private UserService userService;
    @Resource
    private AuthenticationService authenticationService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping
    public Response postLogin(@RequestParam("mail") String mail,
                              @RequestParam("password") String password, HttpSession session) {
        logger.info("u {} p {}",mail,password);
        JSONObject json = new JSONObject();
        //User u = userRepository.findByMail(mail);
        User u = userService.getUserByMail(mail);
        Response response = new Response();
         if (u != null && passwordEncoder.matches(password, u.getPassword())) {
            if (!u.isActive()) {
                response.setCode("401");
                response.setMsg("Your account is not active");
            }else{
                response.setCode("200");
                response.setMsg("success");
                logger.info("session1     "+session.getId());
                String token = authenticationService.generateToken(String.valueOf(u.getId()),session.getId());
                json.put("token",token) ;
                json.put("mail",u.getMail());
                response.setData(json);
            }
        }else{
            response.setCode("401");
            response.setMsg("Invalid username or password.");
            logger.info("密码错误");
        }
        logger.info(response.toString());
        return response;
    }
}
