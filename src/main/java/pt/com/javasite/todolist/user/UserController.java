package pt.com.javasite.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepo userRepository;

  @PostMapping("/")
  public ResponseEntity create(@RequestBody UserModel usermodel){
    UserModel user = this.userRepository.findByUsername(usermodel.getUsername());

    if(user!=null){
      return ResponseEntity.status(400).body("User already exist");
    }

    var passHashred = BCrypt.withDefaults().hashToString(12, usermodel.getPassword().toCharArray());

    usermodel.setPassword(passHashred);

    UserModel userCreated = this.userRepository.save(usermodel);
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
  }  
}
