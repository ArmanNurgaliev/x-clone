package ru.arman.backendxclone.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.arman.backendxclone.model.SecurityUser;
import ru.arman.backendxclone.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailsServerImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByNameOrEmailOrLogin(username, username, username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("No such user"));
    }
}
