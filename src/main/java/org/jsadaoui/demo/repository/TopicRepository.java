package org.jsadaoui.demo.repository;

import org.jsadaoui.demo.domain.Topic;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TopicRepository extends ReactiveMongoRepository<Topic, String> {

    Flux<Topic> findByUsersEmail(String email);
}
