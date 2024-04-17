package fr.utc.chat.service.impl;

import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.model.User;
import fr.utc.chat.service.GroupMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class GroupMemberServiceImpl implements GroupMemberService {
    @Override
    public void groupMemberSave(GroupMember groupMember) {
        groupMemberRepository.save(groupMember);
    }
    @Resource
    private GroupMemberRepository groupMemberRepository;
    @Override
    public List<GroupMember> findByChatGroupId(Long groupId) {
        return groupMemberRepository.findByChatGroupId(groupId);
    }

    @Override
    public List<GroupMember> findByChatGroupIdOrderByUserIdAsc(Long chatGroupId) {
        return groupMemberRepository.findByChatGroupIdOrderByUserIdAsc(chatGroupId);
    }

    @Override
    public List<GroupMember> findByUserId(Long userId) {
        return groupMemberRepository.findByUserId(userId);
    }

    @Override
    public GroupMember findByChatGroupAndUser(ChatGroup chatGroup, User user) {
        return groupMemberRepository.findByChatGroupAndUser(chatGroup,user);
    }

    @Override
    public void deleteByChatGroupId(Long chatGroupId) {
        groupMemberRepository.deleteByChatGroupId(chatGroupId);
    }

    @Override
    public void deleteByUserId(Long userId) {
        groupMemberRepository.deleteByUserId(userId);
    }
}
