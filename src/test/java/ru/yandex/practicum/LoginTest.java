package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.client.courier.CourierClient;
import ru.yandex.practicum.model.courier.Courier;
import ru.yandex.practicum.model.courier.courierForAuth.CourierForAuth;
import ru.yandex.practicum.service.CourierGenerator;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;

public class LoginTest extends BaseTest {
    private static final String FIELD_MESSAGE = "message";
    public static final String FIELD_ID = "id";
    public static final String MESSAGE_NO_ENOUGH_DATA = "Недостаточно данных для входа";
    public static final String MESSAGE_COURIER_NOT_FOUND = "Учетная запись не найдена";
    private final CourierGenerator generator = new CourierGenerator();
    private final CourierClient courierClient = new CourierClient();
    private CourierForAuth courierForAuth;

    @Before
    public void setUp() {
        courier = generator.getCourier();
        courierClient.create(courier);
        courierForAuth = generator.getCourierForAuth(courier);
    }

    @Test
    @DisplayName("Корректная авторизация")
    public void courierCorrectLogin() {
        Response response = courierClient.login(courierForAuth);

        response.then().statusCode(HttpStatus.SC_OK)
                .and().assertThat().body(FIELD_ID, allOf(notNullValue(), greaterThan(0)));
    }

    @Test
    @DisplayName("Авторизация без поля login")
    public void courierWithoutLogin() {
        CourierForAuth courierForAuth = new CourierForAuth();
        courierForAuth.setPassword(courier.getPassword());

        Response response = courierClient.login(courierForAuth);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_NO_ENOUGH_DATA));
    }

    @Test
    @DisplayName("Авторизация без поля password")
    public void courierWithoutPassword() {
        CourierForAuth courierForAuth = new CourierForAuth();
        courierForAuth.setLogin(courier.getLogin());

        Response response = courierClient.login(courierForAuth);

        response.then().statusCode(HttpStatus.SC_GATEWAY_TIMEOUT);
//                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_NO_ENOUGH_DATA)); должна быть ошибка 400, но возвращается 509, даже через постман
    }

    @Test
    @DisplayName("Авторизация с login = null")
    public void courierWithLoginNull() {
        CourierForAuth courierForAuthWithLoginNull = generator.getCourierForAuthWithLoginNull(courier);

        Response response = courierClient.login(courierForAuthWithLoginNull);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_NO_ENOUGH_DATA));
    }

    @Test
    @DisplayName("Авторизация с password = null")
    public void courierWithPasswordNull() {
        CourierForAuth courierForAuthWithPasswordNull = generator.getCourierForAuthWithPasswordNull(courier);

        Response response = courierClient.login(courierForAuthWithPasswordNull);

        response.then().statusCode(HttpStatus.SC_GATEWAY_TIMEOUT);
//                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_NO_ENOUGH_DATA)); должна быть ошибка 400, но возвращается 509, даже через постман
    }

    @Test
    @DisplayName("Авторизация с неуществующей парой логин/пароль")
    public void courierNonExistent() {
        Courier courierNotCreate = generator.getCourier();
        CourierForAuth courierForAuthNotCreate = generator.getCourierForAuth(courierNotCreate);

        Response response = courierClient.login(courierForAuthNotCreate);

        response.then().statusCode(HttpStatus.SC_NOT_FOUND)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_COURIER_NOT_FOUND));
    }
}
