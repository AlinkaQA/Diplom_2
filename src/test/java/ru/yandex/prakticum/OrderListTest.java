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

public class OrderListTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private final UserGenerator userGenerator = new UserGenerator();
    private final IngredientsGenerator ingredientsGenerator = new IngredientsGenerator();

    private String accessToken;
    private User user;

    @Before
    public void setUp() {
        user = userGenerator.generateValidUser();
        Response response = userClient.register(user);
        accessToken = response.body().path("accessToken");

        // Создаём хотя бы один заказ
        Map<String, List<String>> ingredients = ingredientsGenerator.getValidIngredients();
        orderClient.sendOrderWithLogin(ingredients, accessToken);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованным пользователем")
    public void getOrdersWithAuth() {
        Response response = orderClient.getUserOrders(accessToken);

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации должно быть запрещено")
    public void getOrdersWithoutAuth() {
        Response response = orderClient.getAnonymousOrders();

        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}
