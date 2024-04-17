package fr.utc.chat.component;

import fr.utc.chat.dao.ChatGroupRepository;
import fr.utc.chat.dao.GroupMessageRepository;
import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.model.ChatGroup;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Transactional
public class ChatGroupCleanupService implements CommandLineRunner {

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public void run(String... args) throws Exception {
        List<ChatGroup> expiredChatGroups = chatGroupRepository.findByExpirationTimeBefore(LocalDateTime.now());
        for(ChatGroup chatGroup : expiredChatGroups) {
            groupMessageRepository.deleteByChatGroupId(chatGroup.getId());
            groupMemberRepository.deleteByChatGroupId(chatGroup.getId());
            chatGroupRepository.deleteById(chatGroup.getId());
        }
    }
}
