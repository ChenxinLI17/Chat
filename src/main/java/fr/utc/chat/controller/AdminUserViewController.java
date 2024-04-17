package fr.utc.chat.controller;

import fr.utc.chat.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("admin")
public class AdminUserViewController {
    @Resource
    private UserService userService;

    @GetMapping("user_list")
    public String userList(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "8") int size,
                           @RequestParam(name = "sortField", defaultValue = "id") String sortField,
                           @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder,
                           @RequestParam(name = "search", defaultValue = "") String search,
                           @RequestParam(name = "active", defaultValue = "true") boolean active) {

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<User> users;

        if (search.isEmpty()) {
            if (active) {
                users = userService.getAllUsersByPage(pageable);
            } else {
                users = userService.getInActiveUser(pageable);
            }
        } else {
            //users = userRepository.findByFirstNameOrLastNameOrMail(search, search, search, pageable);
            users = userService.queryUser(search, search, search, pageable);
        }

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("search", search);
        model.addAttribute("active", active);

        return "admin/user_list";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

}
