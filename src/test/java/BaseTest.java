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
        try {
            user = userGenerator.createUser(); // Генерация пользователя
            responseCreateUser = userClient.registerUser(user); // Регистрация пользователя
            if (responseCreateUser.statusCode() == 200) { // Проверка успешности регистрации
                accessToken = responseCreateUser.body().path(ACCESS_TOKEN); // Извлечение токена
                log.info("Пользователь успешно создан. Токен: {}", accessToken);
            } else {
                log.error("Не удалось создать пользователя. Код статуса: {}", responseCreateUser.statusCode());
            }
        } catch (Exception e) {
            log.error("Произошла ошибка при создании пользователя: {}", e.getMessage());
        }
    }

    @After
    public void deleteUser() {
        if (user != null && accessToken != null) { // Проверка существования пользователя и токена
            try {
                Response response = userClient.deleteUser(user, accessToken); // Удаление пользователя
                if (response.statusCode() == 200) {
                    log.info("Пользователь успешно удален.");
                } else {
                    log.error("Не удалось удалить пользователя. Код статуса: {}", response.statusCode());
                }
            } catch (Exception e) {
                log.error("Произошла ошибка при удалении пользователя: {}", e.getMessage());
            }
        } else {
            log.warn("Пользователь или токен равны null, удаление пропущено.");
        }
    }
}
