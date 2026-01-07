package sys.bac.integration;

import static io.restassured.RestAssured.given;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import sys.bac.utils.TestUtils;

@QuarkusTest
public class DeviceIT {

    @Inject TestUtils u;

    int cId = -1;
    
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

            u.clearDevices();
            cId = u.createCustomerFixture();
        }
    
    @Test
    @TestTransaction
    public void createDevice201Test() {
        String location =
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
        .when()
        .post("/devices")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("content-length", "0")
        .extract()
        .header("Location");

        int id = Location.extractId(location, "devices");
        assertEquals(location, "http://localhost:8081/devices/" + id);
    }
    
    @Test
    @TestTransaction
    public void post405() {
        given()
        .when().post("devices/1")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
    }
    
    @Test
    @TestTransaction
    public void getDevice200() {
        String location = 
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
        .when().post("/devices")
        .then().statusCode(201)
        .extract().header("Location");
        int id = Location.extractId(location, "devices");
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/devices/" + id)
        .then()
        .statusCode(200)
        .body("id", equalTo(id))
        .body("customerId", equalTo(cId))
        .body("self.href", equalTo("http://localhost:8081/devices/" + id))
        .body("self.rel", equalTo("getDeviceWithId" + id))
        .body("self.type", equalTo("application/json"))
        .body("serialNumber", equalTo("123"))
        .body("type", equalTo("Phone"))
        .body("brand", equalTo("Apple"))
        .body("notes", equalTo("Cracked Screen"))
        .body("model", equalTo("iPhone 17 Pro Max"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, 
            hasItems("<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/devices/" + id + ">;rel=\"updateDevice\";type=\"application/json\"", 
            "<http://localhost:8081/devices/" + id + ">;rel=\"deleteDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void deleteDevice204() {
            String location = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id = Location.extractId(location, "devices");
            
            given().contentType(ContentType.JSON)
            .when()
            .delete("/devices/" + id)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void delete405() {
            given()
            .when().delete("devices")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void updateDevice204() {
            String location = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id = Location.extractId(location, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when()
            .put("/devices/" + id)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/devices/" + id + ">;rel=\"getDevice\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void put405() {
            given()
            .when().put("devices")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void getDevicesEmpty200() {            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices")
            .then()
            .statusCode(200)
            .body(equalTo("[]"))
            .contentType(ContentType.JSON)
            .header("content-length", equalTo("2"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(4));
            assertThat(links, hasItems("<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesQuery200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "devices");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?query=Apple")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("serialNumber", hasItems("123", "231"))
            .body("type", hasItems("Phone", "Phone"))
            .body("brand", hasItems("Apple", "Apple"))
            .body("notes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("model", hasItems("iPhone 17 Pro Max", "iPhone 17 Pro Max"))
            .body("self.href", hasItems("http://localhost:8081/devices/" + id1, "http://localhost:8081/devices/" + id3))
            .body("self.rel", hasItems("getDeviceWithId" + id1, "getDeviceWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesQueryNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"312\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?query=Apple")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("customerId", hasItems(cId, cId))
            .body("serialNumber", hasItems("123", "312"))
            .body("type", hasItems("Phone", "Phone"))
            .body("brand", hasItems("Apple", "Apple"))
            .body("notes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("model", hasItems("iPhone 17 Pro Max", "iPhone 17 Pro Max"))
            .body("self.href", hasItems("http://localhost:8081/devices/" + id1, "http://localhost:8081/devices/" + id2))
            .body("self.rel", hasItems("getDeviceWithId" + id1, "getDeviceWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=2&size=2&query=Apple>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesQueryPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"312\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "devices");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?query=Apple&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id3))
            .body("[0].customerId", equalTo(cId))
            .body("[0].serialNumber", equalTo("231"))
            .body("[0].type", equalTo("Phone"))
            .body("[0].brand", equalTo("Apple"))
            .body("[0].notes", equalTo("Cracked Screen"))
            .body("[0].model", equalTo("iPhone 17 Pro Max"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=0&size=2&query=Apple>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesQueryNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"132\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"312\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "devices");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"321\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?query=Apple&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("serialNumber", hasItems("123", "231"))
            .body("brand", hasItems("Apple", "Apple"))
            .body("type", hasItems("Phone", "Phone"))
            .body("notes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("model", hasItems("iPhone 17 Pro Max", "iPhone 17 Pro Max"))
            .body("self.href", hasItems("http://localhost:8081/devices/" + id1, "http://localhost:8081/devices/" + id3))
            .body("self.rel", hasItems("getDeviceWithId" + id1, "getDeviceWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(7));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=4&size=2&query=Apple>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/devices?offset=0&size=2&query=Apple>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "devices");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("customerId", hasItems(cId, cId))
            .body("serialNumber", hasItems("123", "231"))
            .body("brand", hasItems("Apple", "Apple"))
            .body("type", hasItems("Phone", "Phone"))
            .body("notes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("model", hasItems("iPhone 17 Pro Max", "iPhone 17 Pro Max"))
            .body("self.href", hasItems("http://localhost:8081/devices/" + id1, "http://localhost:8081/devices/" + id2))
            .body("self.rel", hasItems("getDeviceWithId" + id1, "getDeviceWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=2&size=2>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"1234\", \"type\":\"Phone\", \"brand\":\"Samsung\", \"notes\":\"Cracked Screen\", \"model\":\"Galaxy\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "devices");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id2))
            .body("[0].customerId", equalTo(cId))
            .body("[0].serialNumber", equalTo("231"))
            .body("[0].brand", equalTo("Apple"))
            .body("[0].type", equalTo("Phone"))
            .body("[0].notes", equalTo("Cracked Screen"))
            .body("[0].model", equalTo("iPhone 17 Pro Max"))
            .body("[0].self.href", equalTo("http://localhost:8081/devices/" + id2))
            .body("[0].self.rel", equalTo("getDeviceWithId" + id2))
            .body("[0].self.type", equalTo("application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getDevicesNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"132\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"312\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "devices");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"231\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "devices");
            
            given().contentType(ContentType.JSON)
            .body("{\"customerId\":" + cId + ", \"serialNumber\":\"321\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
            .when().post("/devices")
            .then().statusCode(201)
            .extract().header("Location");
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("devices?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("customerId", hasItems(cId, cId))
            .body("serialNumber", hasItems("123", "231"))
            .body("brand", hasItems("Apple", "Apple"))
            .body("type", hasItems("Phone", "Phone"))
            .body("notes", hasItems("Cracked Screen", "Cracked Screen"))
            .body("model", hasItems("iPhone 17 Pro Max", "iPhone 17 Pro Max"))
            .body("self.href", hasItems("http://localhost:8081/devices/" + id1, "http://localhost:8081/devices/" + id3))
            .body("self.rel", hasItems("getDeviceWithId" + id1, "getDeviceWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/devices?offset=4&size=2>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/devices?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
        }
    }
    