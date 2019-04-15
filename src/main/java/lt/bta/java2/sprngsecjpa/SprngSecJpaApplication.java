package lt.bta.java2.sprngsecjpa;

import com.sun.security.auth.UserPrincipal;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
public class SprngSecJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprngSecJpaApplication.class, args);
    }

}

@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/").permitAll()

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

        return new AppUserDetail(user);
    }
}


class AppUserDetail implements UserDetails {

    private User user;

    AppUserDetail(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getSecret();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public int getId() {
        return user.getId();
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

    @PreAuthorize("hasAuthority('USER') OR hasAuthority('ADMIN')")
    @GetMapping("/user")
    public String user() {
        return "user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/any")
    public String any(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("principal", principal);
        model.addAttribute("authentication", authentication);

        AppUserDetail userDetail = (AppUserDetail) authentication.getPrincipal();
        model.addAttribute("userDetail", userDetail);

        return "any";
    }
}





