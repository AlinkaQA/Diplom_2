package ru.yandex.prakticum;

import io.qameta.allure.Description;
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
    private User originalUser;
    private String accessToken;

    @Before
    public void setup() {
        originalUser = UserGenerator.generateValidUser();
        accessToken = userClient.register(originalUser).body().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(originalUser, accessToken);
        }
    }

    @Test
    @DisplayName("Обновление данных пользователя с токеном")
    @Description("Проверка, что авторизованный пользователь может обновить свои данные")
    public void shouldUpdateUserWithToken() {
        User updatedUser = User.builder()
                .email("upd_" + originalUser.getEmail())
                .password(originalUser.getPassword())
                .name("Обновлённый")
                .build();

        userClient.updateUser(updatedUser, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Обновление данных пользователя без токена должно быть запрещено")
    @Description("Проверка, что запрос на обновление без токена возвращает ошибку")
    public void shouldNotUpdateUserWithoutToken() {
        User updatedUser = User.builder()
                .email("upd_" + originalUser.getEmail())
                .password(originalUser.getPassword())
                .name("БезТокена")
                .build();

        userClient.updateUserWithoutToken(updatedUser)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}

