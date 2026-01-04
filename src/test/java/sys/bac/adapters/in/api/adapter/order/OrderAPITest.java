package sys.bac.adapters.in.api.adapter.order;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import sys.bac.adapters.in.api.models.OrderDTO;
import sys.bac.adapters.in.api.models.OrdersApiResult;
import sys.bac.application.domain.models.order.OrderStatus;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@QuarkusTest
public class OrderAPITest {
    
    @InjectMock
    OrderServiceAdapter oSA;
    
    @Test
    public void createOrder201Test() {
        
        when(oSA.createOrder(any()))
        .thenReturn(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED));
        
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":1, \"deviceId\":\"1\", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
        .when()
        .post("/orders")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("Location", "<http://localhost:8081/orders/1>;rel=\"getOrder\";type=\"application/json\"")
        .header("content-length", "0");
    }
    
    @Test
    public void getOrder200() {
        when(oSA.getOrderById(anyLong()))
        .thenReturn(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED));
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/orders/" + 1)
        .then()
        .statusCode(200)
        .body("id", equalTo(1))
        .body("customerId", equalTo(1))
        .body("self.href", equalTo("http://localhost:8081/orders/1"))
        .body("self.rel", equalTo("getOrderWithId1"))
        .body("self.type", equalTo("application/json"))
        .body("deviceId", equalTo(1))
        .body("status", equalTo("RECEIVED"))
        .body("costEstimation", equalTo(100.0F))
        .body("receivedAt", equalTo("2020-12-30"))
        .body("issueNotes", equalTo("Cracked Screen"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, hasItems("<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
        "<http://localhost:8081/orders/1>;rel=\"updateOrder\";type=\"application/json\"", 
        "<http://localhost:8081/orders/1>;rel=\"deleteOrder\";type=\"application/json\""));
    }
    
    @Test
    public void deleteOrder204() {
        given().contentType(ContentType.JSON)
        .when()
        .delete("/orders/" + 1)
        .then()
        .statusCode(204)
        .header("Cache-control", "no-cache, no-transform, no-store")
        .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
    }
    
    @Test
    public void updateOrder204() {
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":1, \"deviceId\":\"1\", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
        .when()
        .put("/orders/" + 1)
        .then()
        .statusCode(204)
        .header("Cache-control", "no-cache, no-transform, no-store")
        .header("Link", "<http://localhost:8081/orders/1>;rel=\"getOrder\";type=\"application/json\"");
    }
    
    @Test
    public void getOrdersEmpty200() {
        when(oSA.getOrders("", 0, 2)).thenReturn(new OrdersApiResult(new ArrayList<>(), false, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders")
        .then()
        .statusCode(200)
        .body(equalTo("[]"))
        .contentType(ContentType.JSON)
        .header("content-length", equalTo("2"))
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(4));
        assertThat(links, hasItems("<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersQuery200() {
        when(oSA.getOrders("2020-12-30", 0, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED),
        new OrderDTO(3, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), false, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?query=2020-12-30")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/1"))
        .body("[0].self.rel", equalTo("getOrderWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].customerId", equalTo(1))
        .body("[1].self.href", equalTo("http://localhost:8081/orders/3"))
        .body("[1].self.rel", equalTo("getOrderWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].deviceId", equalTo(1))
        .body("[1].status", equalTo("RECEIVED"))
        .body("[1].costEstimation", equalTo(100.0F))
        .body("[1].receivedAt", equalTo("2020-12-30"))
        .body("[1].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(5));
        assertThat(links, hasItems("<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersQueryNext200() {
        when(oSA.getOrders(any(), anyInt(), anyInt()))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED),
        new OrderDTO(3, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), true, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?query=Apple")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/1"))
        .body("[0].self.rel", equalTo("getOrderWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].customerId", equalTo(1))
        .body("[1].self.href", equalTo("http://localhost:8081/orders/3"))
        .body("[1].self.rel", equalTo("getOrderWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].deviceId", equalTo(1))
        .body("[1].status", equalTo("RECEIVED"))
        .body("[1].costEstimation", equalTo(100.0F))
        .body("[1].receivedAt", equalTo("2020-12-30"))
        .body("[1].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(6));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=2&size=2&query=Apple>;rel=\"next\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersQueryPrev200() {
        when(oSA.getOrders("Apple", 4, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(5, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), false, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?query=Apple&offset=4&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(5))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/5"))
        .body("[0].self.rel", equalTo("getOrderWithId5"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(6));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=2&size=2&query=Apple>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersQueryNextPrev200() {
        when(oSA.getOrders("Apple", 2, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED),
        new OrderDTO(3, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), true, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?query=Apple&offset=2&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/1"))
        .body("[0].self.rel", equalTo("getOrderWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].customerId", equalTo(1))
        .body("[1].self.href", equalTo("http://localhost:8081/orders/3"))
        .body("[1].self.rel", equalTo("getOrderWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].deviceId", equalTo(1))
        .body("[1].status", equalTo("RECEIVED"))
        .body("[1].costEstimation", equalTo(100.0F))
        .body("[1].receivedAt", equalTo("2020-12-30"))
        .body("[1].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(7));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=4&size=2&query=Apple>;rel=\"next\";type=\"application/json\"",
        "<http://localhost:8081/orders?offset=0&size=2&query=Apple>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersNext200() {
        when(oSA.getOrders("", 0, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED),
        new OrderDTO(3, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), true, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/1"))
        .body("[0].self.rel", equalTo("getOrderWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].customerId", equalTo(1))
        .body("[1].self.href", equalTo("http://localhost:8081/orders/3"))
        .body("[1].self.rel", equalTo("getOrderWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].deviceId", equalTo(1))
        .body("[1].status", equalTo("RECEIVED"))
        .body("[1].costEstimation", equalTo(100.0F))
        .body("[1].receivedAt", equalTo("2020-12-30"))
        .body("[1].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(5));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=2&size=2>;rel=\"next\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersPrev200() {
        when(oSA.getOrders("", 4, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(5, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), false, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?offset=4&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(5))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/5"))
        .body("[0].self.rel", equalTo("getOrderWithId5"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(5));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=2&size=2>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void getOrdersNextPrev200() {
        when(oSA.getOrders("", 2, 2))
        .thenReturn(new OrdersApiResult(Arrays.asList(new OrderDTO(1, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED),
        new OrderDTO(2, 1, 1, "Cracked Screen", LocalDate.parse("2020-12-30"), 100, OrderStatus.RECEIVED)), true, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("orders?offset=2&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].customerId", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/orders/1"))
        .body("[0].self.rel", equalTo("getOrderWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].deviceId", equalTo(1))
        .body("[0].status", equalTo("RECEIVED"))
        .body("[0].costEstimation", equalTo(100.0F))
        .body("[0].receivedAt", equalTo("2020-12-30"))
        .body("[0].issueNotes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(2))
        .body("[1].customerId", equalTo(1))
        .body("[1].self.href", equalTo("http://localhost:8081/orders/2"))
        .body("[1].self.rel", equalTo("getOrderWithId2"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].deviceId", equalTo(1))
        .body("[1].status", equalTo("RECEIVED"))
        .body("[1].costEstimation", equalTo(100.0F))
        .body("[1].receivedAt", equalTo("2020-12-30"))
        .body("[1].issueNotes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(6));
        assertThat(links, hasItems("<http://localhost:8081/orders?offset=4&size=2>;rel=\"next\";type=\"application/json\"",
        "<http://localhost:8081/orders?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
    }
    
    @Test
    public void delete405() {
        given()
        .when().delete("orders")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
    }
    
    @Test
    public void put405() {
        given()
        .when().put("orders")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
    }
    
    @Test
    public void post405() {
        given()
        .when().post("orders/1")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
    }
}