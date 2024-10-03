package ru.yandex.practicum.model.Ingredient;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class IngredientData {
    private List<Ingredient> ingredients;

    public IngredientData() {
        ingredients = new ArrayList<>();
        fetchIngredients(); // Извлекаем ингредиенты при создании объекта
    }

    private void fetchIngredients() {
        Response response = RestAssured.given()
                .when()
                .get("https://stellarburgers.nomoreparties.site/api/ingredients");

        // Проверка, что ответ успешен
        if (response.statusCode() == 200) {
            // Извлекаем ингредиенты и заполняем список
            ingredients = response.jsonPath().getList("data", Ingredient.class);
        } else {
            throw new RuntimeException("Не удалось получить ингредиенты: " + response.statusLine());
        }
    }
}
