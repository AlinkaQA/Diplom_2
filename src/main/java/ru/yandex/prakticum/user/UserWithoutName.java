package ru.yandex.prakticum.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class UserWithoutName {
    private String email;
    private String password;
}
