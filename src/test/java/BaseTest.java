import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import ru.yandex.practicum.client.user.UserClient;
import ru.yandex.practicum.generator.UserGenerator;
import ru.yandex.practicum.model.user.User;

@Slf4j
public class BaseTest {
    public static final String ACCESS_TOKEN = "accessToken";
    protected final UserClient userClient = new UserClient();
    protected final UserGenerator userGenerator = new UserGenerator();
    protected User user;
    protected String accessToken;
    protected Response responseCreateUser;

    @Before
    public void createUser() {
        user = userGenerator.createUser(); // Генерация нового пользователя
        responseCreateUser = userClient.registerUser(user); // Регистрация пользователя
        accessToken = responseCreateUser.body().path(ACCESS_TOKEN); // Извлечение токена
    }

    @After
    public void deleteUser() {
        if (user != null) {
            try {
                Response response = userClient.deleteUser(user, accessToken); // Удаление пользователя
                if (response.statusCode() != 202) { // Проверка статуса ответа
                    throw new RuntimeException("Не удалось удалить пользователя. Код статуса: " + response.statusCode());
                }
                log.info("Пользователь успешно удален.");
            } catch (Exception e) {
                log.error("Произошла ошибка при удалении пользователя: {}", e.getMessage());
                throw new RuntimeException("Ошибка при удалении пользователя", e); // Бросаем исключение, чтобы тест упал
            }
        } else {
            log.warn("Пользователь для удаления равен null.");
        }
    }
}
