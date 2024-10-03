import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.generator.IngredientsGenerator;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

@Slf4j
public class CreateOrderWithoutLoginTest {
    public static final String SUCCESS = "success";
    public static final String ORDER = "order";
    public static final String MESSAGE = "message";
    public static final String TEXT_MESSAGE_INGREDIENT_IDS_MUST_BE_PROVIDED = "Ingredient ids must be provided";

    private final OrderClient orderClient = new OrderClient();
    private final IngredientsGenerator ingredientsGenerator = new IngredientsGenerator();

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createCorrectOrderWithoutLogin() {
        Map<String, String[]> ingredientsMap = ingredientsGenerator.getCorrectIngredients();
        log.info("Список ингредиентов: {}", ingredientsMap);

        Response response = createOrderWithoutLogin(ingredientsMap);
        log.info("Получен ответ от сервера: {}", response.body().asString());

        verifySuccessfulOrderCreation(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, без ингредиентов")
    public void createOrderWithoutIngredients() {
        Map<String, String[]> ingredientsMap = ingredientsGenerator.getEmptyIngredients();
        log.info("Список ингредиентов: {}", ingredientsMap);

        Response response = createOrderWithoutLogin(ingredientsMap);
        log.info("Получен ответ от сервера: {}", response.body().asString());

        verifyOrderCreationWithoutIngredients(response);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, с некорректным хэшем ингредиентов")
    public void createOrderWithIncorrectIngredients() {
        Map<String, String[]> ingredientsMap = ingredientsGenerator.getIncorrectIngredients();
        log.info("Список ингредиентов: {}", ingredientsMap);

        Response response = createOrderWithoutLogin(ingredientsMap);
        log.info("Получен ответ от сервера: {}", response.body().asString());

        verifyOrderCreationWithIncorrectIngredients(response);
    }

    @Step("Создание заказа без авторизации")
    private Response createOrderWithoutLogin(Map<String, String[]> ingredients) {
        return orderClient.createOrderWithoutLogin(ingredients);
    }

    @Step("Проверка успешного создания заказа")
    private void verifySuccessfulOrderCreation(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true))
                .and().body(ORDER, notNullValue());
    }

    @Step("Проверка создания заказа без ингредиентов")
    private void verifyOrderCreationWithoutIngredients(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().body(SUCCESS, equalTo(false))
                .and().body(MESSAGE, equalTo(TEXT_MESSAGE_INGREDIENT_IDS_MUST_BE_PROVIDED));
    }

    @Step("Проверка создания заказа с некорректными ингредиентами")
    private void verifyOrderCreationWithIncorrectIngredients(Response response) {
        response.then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }
}
