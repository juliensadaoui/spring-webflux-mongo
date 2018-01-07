package org.jsadaoui.demo.security;

import org.jsadaoui.demo.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public class MongoReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final UserService userService;

    private final User.UserBuilder userBuilder;

    public MongoReactiveUserDetailsService(UserService userService, User.UserBuilder userBuilder) {
        this.userService = userService;
        this.userBuilder = userBuilder;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.getUser(username)
                .map(user -> userBuilder
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[user.getRoles().size()]))
                        .build());
    }
}
