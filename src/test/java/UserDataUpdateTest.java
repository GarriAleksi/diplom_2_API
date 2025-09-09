import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.client.user.UserClient;
import ru.yandex.practicum.generator.UserGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;

@Slf4j
@RunWith(Parameterized.class)
public class UserDataUpdateTest extends BaseTest {
    private static final String OLD = "old";
    private static final String NULL = "null";
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private static final String UNAUTHORIZED_MESSAGE = "You should be authorised";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";

    private final UserGenerator userGenerator = new UserGenerator();
    private final UserClient userClient = new UserClient();
    private final String email;
    private final String password;
    private final String name;
    private final String testName;

    public UserDataUpdateTest(String email, String password, String name, String testName) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "{index}: Update {3}")
    public static Object[][] getParameters() {
        return new Object[][]{
                {generateUniqueEmail(), generatePassword(), generateName(), "all fields"},
                {generateUniqueEmail(), OLD, OLD, EMAIL},
                {OLD, generatePassword(), OLD, PASSWORD},
                {OLD, OLD, generateName(), NAME},
                {generateUniqueEmail(), NULL, NULL, "email only"},
                {NULL, generatePassword(), NULL, "password only"},
                {NULL, NULL, generateName(), "name only"},
                {generateUniqueEmail(), generatePassword(), NULL, "email + password without name"},
                {NULL, generatePassword(), generateName(), "password + name without email"},
                {generateUniqueEmail(), NULL, generateName(), "email + name without password"}
        };
    }

    @Test
    @DisplayName("Update user with authorization")
    public void updateLoginUser() {
        Map<String, String> updateData = buildUpdateData(email, password, name);
        log.info("Updating user data with authorization: {}", updateData);

        Response response = userClient.updateUser(updateData, accessToken);
        log.info("Server response: {}", response.body().asString());

        response.then()
                .statusCode(HttpStatus.SC_OK)
                .body(SUCCESS, equalTo(true));
    }

    @Test
    @DisplayName("Update user without authorization")
    public void updateWithoutLogin() {
        Map<String, String> updateData = buildUpdateData(email, password, name);
        log.info("Updating user data without authorization: {}", updateData);

        Response response = userClient.updateUserWithoutLogin(updateData);
        log.info("Server response: {}", response.body().asString());

        response.then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body(SUCCESS, equalTo(false))
                .body(MESSAGE, equalTo(UNAUTHORIZED_MESSAGE));
    }

    private Map<String, String> buildUpdateData(String email, String password, String name) {
        Map<String, String> updateData = new HashMap<>();

        Optional.ofNullable(getFieldOrDefault(email, user.getEmail())).ifPresent(value -> updateData.put(EMAIL, value));
        Optional.ofNullable(getFieldOrDefault(password, user.getPassword())).ifPresent(value -> updateData.put(PASSWORD, value));
        Optional.ofNullable(getFieldOrDefault(name, user.getName())).ifPresent(value -> updateData.put(NAME, value));

        return updateData;
    }

    private String getFieldOrDefault(String field, String defaultValue) {
        if (OLD.equals(field)) {
            return defaultValue;
        }
        return NULL.equals(field) ? null : field;
    }

    private static String generateUniqueEmail() {
        return String.format("%s@%s.com", RandomStringUtils.randomAlphabetic(5), RandomStringUtils.randomAlphabetic(3));
    }

    private static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private static String generateName() {
        return RandomStringUtils.randomAlphabetic(6);
    }
}
