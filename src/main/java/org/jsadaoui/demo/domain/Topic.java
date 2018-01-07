package org.jsadaoui.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Document
@Builder
@Data
@EqualsAndHashCode(exclude = "topicId")
@CompoundIndex(name = "topic_ref_to_user", def = "{'users.email' : 1}")
public class Topic {

    @Id
    private String topicId;

    @Size(min = 1, max = 255)
    private String description;

    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    @DBRef
    @Builder.Default
    private Set<User> users = new HashSet<>();

}
