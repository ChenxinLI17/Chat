package fr.utc.chat.service.impl;

import fr.utc.chat.dao.UserRepository;
import fr.utc.chat.model.User;
import fr.utc.chat.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Resource
    private UserRepository userRepository;
    @Override
    public void userSave(User user){
        userRepository.save(user);
    }
    @Override
    public void userDelete(User user){
        userRepository.delete(user);
    }
    @Override
    public User getUserById(long id){
        return userRepository.findById(id);
    }
    @Override
    public Optional<User> getOptionalUserById(Long id){
        return userRepository.findOptionalById(id);
    }
    @Override
    public User getUserByMail(String mail){
       return userRepository.findByMail(mail);
    }
    @Override
    public Page<User> getInActiveUser(Pageable pageable){
        return userRepository.findAllByActive(false,pageable);
    }
    @Override
    public Page<User> queryUser(String firstName, String lastName, String mail, Pageable pageable){
        return userRepository.findByFirstNameOrLastNameOrMail(firstName,lastName,mail,pageable);
    }
    @Override
    public User queryUserIgnoreCase(String firstName, String lastName){
        return userRepository.findByFirstNameAndLastNameIgnoreCase(firstName,lastName);
    }
    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    @Override
    public Page<User> getAllUsersByPage(PageRequest pageable){
        return userRepository.findAll(pageable);
    }

}
