package at.ac.tuwien.sepm.groupphase.backend.basetest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public interface TestData {

    Long ID = 1L;
    String TEST_NEWS_TITLE = "Title";
    String TEST_NEWS_SUMMARY = "Summary";
    String TEST_NEWS_TEXT = "TestMessageText";
    LocalDateTime TEST_NEWS_PUBLISHED_AT =
        LocalDateTime.of(2019, 11, 13, 12, 15, 0, 0);

    String BASE_URI = "/api/v1";
    String LOGIN_BASE_URI = BASE_URI + "/login";
    String USER_BASE_URI = BASE_URI + "/users";
    String MESSAGE_BASE_URI = BASE_URI + "/messages";
    String VERIFY_ACCOUNT_URI = "http://localhost:4200/#/account/login/key?key=";

    String ADMIN_USER = "admin@email.com";
    String VALID_USERNAME = "okokokok";
    String VALID_EMAIL = "ok@ok.ok";
    String VALID_PASSWORD = "okokokok";
    Locale UNKNOWN_LOCALE = Locale.KOREAN;
    List<String> ADMIN_ROLES = new ArrayList<>() {
        {
            add("ROLE_ADMIN");
            add("ROLE_USER");
        }
    };
    String DEFAULT_USER = "admin@email.com";
    List<String> USER_ROLES = new ArrayList<>() {
        {
            add("ROLE_USER");
        }
    };

}
