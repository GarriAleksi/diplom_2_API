import io.qameta.allure.*;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.user.UserClient;
import ru.yandex.practicum.generator.UserGenerator;
import ru.yandex.practicum.model.user.UserWithoutEmail;
import ru.yandex.practicum.model.user.UserWithoutName;
import ru.yandex.practicum.model.user.UserWithoutPassword;

import static org.hamcrest.CoreMatchers.equalTo;

@Epic("User Login Tests")
@Feature("Login functionality")
public class UserLoginTest extends BaseTest {
    public static final String SUCCESS = "success";
    public static final String INCORRECT_EMAIL = "incorrectEmail@new.ru";
    public static final String INCORRECT_PASSWORD = "incorrect_password";
    private final UserGenerator userGenerator = new UserGenerator();
    private final UserClient userClient = new UserClient();

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that a user can log in with correct credentials")
    @Story("Valid user login")
    public void loginCorrectUser() {
        loginUserAndVerifySuccess();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login when user's name is null")
    @Story("Login with null name")
    public void loginWithNameNull() {
        user.setName(null);
        loginUserAndVerifySuccess();
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login without providing user's name")
    @Story("Login without name")
    public void loginWithoutName() {
        UserWithoutName userWithoutName = new UserWithoutName(user.getEmail(), user.getPassword());
        userClient.loginUserWithoutName(userWithoutName)
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login with null password")
    @Story("Login with null password")
    public void loginWithPasswordNull() {
        user.setPassword(null);
        userClient.loginUser(user)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login with incorrect password")
    @Story("Login with incorrect password")
    public void loginWithPasswordIncorrect() {
        user.setPassword(INCORRECT_PASSWORD);
        userClient.loginUser(user)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login without providing password")
    @Story("Login without password")
    public void loginWithoutPassword() {
        UserWithoutPassword userWithoutPassword = new UserWithoutPassword(user.getEmail(), user.getName());
        userClient.loginUserWithoutPassword(userWithoutPassword)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login with null email")
    @Story("Login with null email")
    public void loginWithEmailNull() {
        user.setEmail(null);
        userClient.loginUser(user)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify login with incorrect email")
    @Story("Login with incorrect email")
    public void loginWithEmailIncorrect() {
        user.setEmail(INCORRECT_EMAIL);
        userClient.loginUser(user)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login without providing email")
    @Story("Login without email")
    public void loginWithoutEmail() {
        UserWithoutEmail userWithoutEmail = new UserWithoutEmail(user.getPassword(), user.getName());
        userClient.loginUserWithoutEmail(userWithoutEmail)
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().body(SUCCESS, equalTo(false));
    }

    @Step("Login and verify success")
    private void loginUserAndVerifySuccess() {
        userClient.loginUser(user)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().body(SUCCESS, equalTo(true));
    }
}
