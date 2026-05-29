package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.courier.CourierClient;
import ru.yandex.practicum.model.courier.Courier;
import ru.yandex.practicum.service.CourierGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class CourierCreateTest extends BaseTest {
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_OK = "ok";
    private static final String MESSAGE_EXISTING_LOGIN = "Этот логин уже используется. Попробуйте другой.";
    private static final String MESSAGE_WITHOUT_REQUIRED_FIELDS = "Недостаточно данных для создания учетной записи";
    private final CourierGenerator generator = new CourierGenerator();
    private final CourierClient courierClient = new CourierClient();

    @Test
    @DisplayName("Создание курьера с параметрами логин, пароль, имя")
    public void createCourier() {
        courier = generator.getCourier();

        Response response = courierClient.create(courier);

        response.then().assertThat().statusCode(HttpStatus.SC_CREATED)
                .and().body(FIELD_OK, equalTo(true));
    }

    @Test
    @DisplayName("Создание курьера с существующим логином")
    public void createCourierWithExistingLogin() {
        courier = generator.getCourier();

        courierClient.create(courier);
        Response conflictResponse = courierClient.create(courier);

        conflictResponse.then().statusCode(HttpStatus.SC_CONFLICT)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_EXISTING_LOGIN));
    }

    @Test
    @DisplayName("Создание курьера без поля name")
    public void createCourierWithoutName() {
        courier = generator.getCourier();

        Response response = courierClient.create(courier);

        response.then().statusCode(HttpStatus.SC_CREATED)
                .and().body(FIELD_OK, equalTo(true));
    }

    @Test
    @DisplayName("Создание курьера без поля password")
    public void createCourierWithoutPassword() {
        Courier courier = new Courier("login", null, "firstName");

        Response response = courierClient.create(courier);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_WITHOUT_REQUIRED_FIELDS));
    }

    @Test
    @DisplayName("Создание курьера с password = null")
    public void createCourierWithPasswordNull() {
        Courier courier = generator.getCourierWithPasswordNull();

        Response response = courierClient.create(courier);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body(FIELD_MESSAGE, equalTo(MESSAGE_WITHOUT_REQUIRED_FIELDS));
    }

    @Test
    @DisplayName("Создание курьера без поля login")
    public void createCourierWithoutLogin() {
        Courier courier = new Courier(null, "password", "firstName");

        Response response = courierClient.create(courier);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body(FIELD_MESSAGE, equalTo(MESSAGE_WITHOUT_REQUIRED_FIELDS));
    }

    @Test
    @DisplayName("Создание курьера с login = null")
    public void createCourierWithLoginNull() {
        Courier courier = generator.getCourierWithLoginNull();

        Response response = courierClient.create(courier);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body(FIELD_MESSAGE, equalTo(MESSAGE_WITHOUT_REQUIRED_FIELDS));
    }
}
