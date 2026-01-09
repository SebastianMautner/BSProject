package sys.bac.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
public class DispatcherIT {
    
    @Test
    public void getTest() {
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("")
        .then()
        .statusCode(200)
        .header("content-length", equalTo("0"))
        .extract()
        .headers()
        .getValues("Link");
        
        assertEquals(3, links.size());
        assertThat(links, hasItems("<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\""));
    }

    @Test
    public void delete405Test() {
        given().contentType(ContentType.JSON)
        .when().delete("")
        .then()
        .statusCode(405)
        .header("content-length", equalTo("0"))
        .header("Link", "<http://localhost:8081/>;rel=\"getDispatcherService\";type=\"application/json\"");
    }

    @Test
    public void put405Test() {
        given().contentType(ContentType.JSON)
        .when().put("")
        .then()
        .statusCode(405)
        .header("content-length", equalTo("0"))
        .header("Link", "<http://localhost:8081/>;rel=\"getDispatcherService\";type=\"application/json\"");
    }

    @Test
    public void post405Test() {
        given().contentType(ContentType.JSON)
        .when().post("")
        .then()
        .statusCode(405)
        .header("content-length", equalTo("0"))
        .header("Link", "<http://localhost:8081/>;rel=\"getDispatcherService\";type=\"application/json\"");
    }

    @Test
    public void getById405Test() {
        given().contentType(ContentType.JSON)
        .when().get("/1")
        .then().statusCode(405)
        .header("content-length", equalTo("0"))
        .header("Link", "<http://localhost:8081/>;rel=\"getDispatcherService\";type=\"application/json\"");
    }
}
