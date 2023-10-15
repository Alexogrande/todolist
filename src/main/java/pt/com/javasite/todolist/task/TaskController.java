package pt.com.javasite.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import pt.com.javasite.todolist.utils.Utils;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepo taskRepo;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request){
        System.out.println("Chegou no controller" );
        taskModel.setUserId((UUID) request.getAttribute("userId"));

        var currentDate = LocalDateTime.now();

        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de inicio / fim deve ser maior que a data atual");
        }

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt())){
            System.out.println("Entrou");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("A data de fim deve ser maior que a data inicial");
        }

        var task= this.taskRepo.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
    
    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request){
        return this.taskRepo.findByUserId((UUID)request.getAttribute("userId"));
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id){
        
        var task = this.taskRepo.findById(id).orElse(null);

        if(task == null){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Tarefa não encontrada");
        }

        var userid = request.getAttribute("userId");

        if(!task.getUserId().equals(userid)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Utilizador não tem permissao para alterar a tarefa");
        }
        
        Utils.copyNonNullProperties(taskModel, task);
        return ResponseEntity.status(HttpStatus.OK).body(this.taskRepo.save(task));
    }
}
