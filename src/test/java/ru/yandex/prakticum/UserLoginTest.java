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

public class UserLoginTest {
    private final UserClient userClient = new UserClient();
    private final UserGenerator userGenerator = new UserGenerator();
    private User testUser;
    private String accessToken;

    @Before
    public void setUp() {
        testUser = userGenerator.generateValidUser();
        Response response = userClient.register(testUser);
        accessToken = response.body().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(testUser, accessToken);
        }
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    public void shouldLoginSuccessfully() {
        Response response = userClient.login(testUser);
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    public void shouldNotLoginWithWrongPassword() {
        testUser.setPassword("wrongPassword");
        Response response = userClient.login(testUser);
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с пустым email")
    public void shouldNotLoginWithoutEmail() {
        testUser.setEmail(null);
        Response response = userClient.login(testUser);
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Авторизация с пустым паролем")
    public void shouldNotLoginWithoutPassword() {
        testUser.setPassword(null);
        Response response = userClient.login(testUser);
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false));
    }
}

