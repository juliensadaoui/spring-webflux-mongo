package org.jsadaoui.demo.web;

import org.jsadaoui.demo.domain.Topic;
import org.jsadaoui.demo.service.TopicService;
import org.jsadaoui.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final UserService userService;

    private final TopicService topicService;

    public TopicController(UserService userService, TopicService topicService) {
        this.userService = userService;
        this.topicService = topicService;
    }

    @GetMapping
    public Flux<Topic> list() {
        return userService.getCurrentUser()
                .flatMapMany(user -> topicService.getTopicsByUser(user.getEmail()));
    }

    @PostMapping
    public Mono<ResponseEntity<Topic>> add(@Valid @RequestBody Topic topic) {
        return userService.getCurrentUser()
                .flatMap(user -> topicService.newTopic(topic, user))
                .map(savedTopic -> new ResponseEntity<>(savedTopic, HttpStatus.CREATED));
    }

    @DeleteMapping("/{topicId}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String topicId) {
        return topicService.deleteTopic(topicId)
                .map(ResponseEntity::ok);
    }

}