package ru.yandex.prakticum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.yandex.prakticum.client.UserClient;
import ru.yandex.prakticum.generator.UserGenerator;
import ru.yandex.prakticum.user.*;

import static org.hamcrest.CoreMatchers.*;

public class UserCreateTest {
    private final UserClient userClient = new UserClient();
    private User user;
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken);
        }
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    public void shouldRegisterUserSuccessfully() {
        user = UserGenerator.generateValidUser();
        Response response = userClient.register(user);
        accessToken = response.body().path("accessToken");

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", notNullValue());
    }

    @Test
    @DisplayName("Регистрация без email должна возвращать ошибку")
    public void shouldNotRegisterUserWithoutEmail() {
        UserWithoutEmail user = UserGenerator.generateWithoutEmail();
        Response response = userClient.registerWithoutEmail(user);

        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без имени должна возвращать ошибку")
    public void shouldNotRegisterUserWithoutName() {
        UserWithoutName user = UserGenerator.generateWithoutName();
        Response response = userClient.registerWithoutName(user);

        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля должна возвращать ошибку")
    public void shouldNotRegisterUserWithoutPassword() {
        UserWithoutPassword user = UserGenerator.generateWithoutPassword();
        Response response = userClient.registerWithoutPassword(user);

        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
