package fr.utc.chat.service;

import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface GroupMemberService {
    void groupMemberSave(GroupMember groupMember);
    List<GroupMember> findByChatGroupId(Long groupId);
    List<GroupMember> findByChatGroupIdOrderByUserIdAsc(Long chatGroupId);

    List<GroupMember> findByUserId(Long userId);

    GroupMember findByChatGroupAndUser(ChatGroup chatGroup, User user);

    void deleteByChatGroupId(Long chatGroupId);

    void deleteByUserId(Long userId);

}
