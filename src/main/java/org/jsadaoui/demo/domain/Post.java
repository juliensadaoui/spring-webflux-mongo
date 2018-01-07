package org.jsadaoui.demo.domain;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Builder
@Value
public class Post {

    @Size(min = 1, max = 4000)
    private String comment;

    private LocalDate date;

    @DBRef
    private User user;
}
