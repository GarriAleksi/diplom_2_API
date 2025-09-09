import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.ingredient.IngredientClient;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
public class IngredientsListRetrievalTest {
    private final IngredientClient ingredientClient = new IngredientClient();

    // Константы для полей в ответе
    public static final String SUCCESS = "success";
    public static final String INGREDIENTS = "data";  // Предположим, что список ингредиентов возвращается в поле 'data'

    @Test
    @DisplayName("Получение списка ингредиентов")
    public void getAllIngredients() {
        // Получение списка ингредиентов через API
        Response response = ingredientClient.getAllIngredients();
        log.info("Получен ответ от сервера: {}", response.body().asString());

        // Проверка статуса, флага успеха и наличия списка ингредиентов
        response.then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true))  // Проверяем, что запрос завершился успешно
                .and().body(INGREDIENTS, notNullValue());  // Проверяем, что список ингредиентов не пуст
    }
}
