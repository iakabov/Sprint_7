package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.courier.CourierClient;
import ru.yandex.practicum.model.courier.Courier;
import ru.yandex.practicum.service.CourierGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class DeleteCourierTest {
    private final CourierGenerator generator = new CourierGenerator();
    private final CourierClient courierClient = new CourierClient();
    private static final String FIELD_MESSAGE = "message";
    private static final String FIELD_OK = "ok";
    private static final String MESSAGE_NOT_FOUND = "Курьера с таким id нет.";
    private Integer id = 0;

    @Test
    @DisplayName("Удаление курьера")
    public void deleteCourier() {
        Courier courier = generator.getCourier();
        courierClient.create(courier);
        id = courierClient.login(generator.getCourierForAuth(courier)).body().path("id");

        Response response = courierClient.delete(id);

        response.then().assertThat().body(FIELD_OK, equalTo(true))
                .and().statusCode(HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("Удаление курьера по несуществующему id")
    public void deleteNonExistent() {
        id = Integer.MIN_VALUE;
        Response response = courierClient.delete(id);

        response.then().statusCode(HttpStatus.SC_NOT_FOUND)
                .and().assertThat().body(FIELD_MESSAGE, equalTo(MESSAGE_NOT_FOUND));
    }
}
