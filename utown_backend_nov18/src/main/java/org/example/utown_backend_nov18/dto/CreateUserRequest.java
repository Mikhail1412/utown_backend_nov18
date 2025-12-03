package org.example.utown_backend_nov18.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Имя обязательно")
    @Pattern(regexp = "^[\\p{L}\\s-]+$", message = "Имя: только буквы / пробел / дефис")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна")
    @Pattern(regexp = "^[\\p{L}\\s-]+$", message = "Фамилия: только буквы / пробел / дефис")
    private String lastName;

    @NotNull(message = "Возраст обязателен")
    @Min(value = 1, message = "Возраст должен быть положительным числом")
    private Integer age;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    private String password;

    @NotEmpty(message = "Нужно выбрать хотя бы одну роль")
    private List<Long> roleIds;
}
