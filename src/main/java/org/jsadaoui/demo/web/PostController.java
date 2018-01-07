package org.jsadaoui.demo.web;

import org.jsadaoui.demo.domain.Post;
import org.jsadaoui.demo.service.TopicService;
import org.jsadaoui.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/topic/{topicId}/post")
public class PostController {

    private final TopicService topicService;

    private final UserService userService;

    public PostController(TopicService topicService, UserService userService) {
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping
    public Flux<Post> list(@PathVariable("topicId") String topicId) {
        return topicService.getPostsByTopic(topicId);
    }

    @PostMapping
    public Mono<ResponseEntity<Post>> add(@PathVariable("topicId") String topicId, @RequestBody String message) {
        return userService.getCurrentUser()
                .flatMap(user -> topicService.newPost(message, topicId, user))
                .map(savedPost -> new ResponseEntity<>(savedPost, HttpStatus.CREATED));
    }
}
