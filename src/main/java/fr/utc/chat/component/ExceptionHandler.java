package fr.utc.chat.component;

import java.security.SignatureException;

import fr.utc.chat.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);
    @org.springframework.web.bind.annotation.ExceptionHandler(value = { SignatureException.class })
    @ResponseBody
    public Response authorizationException(SignatureException e){
        logger.info("permission");
        return new Response("401",e.getMessage());
    }
}

