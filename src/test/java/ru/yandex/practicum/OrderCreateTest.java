package ru.yandex.practicum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.model.order.Order;
import ru.yandex.practicum.service.OrderGenerator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static ru.yandex.practicum.constant.ScooterColorUtils.COLOR_BLACK;
import static ru.yandex.practicum.constant.ScooterColorUtils.COLOR_GREY;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    public static final String FIELD_TRACK = "track";
    private final String firstName;
    private final String lastName;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final Integer rentTime;
    private final String deliveryDate;
    private final String comment;
    private final String[] color;
    private final OrderClient orderClient = new OrderClient();
    private final OrderGenerator generator = new OrderGenerator();
    private final UtilMethods util = new UtilMethods();
    private Integer trackId;

    public OrderCreateTest(String firstName, String lastName, String address, String metroStation, String phone,
                           Integer rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    @Parameterized.Parameters(name = "{index} : color {7}")
    //индекс поля "комментарий" в массиве данных для тестирования
    public static Object[][] getParameters() {
        return new Object[][]{
                {"Коля", "Петров", "Адрес 154", "Динамо", "79261123111", 1, "2024-07-10", "black", new String[]{COLOR_BLACK}},
                {"Василий", "Сидоров", "Адрес 342", "Чистые пруды", "79992985222", 2, "2024-05-17", "grey", new String[]{COLOR_GREY}},
                {"Петр", "Фамилия 3", "Адрес 783", "Охотный ряд", "79263395833", 3, "2024-02-13", "black and grey", new String[]{COLOR_BLACK, COLOR_GREY}},
                {"Ильич", "Гогоро", "Адрес 374", "Аэропорт", "79264438644", 4, "2024-07-17", "не указан", new String[]{}},
                {"Борис", "Боборо", "Адрес 985", "Театральная", "79265556985", 5, "2024-09-12", "null", null}
        };
    }

    @After
    public void delete() {
        if (trackId != null && trackId > 0) {
            util.cancelOrder(trackId);
        }
    }

    @Test
    @DisplayName("Create order")
    public void createOrder() {
        Order order = generator.getOrder(firstName, lastName, address, metroStation, phone,
                rentTime, deliveryDate, comment, color);

        Response response = orderClient.createOrder(order);

        trackId = response.body().path(FIELD_TRACK);

        response.then().statusCode(HttpStatus.SC_CREATED)
                .and().assertThat().body(FIELD_TRACK, notNullValue());
    }
}
