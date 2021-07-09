package org.example.api;


import io.restassured.RestAssured;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.runner.RunWith;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.is;

//@RunWith(SerenityRunner.class)
public class RestClassTest {

    public static final String URL = "https://postman-echo.com";
    public static final String queryParams = "/get?foo1=bar1&foo2=bar2";

    @Test(description = "default")
    public void postmanFirstGetTest() {
        RestAssured
                .when().get(URL + queryParams)
                .then().assertThat().statusCode(200)
                .and().body("args.foo2", is("bar2"));
    }
}
