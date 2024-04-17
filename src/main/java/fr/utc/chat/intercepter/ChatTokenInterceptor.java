package fr.utc.chat.intercepter;


import fr.utc.chat.component.AuthenticationService;
import fr.utc.chat.model.User;
import fr.utc.chat.service.UserService;
import io.jsonwebtoken.Claims;
import java.security.SignatureException;
import java.util.Optional;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class ChatTokenInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(ChatTokenInterceptor.class);
    @Resource
    private AuthenticationService authenticationService;
    @Resource
    private UserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Skip interceptor for user registration
        if (request.getRequestURI().equals("/chat/user_create")) {
            return true;
        }

        String method = request.getMethod();
        if (HttpMethod.OPTIONS.toString().equals(method)){
            return true;
        }

        String token = request.getHeader("Authorization");
        logger.info("token header "+token);
        if(token == null||token.isEmpty()){
            token = request.getParameter(authenticationService.getHeader());
            logger.info("token param "+token);
        }
        if(token == null||token.isEmpty()){
            throw new SignatureException("token is null");
        }

        Claims claims;
        try{
            claims = authenticationService.getTokenClaim(token);
            logger.info("claims"+claims);
            if(claims == null){
                throw new SignatureException("token is null");
            }
            if(authenticationService.isTokenExpired(claims.getExpiration())){
                throw new SignatureException("token is expired");
            }
        }catch (Exception e){
            logger.info("exception"+e);
            throw new SignatureException("token invalid, please login in again");
        }
        Long userId = authenticationService.getUserIdFromToken(token);
        logger.info("userId"+userId);
//        Optional<User> optionalUser = userRepository.findOptionalById(userId);
        Optional<User> optionalUser = userService.getOptionalUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            HttpSession session = request.getSession(false);
            if (session != null && session.getId().equals(claims.get("sessionId"))){
                logger.info("loggedInUser");
                logger.info("session2    "+session.getId());
                session.setAttribute("loggedInUser", user);
                return true;
            }else{
                logger.info("faux session");
                throw new SignatureException("faux login");
            }
        }
        throw new SignatureException("faux login");
    }
}

