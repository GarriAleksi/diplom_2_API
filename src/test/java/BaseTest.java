import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
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
    @Step("Генерация нового пользователя перед тестом")
    @Description("Создание нового пользователя для теста и получение токена доступа.")
    public void createUser() {
        user = userGenerator.createUser(); // Генерация нового пользователя
        responseCreateUser = userClient.registerUser(user); // Регистрация пользователя
        accessToken = responseCreateUser.body().path(ACCESS_TOKEN); // Извлечение токена
    }

    @After
    @Step("Удаление пользователя после теста")
    @Description("Удаление созданного пользователя и проверка успешности операции.")
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

    @Test
    @Step("Тестирование создания и удаления пользователя")
    @Description("Проверка, что пользователь успешно создается и удаляется.")
    public void userCreationAndDeletionTest() {
        // Здесь вы можете добавить код теста, который использует созданного пользователя
    }
}
