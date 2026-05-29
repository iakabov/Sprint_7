package ru.yandex.practicum.client.courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.practicum.client.Client;
import ru.yandex.practicum.model.courier.Courier;
import ru.yandex.practicum.model.courier.courierForAuth.CourierForAuth;

public class CourierClient extends Client {
    private final static String ROOT = "/courier";
    private static final String LOGIN = "/login";

    @Step("Создание курьера")
    public Response create(Courier courier) {
        return spec()
                .body(courier)
                .when()
                .post(ROOT);
    }

    @Step("Авторизация курьера")
    public Response login(CourierForAuth courierForAuth) {
        return spec()
                .body(courierForAuth)
                .when()
                .post(ROOT + LOGIN);
    }

    @Step("Удаление курьера")
    public Response delete(Integer courierId) {
        return spec()
                .delete(ROOT + String.format("/%d", courierId));
    }
}
