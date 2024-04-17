package fr.utc.chat.dao;

import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByChatGroupId(Long groupId);

    @EntityGraph(attributePaths = {"chatGroup", "user"})
    List<GroupMember> findByChatGroupIdOrderByUserIdAsc(Long chatGroupId);

    List<GroupMember> findByUserId(Long userId);

    GroupMember findByChatGroupAndUser(ChatGroup chatGroup, User user);
    @Transactional
    void deleteByChatGroupId(Long chatGroupId);
    @Transactional
    void deleteByUserId(Long userId);

}
