package pt.com.javasite.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface IUserRepo extends JpaRepository<UserModel,UUID>{
    UserModel findByUsername(String username);
}
