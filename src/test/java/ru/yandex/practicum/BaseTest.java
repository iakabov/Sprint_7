package ru.yandex.practicum;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.junit.Before;
import org.junit.After;
import ru.yandex.practicum.model.courier.Courier;

public class BaseTest {
    protected Courier courier;
    private final UtilMethods util = new UtilMethods();

    // 1. ДОБАВЛЯЕМ ЭТОТ МЕТОД ДЛЯ ЗАЩИТЫ ОТ ЗАВИСАНИЯ СЕРВЕРА
    @Before
    public void setUpTimeout() {
        RestAssured.config = RestAssuredConfig.config().httpClient(
                HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000) // 5 секунд на подключение
                        .setParam("http.socket.timeout", 5000)     // 5 секунд на ожидание ответа
        );
    }

    @After
    public void deleteCourier() {
        if (courier != null) {
            util.deleteCourier(courier);
        }
    }
}