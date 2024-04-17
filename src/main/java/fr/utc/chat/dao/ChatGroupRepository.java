package fr.utc.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import fr.utc.chat.model.ChatGroup;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    Optional<ChatGroup> findChatGroupById(Long id);
    ChatGroup findChatGroupByTitle(String chatTitle);
    List<ChatGroup> findByOwnerId(Long ownerId);
    List<ChatGroup> findByExpirationTimeBefore(LocalDateTime now);
}

