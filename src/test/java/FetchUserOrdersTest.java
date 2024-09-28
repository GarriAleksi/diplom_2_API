import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.generator.IngredientsGenerator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@Slf4j  // Добавляем @Slf4j здесь для логирования
public class FetchUserOrdersTest extends BaseTest {
    private final IngredientsGenerator ingredientsGenerator = new IngredientsGenerator();
    private final OrderClient orderClient = new OrderClient();

    // Константы для ответов API
    private static final String SUCCESS = "success";
    private static final String ORDERS = "orders";
    private static final String MESSAGE = "message";
    private static final String AUTH_ERROR_MESSAGE = "You should be authorised";

    @Test
    public void getOrdersWithAuth() {
        // Создание заказа перед проверкой получения списка заказов
        orderClient.createOrderWithLogin(ingredientsGenerator.getCorrectIngredients(), accessToken);

        // Получение заказов с авторизацией
        Response response = orderClient.getOrdersWithAuth(accessToken);

        // Логгирование ответа
        log.info("Ответ на запрос заказов с авторизацией: {}", response.body().asString());

        // Проверка успешного ответа и наличия заказов
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true))
                .and().body(ORDERS, notNullValue());

        // Дополнительно можно проверить, что заказы действительно есть
        int ordersCount = response.body().path(ORDERS + ".size()");
        log.info("Количество заказов: {}", ordersCount);
    }

    @Test
    public void getOrdersWithoutAuth() {
        // Получение заказов без авторизации
        Response response = orderClient.getOrdersWithoutAuth();

        // Логгирование ответа
        log.info("Ответ на запрос заказов без авторизации: {}", response.body().asString());

        // Проверка, что запрос без авторизации возвращает ошибку
        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false))
                .and().body(MESSAGE, equalTo(AUTH_ERROR_MESSAGE));
    }
}
