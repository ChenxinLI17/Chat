package fr.utc.chat.dao;

import fr.utc.chat.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    List<GroupMessage> findByChatGroupIdOrderByTimestampDesc(Long groupId);

    void deleteByChatGroupId(Long chatGroupId);
}
