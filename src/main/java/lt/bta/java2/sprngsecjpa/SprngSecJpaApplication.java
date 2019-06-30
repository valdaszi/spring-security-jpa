package lt.bta.java2.sprngsecjpa;

import lombok.Data;
import lt.bta.java2.sprngsecjpa.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class SprngSecJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprngSecJpaApplication.class, args);
    }

}

@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // disabiname csrf
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()

                .and()
                .formLogin()

                .and()
                .logout()
                .logoutSuccessUrl("/")  // nurodytas URL į kurį nueis po sėkmingo logout'o - pagal nutylėjimą atidaromas login langas
        ;
    }

}

@Service
class AppUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("Sorry no :)");

        return user;
    }
}


interface UserRepository extends PagingAndSortingRepository<User, Integer> {
    User findByUsername(String username);
}


@Configuration
class MVCConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/error").setViewName("error");
    }
}

@Controller
class Ctrl {

    @PreAuthorize("hasAuthority('A') OR hasAuthority('B')")
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/any")
    public String any(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("principal", principal);
        model.addAttribute("authentication", authentication);
        model.addAttribute("userDetail", authentication.getPrincipal());
        return "any";
    }

    @PermitAll
    @RequestMapping(path = "/open", method = {RequestMethod.GET, RequestMethod.PUT})
    public String open(Principal principal) {
        //UserDetails appUserDetail = (UserDetails) principal;
        Object cart = getCart(principal);
        System.out.println(principal);
        return "open";
    }


    private Object getCart(Principal principal) {
        if (principal instanceof UserDetails) {
            //TODO kai useris prisilogines
        } else {
            //TODO kai ne
        }
        return null;
    }



    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RolesAllowed("ADMIN")
    @PostMapping("/create")
    @ResponseBody
    public Map createUser(@RequestBody UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return Collections.singletonMap("id", user.getId());
    }
}

@Data
class UserRequest {
    private String username;
    private String password;
}





