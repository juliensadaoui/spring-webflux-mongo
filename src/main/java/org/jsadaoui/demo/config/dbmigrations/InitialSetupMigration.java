package org.jsadaoui.demo.config.dbmigrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.jsadaoui.demo.domain.User;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Creates the initial database setup
 */
@ChangeLog(order = "001")
public class InitialSetupMigration {

    @ChangeSet(order = "01", author = "jsadaoui-demo", id = "01-addUsers")
    public void addUsers(MongoTemplate mongoTemplate) {
        mongoTemplate.save(User.builder()
                .email("user@email.org")
                .password("Pa$$word1")
                .firstName("user_first-name")
                .lastName("user_last-name")
                .role("USER")
                .build());
        mongoTemplate.save(User.builder()
                .email("admin@email.org")
                .password("Pa$$word1")
                .firstName("admin_first-name")
                .lastName("admin_last-name")
                .role("USER")
                .role("ADMIN")
                .build());
    }
}
