package ru.yandex.prakticum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.yandex.prakticum.client.UserClient;
import ru.yandex.prakticum.generator.UserGenerator;
import ru.yandex.prakticum.user.User;

import static org.hamcrest.CoreMatchers.equalTo;

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
    @Description("Проверка, что новый пользователь регистрируется успешно")
    public void shouldRegisterUserSuccessfully() {
        user = UserGenerator.generateValidUser();
        Response response = userClient.register(user);
        accessToken = response.body().path("accessToken");

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Регистрация без email должна возвращать ошибку")
    @Description("Проверка ошибки при регистрации без email")
    public void shouldNotRegisterUserWithoutEmail() {
        user = UserGenerator.generateValidUser();
        user.setEmail(null);
        Response response = userClient.register(user);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без имени должна возвращать ошибку")
    @Description("Проверка ошибки при регистрации без имени")
    public void shouldNotRegisterUserWithoutName() {
        user = UserGenerator.generateValidUser();
        user.setName(null);
        Response response = userClient.register(user);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация без пароля должна возвращать ошибку")
    @Description("Проверка ошибки при регистрации без пароля")
    public void shouldNotRegisterUserWithoutPassword() {
        user = UserGenerator.generateValidUser();
        user.setPassword(null);
        Response response = userClient.register(user);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Проверка, что при повторной регистрации возвращается ошибка")
    public void shouldNotRegisterExistingUser() {
        user = UserGenerator.generateValidUser();
        userClient.register(user).then().statusCode(HttpStatus.SC_OK);
        Response response = userClient.register(user);
        response.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }
}

