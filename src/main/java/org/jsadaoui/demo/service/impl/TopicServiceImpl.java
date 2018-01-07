package org.jsadaoui.demo.service.impl;

import org.jsadaoui.demo.domain.Post;
import org.jsadaoui.demo.domain.Topic;
import org.jsadaoui.demo.domain.User;
import org.jsadaoui.demo.repository.TopicRepository;
import org.jsadaoui.demo.service.TopicService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Mono<Topic> newTopic(Topic topic, User user) {
        topic.getUsers().add(user);
        return topicRepository.save(topic);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteTopic(String topicId) {
        return topicRepository.findById(topicId)
                .flatMap(topicRepository::delete);
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public Mono<Post> newPost(String message, String comment, User user) {
        return topicRepository.findById(comment)
                .flatMap(topic -> {
                    Post post = Post.builder()
                            .comment(message)
                            .date(LocalDate.now())
                            .user(user)
                            .build();
                    topic.getPosts().add(post);
                    topic.getUsers().add(user);
                    return topicRepository.save(topic)
                            .then(Mono.just(post));
                });
    }

    @Override
    public Flux<Post> getPostsByTopic(String topicId) {
        return topicRepository.findById(topicId)
                .flatMapMany(todoList -> Flux.fromIterable(todoList.getPosts()));
    }

    @Override
    public Flux<Topic> getTopicsByUser(String email) {
        return topicRepository.findByUsersEmail(email);
    }
}
