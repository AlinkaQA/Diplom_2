package ru.yandex.prakticum.generator;

import org.apache.commons.lang3.RandomStringUtils;
import ru.yandex.prakticum.user.User;
import ru.yandex.prakticum.user.UserWithoutEmail;
import ru.yandex.prakticum.user.UserWithoutPassword;
import ru.yandex.prakticum.user.UserWithoutName;

public class UserGenerator {

    public static User generateValidUser() {
        return User.builder()
                .email(generateEmail())
                .password(generatePassword())
                .name(generateName())
                .build();
    }

    public static UserWithoutEmail generateWithoutEmail() {
        return UserWithoutEmail.builder()
                .password(generatePassword())
                .name(generateName())
                .build();
    }

    public static UserWithoutPassword generateWithoutPassword() {
        return UserWithoutPassword.builder()
                .email(generateEmail())
                .name(generateName())
                .build();
    }

    public static UserWithoutName generateWithoutName() {
        return UserWithoutName.builder()
                .email(generateEmail())
                .password(generatePassword())
                .build();
    }

    private static String generateEmail() {
        return "user_" + RandomStringUtils.randomAlphanumeric(6) + "@yandex.ru";
    }

    private static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private static String generateName() {
        return RandomStringUtils.randomAlphabetic(6);
    }
}