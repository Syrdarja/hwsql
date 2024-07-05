package ru.netology.test;

import org.junit.jupiter.api.*;
import ru.netology.helper.DataHelper;
import ru.netology.helper.SQLHelper;
import ru.netology.page.LoginPage;

import static ru.netology.helper.SQLHelper.cleanAuthCodes;
import static ru.netology.helper.SQLHelper.cleanDatabase;
import static com.codeborne.selenide.Selenide.open;

public class LoginTest {
    LoginPage loginPage;

    @AfterEach
    void tearDown() {

        cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {

        cleanDatabase();
    }

    @BeforeEach
    void setUp() {

        loginPage = open("http://localhost:9999", LoginPage.class);
    }

    @Test
    @DisplayName("Should successfully login to dashboard with exist login and password from sut test data")
    void shouldSuccessfulLogin() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode);
    }

    @Test
    @DisplayName("Should get error notification if user is not exist in base")
    void shouldGetErrorNotificationIfLoginWithRandomUserWithoutAddingToBase() {
        var authInfo = DataHelper.generateRandomUser();
        loginPage.validLogin(authInfo);
        loginPage.verifyErrorNotification("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get error notification if login with exist in base and active user and random verification code")
    void shouldGetErrorNotificationIfLoginWithExistUserAndRandomVerificationCode() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        var verificationPage = loginPage.validLogin(authInfo);
        verificationPage.verifyVerificationPageVisiblity();
        var verificationCode = DataHelper.generateRandomVerificationCode();
        verificationPage.verify(verificationCode.getCode());
        verificationPage.verifyErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз.");

    }
}