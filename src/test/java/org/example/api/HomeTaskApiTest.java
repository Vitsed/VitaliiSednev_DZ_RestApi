package org.example.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import org.example.model.Order;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class HomeTaskApiTest {


    @BeforeClass
    public void prepare() throws IOException {


        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());
    }


    @Test
    public void checkOrderSave() {
        Order order = new Order();
        int id = Integer.parseInt(System.getProperty("orderId"));
        int petId = Integer.parseInt(System.getProperty("petId"));
        order.setId(id);
        order.setPetId(petId);
        order.setQuantity(1);
        order.setShipDate(String.format("%1tFT%1$TH:%1$TM:%1$TSZ", new Date()));
        order.setStatus("placed");
        order.setComplete(true);


        given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);

        Order actual =
                given()
                        .pathParam("orderId", id)
                        .when()
                        .get("/store/order/{orderId}")
                        .then()
                        .statusCode(200)
                        .extract().body()
                        .as(Order.class);

        Assert.assertEquals(actual.getId(), order.getId());

    }

    @Test
    public void testDelete() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .pathParam("orderId", System.getProperty("orderId"))
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);
        given()
                .pathParam("orderId", System.getProperty("orderId"))
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void testGetStoreInventory() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        Map inventory = given()
                .baseUri("https://petstore.swagger.io/v2/")
                .header(new Header("api_key", System.getProperty("api.key")))
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .log().all().extract().body().as(Map.class);


        Assert.assertTrue(inventory.containsKey("sold"), "Key is absent");

    }

    @Ignore
    @Test
    public void testGetByOrderId() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .baseUri("https://petstore.swagger.io/v2/")
                .header(new Header("api_key", System.getProperty("api.key")))
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .pathParam("orderId", System.getProperty("orderId"))
                .get("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .log().all();

    }

    @Ignore
    @Test
    public void testFindByStatus() throws IOException {

        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));
        given()
                .baseUri("https://petstore.swagger.io/v2/")
                .header(new Header("api_key", System.getProperty("api.key")))
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get("/pet/findByStatus?status=available")
                .then()
                .statusCode(200)
                .log().all();

    }
}
