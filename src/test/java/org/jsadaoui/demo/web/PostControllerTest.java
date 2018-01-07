package org.jsadaoui.demo.web;

import org.jsadaoui.demo.domain.Post;
import org.jsadaoui.demo.domain.Topic;
import org.jsadaoui.demo.domain.User;
import org.jsadaoui.demo.service.TopicService;
import org.jsadaoui.demo.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class PostControllerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    private WebTestClient client;

    private User user;

    private String topicId;

    @Before
    public void setUp() {
        user = userService.getCurrentUser().block();

        Topic topic = topicService.newTopic(Topic.builder()
                        .description("topic")
                        .build(),
                        user)
                .block();

        assertThat(topic).as("Topic created ?").isNotNull();
        topicId = topic.getTopicId();

        client = WebTestClient
                .bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .configureClient()
                .filter(basicAuthentication())
                .build();
    }

    @Test
    @WithMockUser("user@email.org")
    public void should_add_new_post() {
        createPost("bar");
    }

    @Test
    @WithMockUser("user@email.org")
    public void should_get_all_posts() {
        createPost("foo");
        createPost("bar");

        FluxExchangeResult<Post> result = this.client
                .mutateWith(csrf())
                .get()
                .uri("/topic/{topicId}/post", topicId)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Post.class);

        StepVerifier.create(result.getResponseBody())
                .expectNext(Post.builder().comment("foo").date(LocalDate.now()).user(user).build())
                .expectNext(Post.builder().comment("bar").date(LocalDate.now()).user(user).build())
                .expectComplete()
                .verify();
    }

    private void createPost(String comment) {

        this.client
                .mutateWith(csrf())
                .post()
                .uri("/topic/{topicId}/post", topicId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(comment), String.class)
                .exchange()
                .expectStatus().isCreated();
    }

}
