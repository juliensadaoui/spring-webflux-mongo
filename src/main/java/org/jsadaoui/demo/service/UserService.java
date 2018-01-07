package org.jsadaoui.demo.service;

import org.jsadaoui.demo.domain.User;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> getUser(String email);

    Mono<User> getCurrentUser();

}
