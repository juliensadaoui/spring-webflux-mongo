package org.jsadaoui.demo.web;

import org.jsadaoui.demo.domain.Topic;
import org.jsadaoui.demo.domain.User;
import org.jsadaoui.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TopicControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    private User user;

    private WebTestClient client;

    @Before
    public void setUp() {
        mongoTemplate.dropCollection(Topic.class);
        user = userService.getCurrentUser().block();

        client = WebTestClient
                .bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .configureClient()
                .filter(basicAuthentication())
                .build();
    }

    @Test
    @WithMockUser("user@email.org")
    public void should_add_new_topic() {
        createTopic("topic");
    }

    @Test
    @WithMockUser(username = "admin@email.org", roles = { "USER", "ADMIN" })
    public void should_delete_existing_topic() {
        Topic topic = createTopic("topic");

        this.client
                .mutateWith(csrf())
                .delete()
                .uri("/topic/{topicId}", topic.getTopicId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser("user@email.org")
    public void should_get_all_topics() {
        createTopic("topic1");
        createTopic("topic2");
        createTopic("topic3");

        FluxExchangeResult<Topic> result = this.client
                .mutateWith(csrf())
                .get()
                .uri("/topic")
                .exchange()
                .expectStatus().isOk()
                .returnResult(Topic.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(Topic.builder()
                        .description("topic1")
                        .users(newHashSet(user))
                        .build())
                .expectNext(Topic.builder()
                        .description("topic2")
                        .users(newHashSet(user))
                        .build())
                .expectNext(Topic.builder()
                        .description("topic3")
                        .users(newHashSet(user))
                        .build())
                .expectComplete()
                .verify();
    }

    private Topic createTopic(String description) {
        Topic topic = Topic.builder()
                .description(description)
                .build();

        return this.client
                .mutateWith(csrf())
                .post()
                .uri("/topic")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Topic.class)
                .getResponseBody()
                .blockFirst();
    }
}
