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

public class UserLoginTest {
    private final UserClient userClient = new UserClient();
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        testUser = UserGenerator.generateValidUser();
        accessToken = userClient.register(testUser).body().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(testUser, accessToken);
        }
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Проверка, что пользователь с корректными данными может войти")
    public void shouldLoginSuccessfully() {
        userClient.login(testUser)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("Проверка текста ошибки при неверном пароле")
    public void shouldNotLoginWithWrongPassword() {
        testUser.setPassword("wrongPassword");
        userClient.login(testUser)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без email")
    @Description("Проверка текста ошибки при пустом email")
    public void shouldNotLoginWithoutEmail() {
        testUser.setEmail(null);
        userClient.login(testUser)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверка текста ошибки при пустом пароле")
    public void shouldNotLoginWithoutPassword() {
        testUser.setPassword(null);
        userClient.login(testUser)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
