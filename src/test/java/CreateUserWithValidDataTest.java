import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.user.UserClient;
import ru.yandex.practicum.generator.UserGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

@Slf4j
public class CreateUserWithValidDataTest extends BaseTest {
    public static final String SUCCESS = "success";
    public static final String MESSAGE = "message";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String TEXT_MESSAGE_EXISTS = "User already exists";

    private final UserClient userClient = new UserClient();
    private final UserGenerator userGenerator = new UserGenerator();

    @Test
    @DisplayName("Регистрация валидного пользователя")
    public void registerUniqueUser() {
        // Генерация нового уникального пользователя
        user = userGenerator.createUser();
        log.info("Попытка создания нового пользователя: {}", user);

        // Отправка запроса на регистрацию
        Response response = userClient.registerUser(user);
        log.info("Ответ от сервера: {}", response.body().asString());

        // Извлечение accessToken и проверка на наличие токена
        accessToken = response.body().path(ACCESS_TOKEN);
        if (accessToken == null) {
            log.error("Токен не был получен при регистрации пользователя");
        }

        // Проверка успешного ответа
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true));
    }

    @Test
    @DisplayName("Повторная регистрация уже существующего пользователя")
    public void registerUserWithExistingEmail() {
        // Генерация пользователя и его регистрация
        user = userGenerator.createUser();
        Response response = userClient.registerUser(user);
        log.info("Ответ от сервера на первую регистрацию: {}", response.body().asString());

        // Сохранение токена после первой успешной регистрации
        accessToken = response.body().path(ACCESS_TOKEN);
        if (accessToken == null) {
            log.error("Токен не был получен при первой регистрации пользователя");
        }

        // Проверка успешной регистрации
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true));

        // Повторная регистрация того же пользователя
        Response badResponse = userClient.registerUser(user);
        log.info("Попытка повторной регистрации существующего пользователя: {}", user);
        log.info("Ответ от сервера на повторную регистрацию: {}", badResponse.body().asString());

        // Проверка корректного сообщения о повторной регистрации
        badResponse.then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and().body(SUCCESS, equalTo(false))
                .and().body(MESSAGE, equalTo(TEXT_MESSAGE_EXISTS));
    }
}
