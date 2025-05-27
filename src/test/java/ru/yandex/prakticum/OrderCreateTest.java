package ru.yandex.prakticum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.prakticum.client.OrderClient;
import ru.yandex.prakticum.client.UserClient;
import ru.yandex.prakticum.generator.IngredientsGenerator;
import ru.yandex.prakticum.generator.UserGenerator;
import ru.yandex.prakticum.user.User;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderCreateTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final UserGenerator userGenerator = new UserGenerator();
    private final IngredientsGenerator ingredientsGenerator = new IngredientsGenerator();

    private String accessToken;
    private User user;

    @Before
    public void setup() {
        user = userGenerator.generateValidUser();
        Response response = userClient.register(user);
        accessToken = response.body().path("accessToken");
    }

    @After
    public void cleanup() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithLogin() {
        Map<String, List<String>> data = ingredientsGenerator.getValidIngredients();
        Response response = orderClient.sendOrderWithLogin(data, accessToken);

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutLogin() {
        Map<String, List<String>> data = ingredientsGenerator.getValidIngredients();
        Response response = orderClient.sendOrderWithoutLogin(data);

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        Map<String, List<String>> data = ingredientsGenerator.getEmptyIngredients();
        Response response = orderClient.sendOrderWithLogin(data, accessToken);

        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидными ингредиентами")
    public void createOrderWithInvalidIngredients() {
        Map<String, List<String>> data = ingredientsGenerator.getInvalidIngredients();
        Response response = orderClient.sendOrderWithLogin(data, accessToken);

        response.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
