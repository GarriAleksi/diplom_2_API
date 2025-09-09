import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.user.UserClient;
import ru.yandex.practicum.generator.UserGenerator;
import ru.yandex.practicum.model.user.*;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import static org.hamcrest.CoreMatchers.equalTo;

@Slf4j
public class CreateUserWithInvalidDataTest {
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String REQUIRED_FIELDS_MESSAGE = "Email, password and name are required fields";

    private final UserClient userClient = new UserClient();
    private final UserGenerator userGenerator = new UserGenerator();

    /**
     * Метод для проверки неудачной регистрации пользователя.
     */
    @Step("Проверка неудачной регистрации: статус {0}, сообщение {1}")
    private void checkInvalidUserResponse(ValidatableResponse response) {
        response.statusCode(HttpStatus.SC_FORBIDDEN)
                .body(SUCCESS, equalTo(false))
                .body(MESSAGE, equalTo(REQUIRED_FIELDS_MESSAGE));
    }

    @Test
    @Description("Проверка регистрации пользователя без имени.")
    @Step("Регистрация пользователя без имени.")
    public void registerUserWithoutName() {
        log.info("Тест: Регистрация пользователя без имени.");
        UserWithoutName userWithoutName = userGenerator.createUserWithoutName();
        ValidatableResponse response = userClient.registerUserWithoutName(userWithoutName);
        checkInvalidUserResponse(response);
    }

    @Test
    @Description("Проверка регистрации пользователя с null-именем.")
    @Step("Регистрация пользователя с null-именем.")
    public void registerUserWithNameNull() {
        log.info("Тест: Регистрация пользователя с null-именем.");
        User user = userGenerator.createUserWithNameNull();
        checkInvalidUserResponse(userClient.registerUser(user).then());
    }

    @Test
    @Description("Проверка регистрации пользователя без пароля.")
    @Step("Регистрация пользователя без пароля.")
    public void registerUserWithoutPassword() {
        log.info("Тест: Регистрация пользователя без пароля.");
        UserWithoutPassword userWithoutPassword = userGenerator.createUserWithoutPassword();
        ValidatableResponse response = userClient.registerUserWithoutPassword(userWithoutPassword);
        checkInvalidUserResponse(response);
    }

    @Test
    @Description("Проверка регистрации пользователя с null-паролем.")
    @Step("Регистрация пользователя с null-паролем.")
    public void registerUserWithPasswordNull() {
        log.info("Тест: Регистрация пользователя с null-паролем.");
        User user = userGenerator.createUserWithPasswordNull();
        checkInvalidUserResponse(userClient.registerUser(user).then());
    }

    @Test
    @Description("Проверка регистрации пользователя без email.")
    @Step("Регистрация пользователя без email.")
    public void registerUserWithoutEmail() {
        log.info("Тест: Регистрация пользователя без email.");
        UserWithoutEmail userWithoutEmail = userGenerator.createUserWithoutEmail();
        ValidatableResponse response = userClient.registerUserWithoutEmail(userWithoutEmail);
        checkInvalidUserResponse(response);
    }

    @Test
    @Description("Проверка регистрации пользователя с null-email.")
    @Step("Регистрация пользователя с null-email.")
    public void registerUserWithEmailNull() {
        log.info("Тест: Регистрация пользователя с null-email.");
        User user = userGenerator.createUserWithEmailNull();
        checkInvalidUserResponse(userClient.registerUser(user).then());
    }
}
