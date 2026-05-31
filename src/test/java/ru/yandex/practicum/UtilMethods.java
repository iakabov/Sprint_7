package ru.yandex.practicum;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import ru.yandex.practicum.client.courier.CourierClient;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.model.courier.Courier;
import ru.yandex.practicum.model.courier.courierForAuth.CourierForAuth;
import ru.yandex.practicum.service.CourierGenerator;

import static org.hamcrest.CoreMatchers.equalTo;

public class UtilMethods {
    public static final String FIELD_ID = "id";
    private static final String FIELD_OK = "ok";
    private final CourierClient courierClient = new CourierClient();
    private final OrderClient orderClient = new OrderClient();
    private final CourierGenerator generator = new CourierGenerator();

    public Integer getCourierId(Courier courier) {
        CourierForAuth courierForAuth = generator.getCourierForAuth(courier);

        Response response = courierClient.login(courierForAuth);
        return response.body().path(FIELD_ID);
    }

    public void deleteCourier(Courier courier) {
        int courierId = getCourierId(courier);
        Response response = courierClient.delete(courierId);

        response.then().assertThat().body(FIELD_OK, equalTo(true))
                .and().statusCode(HttpStatus.SC_OK);
    }

    public void cancelOrder(Integer id) {
        Response response = orderClient.cancelOrder(id);

        response.then().assertThat().body(FIELD_OK, equalTo(true))
                .and().statusCode(HttpStatus.SC_OK);
    }
}
