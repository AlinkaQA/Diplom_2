package ru.yandex.prakticum.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.prakticum.user.User;
import ru.yandex.prakticum.user.UserWithoutEmail;
import ru.yandex.prakticum.user.UserWithoutName;
import ru.yandex.prakticum.user.UserWithoutPassword;

public class UserClient extends Client {

    private static final String ENDPOINT = "/auth";
    private static final String HEADER_AUTH = "Authorization";

    @Step("Регистрация пользователя")
    public Response register(User user) {
        return specification()
                .body(user)
                .post(ENDPOINT + "/register");
    }

    @Step("Регистрация без email")
    public Response registerWithoutEmail(UserWithoutEmail user) {
        return specification()
                .body(user)
                .post(ENDPOINT + "/register");
    }

    @Step("Регистрация без пароля")
    public Response registerWithoutPassword(UserWithoutPassword user) {
        return specification()
                .body(user)
                .post(ENDPOINT + "/register");
    }

    @Step("Регистрация без имени")
    public Response registerWithoutName(UserWithoutName user) {
        return specification()
                .body(user)
                .post(ENDPOINT + "/register");
    }

    @Step("Авторизация пользователя")
    public Response login(User user) {
        return specification()
                .body(user)
                .post(ENDPOINT + "/login");
    }

    @Step("Получение информации о пользователе")
    public Response getUserInfo(String accessToken) {
        return specification()
                .header(HEADER_AUTH, accessToken)
                .get(ENDPOINT + "/user");
    }

    @Step("Обновление данных пользователя с токеном")
    public Response updateUser(User user, String accessToken) {
        return specification()
                .header(HEADER_AUTH, accessToken)
                .body(user)
                .patch(ENDPOINT + "/user");
    }

    @Step("Обновление данных пользователя без токена")
    public Response updateUserWithoutToken(User user) {
        return specification()
                .body(user)
                .patch(ENDPOINT + "/user");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(User user, String accessToken) {
        return specification()
                .header(HEADER_AUTH, accessToken)
                .body(user)
                .delete(ENDPOINT + "/user");
    }
}