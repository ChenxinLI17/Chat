package fr.utc.chat.controller;

import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.service.ChatGroupService;
import fr.utc.chat.service.GroupMemberService;
import fr.utc.chat.util.Response;
import fr.utc.chat.dao.ChatGroupRepository;
import fr.utc.chat.model.User;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chat/create_chat_group")
public class ChatCreateController {
    private static final Logger logger = LoggerFactory.getLogger(ChatCreateController.class);
    @Resource
    private ChatGroupService chatGroupService;
    @Resource
    private GroupMemberService groupMemberService;
    @GetMapping
    public Response getChatGroup(HttpSession session){
        logger.info("getInfo");
        User user = (User) session.getAttribute("loggedInUser");
        if(user==null){
            return new Response("401","faux login in");
        }
        return new Response("200","success");
    }

    @PostMapping
    public Response CreateChatGroup(@RequestParam("chatTitle") String chatTitle,
                               @RequestParam(value="chatDesc", required = false) String chatDesc,
                               @RequestParam("expirationTime") String expirationTime, HttpSession session) {
        logger.info("createChat");
        User user = (User) session.getAttribute("loggedInUser");

        // 检查群聊名称是否已存在
//        ChatGroup existingChatGroup = chatGroupRepository.findChatGroupByTitle(chatTitle);
        ChatGroup existingChatGroup = chatGroupService.getChatGroupByTitle(chatTitle);
        if (existingChatGroup != null) {
            // 如果群聊名称已存在，返回错误信息
            return new Response("400","Chat group title already exists");
        }

        // 转换过期时间为 LocalDateTime
        LocalDateTime expirationDateTime = LocalDateTime.parse(expirationTime);

        // 检查过期时间是否早于当前时间
        if (expirationDateTime.isBefore(LocalDateTime.now())) {
            // 如果过期时间早于当前时间，返回错误信息
            return new Response("400","Expiration time must be later than current time");
        }

        ChatGroup newChat = new ChatGroup();
        newChat.setTitle(chatTitle);
        newChat.setDescription(chatDesc);
        newChat.setCreateTime(LocalDateTime.now());
        newChat.setExpirationTime(expirationDateTime);
        newChat.setOwner(user);
        newChat.setOwnerMail(user.getMail());
//        chatGroupRepository.save(newChat);
        chatGroupService.chatGroupSave(newChat);
        GroupMember groupMember = new GroupMember(newChat,user);
        groupMemberService.groupMemberSave(groupMember);
//        groupMemberRepository.save(groupMember);

        return new Response("200","create success");
    }

    @RequestMapping(method = RequestMethod.OPTIONS)
    public Response handleOptionsRequest() {
        return new Response("200","success");
    }

}
