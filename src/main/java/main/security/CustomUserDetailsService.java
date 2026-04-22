package main.security;

import main.repository.UsersRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE_PATTERN = "user with email {0} not found";
    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return usersRepository.findUserByEmail(email)
                .map(user -> new CustomUserDetails(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getRole().getAuthorities()
                ))
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format(USER_NOT_FOUND_MESSAGE_PATTERN, email)));
    }
}
