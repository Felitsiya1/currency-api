package ru.cofee.house.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.cofee.house.core.dto.UserDto;
import ru.cofee.house.core.dto.mapper.CoffeeHouseMapper;
import ru.cofee.house.model.auth.Role;
import ru.cofee.house.repository.UserRepo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepository;

    private final CoffeeHouseMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailsServiceImpl(UserRepo userRepository, CoffeeHouseMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Optional<ru.cofee.house.model.auth.User> user
                = userRepository.findByUsername(username);
        if (user.isPresent()) {
            final String dbUsername = user.get().getUsername();
            final String dbPassword = user.get().getPassword();
            final Set<Role> roles = user.get().getRoles();
            return new User(dbUsername, dbPassword, roles);
        } else {
            throw new UsernameNotFoundException("not correct username");
        }
    }

    public Optional<ru.cofee.house.model.auth.User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void saveUserDto(UserDto dto) {
        ru.cofee.house.model.auth.User user = mapper.map(dto, ru.cofee.house.model.auth.User.class);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(Role.USER));
        userRepository.save(user);
    }

    public List<UserDto> findAllUsers() {
        return mapper.mapAsList(userRepository.findAll(), UserDto.class);
    }

    public boolean isIamAdmin(Authentication authentication) {
        if (authentication != null)
            return
                    authentication.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> {
                                String authority = grantedAuthority.getAuthority();
                                return authority
                                        .equals(Role.ADMIN.getAuthority());
                            });
        return false;
    }
}
