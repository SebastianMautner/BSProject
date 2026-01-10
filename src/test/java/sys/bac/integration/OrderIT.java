package sys.bac.integration;

import static io.restassured.RestAssured.given;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import sys.bac.utils.TestUtils;

@QuarkusTest
public class OrderIT {

    @Inject TestUtils u;

    int cId = -1;

    int dId = -1;
    
    public final class Location {
        
        private Location() {}
        
        public static int extractId(String location, String resourcePath) {
            String uriPart = location.trim();
            
            URI uri = URI.create(uriPart);
            String path = uri.getPath();
            
            return Integer.valueOf(path.replaceFirst(".*/" + resourcePath + "/", ""));
        }
    }

        @BeforeEach
        void setUp() {

            u.clearOrders();
            cId = u.createCustomerFixture();
            dId = u.createDeviceFixture();
        }
    
    @Test
    @TestTransaction
    public void createOrder201Test() {
        String location =
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":" + cId + ", \"deviceId\":\"" + dId + "\", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
        .when()
        .post("/orders")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("content-length", "0")
        .extract()
        .header("Location");

        int id = Location.extractId(location, "orders");
        assertEquals(location, "http://localhost:8081/orders/" + id);
    }
    
    @Test
    @TestTransaction
    public void post405() {
        given()
        .when().post("orders/1")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
    }
    
    @Test
    @TestTransaction
    public void getOrder200() {
        String location = 
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
        .when().post("/orders")
        .then().statusCode(201)
        .extract().header("Location");
        int id = Location.extractId(location, "orders");
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/orders/" + id)
        .then()
        .statusCode(200)
        .body("id", equalTo(id))
        .body("customerId", equalTo(cId))
        .body("deviceId", equalTo(dId))
        .body("self.href", equalTo("http://localhost:8081/orders/" + id))
        .body("self.rel", equalTo("getOrderWithId" + id))
        .body("self.type", equalTo("application/json"))
        .body("receivedAt", equalTo("2020-12-30"))
        .body("completion", nullValue())
        .body("costEstimation", equalTo(100F))
        .body("finalCost", equalTo(0F))
        .body("issueNotes", equalTo("Cracked Screen"))
        .body("status", equalTo("RECEIVED"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, 
            hasItems("<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/orders/" + id + ">;rel=\"updateOrder\";type=\"application/json\"", 
            "<http://localhost:8081/orders/" + id + ">;rel=\"deleteOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void deleteOrder204() {
            String location = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id = Location.extractId(location, "orders");
            
            given().contentType(ContentType.JSON)
            .when()
            .delete("/orders/" + id)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void delete405() {
            given()
            .when().delete("orders")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void updateOrder204() {

            String location =
                given().contentType(ContentType.JSON)
                .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
                .when().post("/orders")
                .then().statusCode(201)
                .extract().header("Location");

            int id = Location.extractId(location, "orders");

            String etag =
                given()
                .when()
                    .get("/orders/" + id)
                .then()
                    .statusCode(200)
                    .header("ETag", notNullValue())
                    .extract()
                    .header("ETag");

            given().contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when()
                .put("/orders/" + id)
            .then()
                .statusCode(204)
                .header("Cache-control", "no-cache, no-transform, no-store")
                .header("Link", "<http://localhost:8081/orders/" + id + ">;rel=\"getOrder\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void put405() {
            given()
            .when().put("orders")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void getOrdersEmpty200() {            
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
        @TestTransaction
        public void getOrdersQuery200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "orders");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?query=Cracked Screen")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("deviceId", hasItems(dId, dId))
            .body("receivedAt", hasItems("2020-12-30", "2020-11-29"))
            .body("completion", everyItem(nullValue()))
            .body("issueNotes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("costEstimation", hasItems(100F, 100F))
            .body("finalCost", hasItems(0F, 0F))
            .body("status", hasItems("RECEIVED", "RECEIVED"))
            .body("self.href", hasItems("http://localhost:8081/orders/" + id1, "http://localhost:8081/orders/" + id3))
            .body("self.rel", hasItems("getOrderWithId" + id1, "getOrderWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getOrdersQueryNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?query=Cracked Screen")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("customerId", hasItems(cId, cId))
            .body("deviceId", hasItems(dId, dId))
            .body("receivedAt", hasItems("2020-12-30", "2020-11-29"))
            .body("completion", everyItem(nullValue()))
            .body("issueNotes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("costEstimation", hasItems(100F, 100F))
            .body("finalCost", hasItems(0F, 0F))
            .body("status", hasItems("RECEIVED", "RECEIVED"))
            .body("self.href", hasItems("http://localhost:8081/orders/" + id1, "http://localhost:8081/orders/" + id2))
            .body("self.rel", hasItems("getOrderWithId" + id1, "getOrderWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/orders?offset=2&size=2&query=Cracked Screen>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getOrdersQueryPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "orders");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?query=Cracked Screen&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id3))
            .body("[0].customerId", equalTo(cId))
            .body("[0].deviceId", equalTo(dId))
            .body("[0].receivedAt", equalTo("2020-11-29"))
            .body("[0].completion", nullValue())
            .body("[0].issueNotes", equalTo("Cracked Screen"))
            .body("[0].costEstimation", equalTo(100F))
            .body("[0].finalCost", equalTo(0F))
            .body("[0].status", equalTo("RECEIVED"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/orders?offset=0&size=2&query=Cracked Screen>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getOrdersQueryNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"COMPLETED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "orders");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2019-11-29\", \"costEstimation\":255, \"status\":\"COMPLETED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?query=Cracked Screen&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("deviceId", hasItems(dId, dId))
            .body("receivedAt", hasItems("2020-12-30", "2020-11-29"))
            .body("issueNotes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("completion", everyItem(nullValue()))
            .body("costEstimation", hasItems(100F, 100F))
            .body("finalCost", hasItems(0F, 0F))
            .body("status", hasItems("RECEIVED", "RECEIVED"))
            .body("self.href", hasItems("http://localhost:8081/orders/" + id1, "http://localhost:8081/orders/" + id3))
            .body("self.rel", hasItems("getOrderWithId" + id1, "getOrderWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(7));
            assertThat(links, hasItems("<http://localhost:8081/orders?offset=4&size=2&query=Cracked Screen>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/orders?offset=0&size=2&query=Cracked Screen>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getOrdersNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "orders");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("customerId", hasItems(cId, cId))
            .body("deviceId", hasItems(dId, dId))
            .body("receivedAt", hasItems("2020-12-30", "2020-11-29"))
            .body("issueNotes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("completion", everyItem(nullValue()))
            .body("costEstimation", hasItems(100F, 100F))
            .body("finalCost", hasItems(0F, 0F))
            .body("status", hasItems("RECEIVED", "RECEIVED"))
            .body("self.href", hasItems("http://localhost:8081/orders/" + id1, "http://localhost:8081/orders/" + id2))
            .body("self.rel", hasItems("getOrderWithId" + id1, "getOrderWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
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
        @TestTransaction
        public void getOrdersPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Broke in two\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "orders");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id2))
            .body("[0].customerId", equalTo(cId))
            .body("[0].deviceId", equalTo(dId))
            .body("[0].receivedAt", equalTo("2020-11-29"))
            .body("[0].issueNotes", equalTo("Cracked Screen"))
            .body("[0].completion", nullValue())
            .body("[0].costEstimation", equalTo(100F))
            .body("[0].finalCost", equalTo(0F))
            .body("[0].status", equalTo("RECEIVED"))
            .body("[0].self.href", equalTo("http://localhost:8081/orders/" + id2))
            .body("[0].self.rel", equalTo("getOrderWithId" + id2))
            .body("[0].self.type", equalTo("application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/orders?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/orders?query={query}>;rel=\"getNewOrderQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"createOrder\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getOrdersNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"COMPLETED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":255, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-12-30\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "orders");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2020-11-29\", \"costEstimation\":100, \"status\":\"RECEIVED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "orders");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"deviceId\":" + dId + ", \"issueNotes\":\"Cracked Screen\", \"receivedAt\":\"2019-11-29\", \"costEstimation\":255, \"status\":\"COMPLETED\"}")
            .when().post("/orders")
            .then().statusCode(201)
            .extract().header("Location");

            List<String> links = given().contentType(ContentType.JSON)
            .when().get("orders?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("deviceId", hasItems(dId, dId))
            .body("receivedAt", hasItems("2020-12-30", "2020-11-29"))
            .body("issueNotes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("completion", everyItem(nullValue()))
            .body("costEstimation", hasItems(100F, 100F))
            .body("finalCost", hasItems(0F, 0F))
            .body("status", hasItems("RECEIVED", "RECEIVED"))
            .body("self.href", hasItems("http://localhost:8081/orders/" + id1, "http://localhost:8081/orders/" + id3))
            .body("self.rel", hasItems("getOrderWithId" + id1, "getOrderWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
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
    }
    