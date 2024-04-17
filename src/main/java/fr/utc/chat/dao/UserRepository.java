package fr.utc.chat.dao;

import fr.utc.chat.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//在这个接口使用JPA实现增删改查
//-T:操作的实体类
//-ID:操作的实体类中的主键的数据类型

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findById(long id);
    Optional<User> findOptionalById(Long id);
    User findByMail(String mail);
    Page<User> findAllByActive(boolean active, Pageable pageable);
    Page<User> findByFirstNameOrLastNameOrMail(String firstName, String lastName, String mail, Pageable pageable);
    User findByFirstNameAndLastNameIgnoreCase(String firstName, String lastName);

}
