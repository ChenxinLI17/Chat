package fr.utc.chat.controller;

import fr.utc.chat.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat/chatroom")
public class ChatroomController {
    private static final Logger logger = LoggerFactory.getLogger(ChatroomController.class);
    @PostMapping
    public Response EnterChatroom(){
        logger.info("chatroom");
        return new Response("200","success");
    }
}
