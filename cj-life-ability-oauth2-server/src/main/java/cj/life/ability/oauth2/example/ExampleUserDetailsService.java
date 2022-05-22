package cj.life.ability.oauth2.example;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ExampleUserDetailsService implements UserDetailsService {
    final
    PasswordEncoder passwordEncoder;

    public ExampleUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if ("cj".equals(username)) {
            throw new UsernameNotFoundException("用户不存在:" + username);
        }
        String pwd = passwordEncoder.encode("123456");
        if ("wj".equals(username)) {
            UserDetails userDetails = new User(username, pwd, true, true, true, true, Arrays.asList(
                    new SimpleGrantedAuthority("commons")
            ));
            return userDetails;
        }
        UserDetails userDetails = new User(username, pwd, true, true, true, true, Arrays.asList(
                new SimpleGrantedAuthority("User"),
                new SimpleGrantedAuthority("ADMIN")
        ));
        return userDetails;

    }
}
