package fr.utc.chat.intercepter;


import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.service.UserService;
import fr.utc.chat.util.AesUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class SessionTokenInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionTokenInterceptor.class);
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        String encryptedSessionToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("X-Session-Token")) {
                    encryptedSessionToken = cookie.getValue();
                    break;
                }
            }
        }

        if (encryptedSessionToken == null) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing session token");
            logger.info("token null");
            response.sendRedirect("/login");
            return false;
        }

        try {
            String decryptedSessionToken = AesUtil.decrypt(encryptedSessionToken);
            String[] parts = decryptedSessionToken.split(":");
            long decryptedUserId = Long.parseLong(parts[0]);
            String decryptedSessionId = parts[1];

//            Optional<User> optionalUser = userRepository.findOptionalById(decryptedUserId);
            Optional<User> optionalUser = userService.getOptionalUserById(decryptedUserId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                HttpSession session = request.getSession(false);
                if (session != null && decryptedSessionId.equals(session.getId())) {
                    logger.info("sessionCurrent "+session.getId() + "sessionDey " + decryptedSessionId);
                    session.setAttribute("loggedInUser", user);
                    return true;
                }
            }
        } catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session token");
            logger.info("exc "+e.getMessage());
            response.sendRedirect("/login");
            return false;
        }

        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session token");
        response.sendRedirect("/login");
        logger.info("fin");
        return false;
    }
}
