package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;
import ru.yandex.practicum.client.order.OrderClient;

import static org.hamcrest.Matchers.notNullValue;

public class OrdersTest {
    public static final String FIELD_ORDERS = "orders";
    OrderClient orderClient = new OrderClient();

    @Test
    @DisplayName("Получение списка заказов")
    public void getOrders() {
        Response response = orderClient.getOrders();

        response.then().statusCode(HttpStatus.SC_OK)
                .and().assertThat().body(FIELD_ORDERS, notNullValue());
    }
}
