package ru.yandex.prakticum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.prakticum.client.UserClient;
import ru.yandex.prakticum.generator.UserGenerator;
import ru.yandex.prakticum.user.User;

import static org.hamcrest.CoreMatchers.equalTo;

public class UserUpdateTest {
    private final UserClient userClient = new UserClient();
    private final UserGenerator userGenerator = new UserGenerator();
    private User originalUser;
    private String accessToken;

    @Before
    public void setup() {
        originalUser = userGenerator.generateValidUser();
        Response response = userClient.register(originalUser);
        accessToken = response.body().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(originalUser, accessToken);
        }
    }

    @Test
    @DisplayName("Обновление данных пользователя с токеном")
    public void shouldUpdateUserWithToken() {
        User updatedUser = User.builder()
                .email("upd_" + originalUser.getEmail())
                .password(originalUser.getPassword())
                .name("Обновлённый")
                .build();

        Response response = userClient.updateUser(updatedUser, accessToken);

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Обновление данных пользователя без токена должно быть запрещено")
    public void shouldNotUpdateUserWithoutToken() {
        User updatedUser = User.builder()
                .email("upd_" + originalUser.getEmail())
                .password(originalUser.getPassword())
                .name("БезТокена")
                .build();

        Response response = userClient.updateUserWithoutToken(updatedUser);

        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

