package pt.com.javasite.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pt.com.javasite.todolist.user.IUserRepo;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepo userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                var servletPath = request.getServletPath();
                if(servletPath.startsWith("/tasks/")){
                    var authorization = request.getHeader("Authorization");


                    var authEncode = authorization.substring(6, authorization.length());
                    byte[] authDecode = Base64.getDecoder().decode(authEncode);

                    String authString = new String(authDecode);

                    // ou podemos fazer: var pass2 = authorization.substring("Basic".length()).trim(); o trim retira os espacos 
                    System.out.println("Authorization");
                    System.out.println(authString);

                    //Get credencials
                    String[] credentials = authString.split(":");
                    String username = credentials[0];
                    String password = credentials[1];

                    //verify if credentials are valid
                    var user = this.userRepo.findByUsername(username);

                    if(user == null){
                        response.sendError(401);
                    }else{
                        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());
                        if(passwordVerify.verified){
                            request.setAttribute("userId", user.getId());
                            filterChain.doFilter(request, response);
                        }else{
                            response.sendError(401);
                        }
                    }

                    System.out.println(username);
                    System.out.println(password);
                }else{
                    filterChain.doFilter(request, response);
                }
            
    }

  
    
}
