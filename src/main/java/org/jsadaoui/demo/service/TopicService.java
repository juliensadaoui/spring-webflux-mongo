package org.jsadaoui.demo.service;

import org.jsadaoui.demo.domain.Post;
import org.jsadaoui.demo.domain.Topic;
import org.jsadaoui.demo.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TopicService {

    Mono<Topic> newTopic(Topic topic, User user);

    Mono<Post> newPost(String topicId, String comment, User user);

    Mono<Void> deleteTopic(String topicId);

    Flux<Post> getPostsByTopic(String topicId);

    Flux<Topic> getTopicsByUser(String email);

}
