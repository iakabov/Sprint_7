package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.model.order.Order;
import ru.yandex.practicum.service.OrderGenerator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class OrderGetTest {
    public static final String FIELD_MESSAGE = "message";
    public static final String MESSAGE_ORDER_NOT_FOUND = "Заказ не найден";
    public static final String MESSAGE_NOT_ENOUGH_DATA = "Недостаточно данных для поиска";
    public static final String FIELD_TRACK = "track";
    private final OrderClient orderClient = new OrderClient();
    private final OrderGenerator generator = new OrderGenerator();
    private final String[] color = new String[]{"black"};
    private final UtilMethods util = new UtilMethods();
    private Order order;
    private Integer trackId;

    @After
    public void delete() {
        if (trackId != null && trackId > 0) {
            util.cancelOrder(trackId);
        }
    }

    @Test
    @DisplayName("Получение заказа по номеру")
    public void getOrderByNumber() {
        createOrder();

        Response response = orderClient.getOrderByNumber(trackId);

        Order responseOrder = response.body().jsonPath().getObject("order", Order.class);

        assertEquals("Полученный заказ не соответствует запрошенному", order, responseOrder);
    }

    @Test
    @DisplayName("Получение заказа по несуществующему номеру")
    public void getOrderByNonExistentNumber() {
        Integer failTrackId = Integer.MAX_VALUE;
        Response response = orderClient.getOrderByNumber(failTrackId);

        response.then().statusCode(HttpStatus.SC_NOT_FOUND)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_ORDER_NOT_FOUND));
    }

    @Test
    @DisplayName("Получение заказа без указания номера")
    public void getOrderWithoutNumber() {
        Response response = orderClient.getOrderByNumber(null);

        response.then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().body(FIELD_MESSAGE, equalTo(MESSAGE_NOT_ENOUGH_DATA));
    }

    private void createOrder() {
        order = generator.getOrder(
                "Имя",
                "Фамилия",
                "Адрес",
                "Красные ворота",
                "79991231212",
                2,
                "2023-07-17",
                "комментарий?",
                color
        );

        Response response = orderClient.createOrder(order);
        trackId = response.body().path(FIELD_TRACK);
    }
}