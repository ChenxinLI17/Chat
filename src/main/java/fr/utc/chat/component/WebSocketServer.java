package fr.utc.chat.component;


import fr.utc.chat.dao.ChatGroupRepository;
import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.dao.GroupMessageRepository;
import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.model.GroupMessage;
import fr.utc.chat.model.User;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



import jakarta.websocket.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ServerEndpoint("/websocket/{userMail}")
@Component
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    public static final Map<Session, UserChats> sessionMap = new ConcurrentHashMap<>();
    private static ChatGroupRepository chatGroupRepository;
    @Autowired
    public void setChatGroupRepository(ChatGroupRepository chatGroupRepository) {
        WebSocketServer.chatGroupRepository = chatGroupRepository;
    }
    private static UserRepository userRepository;
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        WebSocketServer.userRepository = userRepository;
    }
    private static GroupMemberRepository groupMemberRepository;
    @Autowired
    public void setGroupMemberRepository(GroupMemberRepository groupMemberRepository) {
        WebSocketServer.groupMemberRepository = groupMemberRepository;
    }

//    private static GroupMessageRepository groupMessageRepository;
//    @Autowired
//    public void setGroupMessageRepository(GroupMessageRepository groupMessageRepository) {
//        WebSocketServer.groupMessageRepository = groupMessageRepository;
//    }


    @OnOpen
    public void onOpen(Session session, @PathParam("userMail") String userMail) throws IOException {

        //查询当前的User和他加入的所有chatGroups
        User user = userRepository.findByMail(userMail);
        List<GroupMember> groupMembers = groupMemberRepository.findByUserId(user.getId());
        List<ChatGroup> chatGroups = new ArrayList<>();
        for (GroupMember groupMember : groupMembers) {
            chatGroups.add(groupMember.getChatGroup());
        }

        logger.info(groupMembers.toString());
        sessionMap.put(session,new UserChats(user,chatGroups));

        //返回加入的所有chatGroups
        JSONObject result = new JSONObject();
        JSONArray arrayChat = new JSONArray(chatGroups);
        result.put("chats",arrayChat);
        logger.info("new joined user = {}, size now = {}",userMail,sessionMap.size());
        session.getBasicRemote().sendText(result.toString());
    }
    @OnClose
    public void OnClose(Session session,@PathParam("userMail") String userMail){
        sessionMap.remove(session);
        logger.info("username {}退出",userMail);
    }

    @OnMessage
    public void OnMessage(String message,Session session, @PathParam("userMail") String userMail) throws IOException {
        logger.info("收到客户端{} 的消息 {}",userMail,message);
        JSONObject jsonObject = new JSONObject(message);
        String msg = jsonObject.getString("msg");
        String chatTitle = jsonObject.getString("chatTitle");
        ChatGroup chatGroup= chatGroupRepository.findChatGroupByTitle(chatTitle);
        JSONObject result = new JSONObject();

        // 检查是否是创建群聊的请求
        if (jsonObject.has("getUsers") && jsonObject.getBoolean("getUsers")){
            List<User> users = userRepository.findAll();
            List<String> emails = users.stream().map(User::getMail).collect(Collectors.toList());
            result.put("emails", emails);
            session.getBasicRemote().sendText(result.toString());
        }
        else if (jsonObject.has("inviteUsers") && jsonObject.getBoolean("inviteUsers")) {
            // 提取要邀请的用户邮箱
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            // 初始化一个空的 User 列表
            List<User> usersToInvite = new ArrayList<>();
            List<String> alreadyInGroup = new ArrayList<>(); // 用于保存已经在群聊中的用户邮箱
            JSONArray addedUsers = new JSONArray(); // 用于保存成功添加的用户邮箱和其他属性

            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    // 在数据库中查找每个 User
                    User user = userRepository.findByMail(jsonArray.get(i).toString());
                    if (user != null) {
                        // 添加到 usersToInvite 列表
                        usersToInvite.add(user);

                        // 构建包含属性的 JSON 对象
                        JSONObject userObject = new JSONObject();
                        userObject.put("firstName", user.getFirstName());
                        userObject.put("mail", user.getMail());
                        addedUsers.put(userObject);
                    }
                }
            }
            // 获取当前的聊天群
            chatGroup = chatGroupRepository.findChatGroupByTitle(chatTitle);
            if (chatGroup == null) {
                return;
            }

            for (User user : usersToInvite) {
                // 检查用户是否已经是群聊的成员
                GroupMember existingMember = groupMemberRepository.findByChatGroupAndUser(chatGroup, user);
                if (existingMember != null) {
                    // 如果已经是成员，添加到 alreadyInGroup 列表，然后跳过这次循环
                    alreadyInGroup.add(user.getMail());
                    continue;
                }

                // 为每个用户创建一个新的 GroupMember 并保存
                GroupMember member = new GroupMember();
                member.setUser(user);
                member.setChatGroup(chatGroup);
                groupMemberRepository.save(member);

                // 如果用户成功添加到群聊，添加到 addedUsers 列表
                addedUsers.put(user.getMail());

            }

            // 创建并发送响应，包含已经在群聊中的用户和成功添加的用户
            if (!alreadyInGroup.isEmpty()) {
                for(String mail:alreadyInGroup){
                    for (int i = 0; i < addedUsers.length(); i++) {
                        String element = addedUsers.getJSONObject(i).getString("mail");
                        if (element.equals(mail)) {
                            addedUsers.remove(i);
                            break;
                        }
                    }
                }
                result.put("alreadyInGroup", alreadyInGroup);
            }
            if (addedUsers.length() > 0) {
                result.put("addedUsers", addedUsers);
            }
            session.getBasicRemote().sendText(result.toString());
        }
        else if (jsonObject.has("leaveChat") && jsonObject.getBoolean("leaveChat")) {
            chatTitle = jsonObject.getString("chatTitle");
            String userId = jsonObject.getString("userId");
            // 获取 ChatGroup 的 ID
            Long chatGroupId = chatGroup.getId();

            chatGroup = chatGroupRepository.findChatGroupByTitle(chatTitle);
            if (chatGroup == null) {
                return;
            }

            User user = userRepository.findByMail(userId);
            if (user == null) {
                return;
            }

            // 检查用户身份
            if (user.getId() == chatGroup.getOwner().getId()) {
                result.put("dismissedChat", chatTitle);
                session.getBasicRemote().sendText(result.toString());

                // 用户是 Owner，解散群聊
                chatGroupRepository.delete(chatGroup);

                // 删除对应的 GroupMessage
//                groupMessageRepository.deleteByChatGroupId(chatGroupId);

                // 删除对应的 GroupMember
                groupMemberRepository.deleteByChatGroupId(chatGroupId);

            } else {
                // 用户是普通成员，将其从群聊中移除
                GroupMember groupMember = groupMemberRepository.findByChatGroupAndUser(chatGroup, user);
                if (groupMember != null) {
                    groupMemberRepository.delete(groupMember);
                    result.put("leftChat",user.getMail() + " has left the chat - " + chatTitle);
                    session.getBasicRemote().sendText(result.toString());
                }
            }
        }
        else if (!msg.isEmpty()) {
            User userCurrent = userRepository.findByMail(userMail);
            String userName = userCurrent.getFirstName();
//            GroupMessage groupMessage = new GroupMessage();
//            groupMessage.setUser(userRepository.findByMail(userMail));
//            groupMessage.setMessage(msg);
            ChatGroup targetChatGroup = chatGroupRepository.findChatGroupByTitle(chatTitle);
            if (targetChatGroup == null) {
                // 如果此时聊天群不存在，则发送错误信息到前端，并返回，不再保存这条消息
                result.put("errorSendmsg", "The chat " + chatTitle + " has been dismissed.");
                session.getBasicRemote().sendText(result.toString());
                return;
            }
//            groupMessage.setChatGroup(targetChatGroup);
//            groupMessage.setTimestamp(new Date());
//            groupMessageRepository.save(groupMessage);
            logger.info("msgggg "+message);
            result.put("sender",userMail);
            result.put("senderName",userName);
            result.put("msg",msg);
            result.put("chatId",targetChatGroup.getId());
            result.put("chatTitle", targetChatGroup.getTitle());
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            result.put("time",df.format(date));
            sendOthersMessage(result.toString(), session, targetChatGroup.getId());
        }else{
            // 在关联表查询这个聊天群里的所有用户
            List<GroupMember> groupMembers = groupMemberRepository.findByChatGroupId(chatGroup.getId());
//            List<User> members = new ArrayList<>();
//            for (GroupMember groupMember : groupMembers) {
//                if(groupMember.isOwner()){}
//                members.add(groupMember.getUser());
//            }
            List<Map<String, Object>> memberDataList = new ArrayList<>();
            for (GroupMember groupMember : groupMembers) {
                Map<String, Object> memberData = new HashMap<>();
                memberData.put("user", groupMember.getUser());
                boolean isOwner = groupMember.isOwner();
                memberData.put("isOwner", isOwner);
                memberData.put("mail",groupMember.getUser().getMail());
                memberDataList.add(memberData);
            }

//            JSONArray arrayUsers = new JSONArray(members);
//            result.put("users",arrayUsers);
//            logger.info("users "+result.toString());
//            session.getBasicRemote().sendText(result.toString());
            JSONArray arrayUsers = new JSONArray(memberDataList);
            result.put("users", arrayUsers);
            logger.info("users "+result.toString());
            session.getBasicRemote().sendText(result.toString());


            // 获取并发送历史消息
//            List<GroupMessage> historyMessages = groupMessageRepository.findByChatGroupIdOrderByTimestampAsc(chatGroup.getId());
//            for(GroupMessage historyMessage : historyMessages) {
//                JSONObject messageObject = new JSONObject();
//                messageObject.put("sender", historyMessage.getUser().getMail());
//                messageObject.put("msg", historyMessage.getMessage());
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                messageObject.put("timestamp", formatter.format(historyMessage.getTimestamp()));
//                messageObject.put("chatId", historyMessage.getChatGroup().getId());
//                messageObject.put("chatTitle", chatGroup.getTitle()); // 设置 chatTitle 为选中的聊天群的标题
//                messageObject.put("firstName", historyMessage.getUser().getFirstName());
//                messageObject.put("lastName", historyMessage.getUser().getLastName());
//                session.getBasicRemote().sendText(messageObject.toString());
//            }

        }
    }
    @OnError
    public void onError(Session session, Throwable error){
        logger.error("error");
        error.printStackTrace();
    }


    private void sendAllMessage(String message){
//        try {
//            for (Session session: sessionMap.values()){
//                logger.info("服务器给客户端{}发送消息{}",session.getId(),message);
//                session.getBasicRemote().sendText(message);
//            }
//        }catch (Exception e){
//            logger.error("error",e);
//        }
    }

    private void sendOthersMessage(String message,Session currentSession, Long chatId) throws IOException {
        for (Map.Entry<Session, UserChats> entry : sessionMap.entrySet()) {
            Session session = entry.getKey();
            UserChats userChats = entry.getValue();
            for (ChatGroup chat : userChats.getChats()) {
                if (chat.getId().equals(chatId) && !session.getId().equals(currentSession.getId())) {
                    session.getBasicRemote().sendText(message);
                }
            }
        }


    }

}
