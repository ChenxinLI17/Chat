package fr.utc.chat.controller;

import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.dao.GroupMessageRepository;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.model.GroupMember;
import fr.utc.chat.model.GroupMessage;
import fr.utc.chat.model.User;
import fr.utc.chat.service.ChatGroupService;
import fr.utc.chat.service.GroupMemberService;
import fr.utc.chat.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
@Controller
@RequestMapping("admin")
public class AdminChatGroupController {
    @Resource
    private UserService userService;
    @Resource
    private ChatGroupService chatGroupService;
    @Resource
    private GroupMessageRepository groupMessageRepository;
    @Resource
    private GroupMemberRepository groupMemberRepository;
    @Resource
    private GroupMemberService groupMemberService;
    @GetMapping("chatgroups")
    public String chatGroupList(Model model) {
//        List<ChatGroup> chatGroups = chatGroupRepository.findAll();
        List<ChatGroup> chatGroups = chatGroupService.getAllChatGroup();
        model.addAttribute("chatGroups", chatGroups);
        return "admin/chatgroups";  // return to the chatgroups view
    }

    @GetMapping("update_chatgroup/{id}")
    public String updateChatGroup(@PathVariable Long id, Model model) {
//        ChatGroup chatGroup = chatGroupRepository.findById(id).orElseThrow(() -> new RuntimeException("ChatGroup not found"));
        ChatGroup chatGroup = chatGroupService.getChatGroupById(id).orElseThrow(() -> new RuntimeException("ChatGroup not found"));
        model.addAttribute("chatGroup", chatGroup);
        return "admin/update_chatgroup";  // return to the update chatgroup view
    }

    @PostMapping("update_chatgroup/{id}")
    public String updateChatGroup(@PathVariable Long id,
                                  @RequestParam(required = false) String newTitle,
                                  @RequestParam(required = false) String memberNames,
                                  Model model) {
        ChatGroup chatGroup = chatGroupService.getChatGroupById(id).orElseThrow(() -> new RuntimeException("ChatGroup not found"));
        model.addAttribute("chatGroup", chatGroup);
        if (newTitle != null && !newTitle.isEmpty()) {
            chatGroup.setTitle(newTitle);
        }
        if (memberNames != null && !memberNames.isEmpty()) {
            String[] names = memberNames.split(",");
            for (String memberName : names) {
                String[] nameParts = memberName.split(" ");  // Assuming names are separated by a space
                if (nameParts.length >= 2) {
                    String firstName = nameParts[0];
                    String lastName = nameParts[1];
//                    User user = userRepository.findByFirstNameAndLastNameIgnoreCase(firstName, lastName);
                    User user = userService.queryUserIgnoreCase(firstName, lastName);
                    if (user == null) {
                        // User not found, show error message
                        model.addAttribute("error", "User not found");
                        // return "redirect:/admin/update_chatgroup/{id}";  // Return to the update chatgroup page with error message
                        return "admin/update_chatgroup";
                    }
                    if (isUserInGroup(chatGroup, user)) {
                        // User already in the group, show error message
                        model.addAttribute("error", "User is already in the group");
                        // return "redirect:/admin/update_chatgroup/{id}";  // Return to the update chatgroup page with error message
                        return "admin/update_chatgroup";
                    }
                    GroupMember member = new GroupMember();
                    member.setUser(user);
                    member.setChatGroup(chatGroup);
                    chatGroup.getMembers().add(member);
                }
            }
        }

//        chatGroupRepository.save(chatGroup);
        chatGroupService.chatGroupSave(chatGroup);
        model.addAttribute("message", "ChatGroup updated successfully");
        return "redirect:/admin/chatgroups";  // Redirect back to the chatgroups view
    }

    @GetMapping("chatgroup/{id}/messages")
    public String viewChatGroupMessages(@PathVariable Long id, Model model) {
        // 根据id获取ChatGroup
//        ChatGroup chatGroup = chatGroupRepository.findById(id).orElse(null);
        ChatGroup chatGroup = chatGroupService.getChatGroupById(id).orElse(null);
        if (chatGroup == null) {
            model.addAttribute("error", "Chat group not found.");
            return "admin/chatgroups";
        }

        // 获取群聊的所有消息并按时间顺序排序
        List<GroupMessage> messages = groupMessageRepository.findByChatGroupIdOrderByTimestampDesc(id);

        model.addAttribute("chatGroup", chatGroup);
        model.addAttribute("messages", messages);

        return "admin/chatgroup_messages";
    }

    @GetMapping("chatgroup/{id}/members")
    public String viewChatGroupMembers(@PathVariable Long id, Model model) {
        // 根据id获取ChatGroup
        ChatGroup chatGroup = chatGroupService.getChatGroupById(id).orElse(null);
        if (chatGroup == null) {
            model.addAttribute("error", "Chat group not found.");
            return "admin/chatgroups";
        }

        // 获取群聊的所有成员
//        List<GroupMember> members = groupMemberRepository.findByChatGroupIdOrderByUserIdAsc(id);
        List<GroupMember> members = groupMemberService.findByChatGroupIdOrderByUserIdAsc(id);
        model.addAttribute("chatGroup", chatGroup);
        model.addAttribute("members", members);

        return "admin/chatgroup_members"; // 返回到一个新的Thymeleaf模板
    }

    private boolean isUserInGroup(ChatGroup chatGroup, User user) {
        Set<GroupMember> members = chatGroup.getMembers();
        for (GroupMember member : members) {
            if (member.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }
}
