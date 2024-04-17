package fr.utc.chat.service;

import fr.utc.chat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void userSave(User user);
    void userDelete(User user);
    User getUserById(long id);
    Optional<User> getOptionalUserById(Long id);
    User getUserByMail(String mail);
    Page<User> getInActiveUser(Pageable pageable);
    Page<User> queryUser(String firstName, String lastName, String mail, Pageable pageable);
    User queryUserIgnoreCase(String firstName, String lastName);
    List<User> getAllUsers();
    Page<User> getAllUsersByPage(PageRequest pageable);


}
