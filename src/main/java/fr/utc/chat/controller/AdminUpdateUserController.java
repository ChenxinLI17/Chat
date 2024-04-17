package fr.utc.chat.controller;

import fr.utc.chat.component.PasswordFormatCheckService;
import fr.utc.chat.dao.ChatGroupRepository;
import fr.utc.chat.dao.GroupMemberRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.model.ChatGroup;
import fr.utc.chat.service.ChatGroupService;
import fr.utc.chat.service.GroupMemberService;
import fr.utc.chat.service.UserService;
import fr.utc.chat.util.SanitizationUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminUpdateUserController {
    @Resource
    private UserService userService;
    @Resource
    private ChatGroupService chatGroupService;
    @Resource
    private GroupMemberRepository groupMemberRepository;
    @Resource
    private GroupMemberService groupMemberService;
    @Resource
    private PasswordFormatCheckService passwordFormatCheckService;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @GetMapping("update_user/{id}")
    public String updateUser(@PathVariable long id, Model model) {
        User userToUpdate = userService.getUserById(id);
        model.addAttribute("user", userToUpdate);
        return "admin/update_user";
    }

    @PostMapping("update_user/{id}")
    public String updateUser(@ModelAttribute User user, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/update_user";
        }

        user.setFirstName(SanitizationUtil.sanitize(user.getFirstName()));
        user.setLastName(SanitizationUtil.sanitize(user.getLastName()));
        user.setMail(SanitizationUtil.sanitize(user.getMail()));

        if (!passwordFormatCheckService.isPasswordValid(user.getPassword())) {
            model.addAttribute("error", "Incorrect password format.");
            return "admin/update_user";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.getUserByMail(user.getMail()) != null && userService.getUserByMail(user.getMail()).getId() != user.getId()) {
            model.addAttribute("error", "Email address already registered.");
            return "admin/update_user";
        }
        userService.userSave(user);
        model.addAttribute("message", "User updated successfully");

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "redirect:/admin/user_list";
    }
    @PostMapping("/toggle_active/{id}")
    public ResponseEntity<Void> toggleActive(@PathVariable long id, @RequestBody Map<String, Object> payload) {
        boolean active = (boolean) payload.get("active");
        User user = userService.getUserById(id);
        if (user != null) {
            user.setActive(active);
            userService.userSave(user);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable long id, Model model, HttpSession session) {
        User userLogged = (User) session.getAttribute("loggedInUser");
        User userToDelete = userService.getUserById(id);
//        List<ChatGroup> chatGroups = chatGroupRepository.findByOwnerId(userToDelete.getId());
        List<ChatGroup> chatGroups = chatGroupService.getChatGroupByOwnerId(userToDelete.getId());
        if (!chatGroups.isEmpty()) {
            for(ChatGroup chatGroup:chatGroups){
//                groupMemberRepository.deleteByChatGroupId(chatGroup.getId());
                groupMemberService.deleteByChatGroupId(chatGroup.getId());
//                chatGroupRepository.delete(chatGroup);
                chatGroupService.chatGroupDelete(chatGroup);
            }
        }
        groupMemberService.deleteByUserId(userToDelete.getId());
//        groupMemberRepository.deleteByUserId(userToDelete.getId());
        userService.userDelete(userToDelete);
        if (userLogged.getId() == userToDelete.getId()) {
            model.addAttribute("user", new User());
            return "redirect:/login";
        } else {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("message", "User deleted unsuccessfully");
            return "redirect:/admin/user_list";
        }
    }
}
