package fr.utc.chat.service;

import fr.utc.chat.model.ChatGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatGroupService {
    void chatGroupSave(ChatGroup chatGroup);
    void chatGroupDelete(ChatGroup chatGroup);
    Optional<ChatGroup> getChatGroupById(Long id);
    List<ChatGroup> getAllChatGroup();
    ChatGroup getChatGroupByTitle(String chatTitle);
    List<ChatGroup> getChatGroupByOwnerId(Long ownerId);
    List<ChatGroup> getChatGroupByExpirationTimeBefore(LocalDateTime now);
}
