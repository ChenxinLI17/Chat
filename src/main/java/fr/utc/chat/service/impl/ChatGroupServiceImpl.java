package fr.utc.chat.service.impl;

import fr.utc.chat.dao.ChatGroupRepository;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.User;
import fr.utc.chat.service.ChatGroupService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatGroupServiceImpl implements ChatGroupService {
    @Resource
    private ChatGroupRepository chatGroupRepository;
    @Override
    public void chatGroupSave(ChatGroup chatGroup) {
        chatGroupRepository.save(chatGroup);
    }
    @Override
    public void chatGroupDelete(ChatGroup chatGroup) {
        chatGroupRepository.delete(chatGroup);
    }
    @Override
    public List<ChatGroup> getAllChatGroup() {
        return chatGroupRepository.findAll();
    }
    @Override
    public Optional<ChatGroup> getChatGroupById(Long id) {
        return chatGroupRepository.findChatGroupById(id);
    }
    @Override
    public ChatGroup getChatGroupByTitle(String chatTitle) {
        return chatGroupRepository.findChatGroupByTitle(chatTitle);
    }

    @Override
    public List<ChatGroup> getChatGroupByOwnerId(Long ownerId) {
        return chatGroupRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<ChatGroup> getChatGroupByExpirationTimeBefore(LocalDateTime now) {
        return chatGroupRepository.findByExpirationTimeBefore(now);
    }

}
