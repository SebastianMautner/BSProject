package sys.bac.adapters.in.api.adapter.device;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import sys.bac.adapters.in.api.models.DeviceDTO;
import sys.bac.adapters.in.api.models.DevicesApiResult;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DeviceAPITest {
    
    @InjectMock
    DeviceServiceAdapter dSA;
    
    @Test
    public void createDevice201Test() {
        
        when(dSA.createDevice(any()))
        .thenReturn(new DeviceDTO(1L, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":1, \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
        .when()
        .post("/devices")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("Location", "<http://localhost:8081/devices/1>;rel=\"getDevice\";type=\"application/json\"")
        .header("content-length", "0");
    }
    
    @Test
    public void getDevice200() {
        when(dSA.getDeviceById(anyLong()))
        .thenReturn(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"));
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/devices/" + 1)
        .then()
        .statusCode(200)
        .body("id", equalTo(1))
        .body("customerId", equalTo(1))
        .body("self.href", equalTo("http://localhost:8081/devices/1"))
        .body("self.rel", equalTo("getDeviceWithId1"))
        .body("self.type", equalTo("application/json"))
        .body("serialNumber", equalTo("123"))
        .body("brand", equalTo("Apple"))
        .body("model", equalTo("iPhone 17 Pro Max"))
        .body("type", equalTo("Phone"))
        .body("notes", equalTo("Cracked Screen"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, hasItems("<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
        "<http://localhost:8081/devices/1>;rel=\"updateDevice\";type=\"application/json\"", 
        "<http://localhost:8081/devices/1>;rel=\"deleteDevice\";type=\"application/json\""));
    }
    
    @Test
    public void deleteDevice204() {
        given().contentType(ContentType.JSON)
        .when()
        .delete("/devices/" + 1)
        .then()
        .statusCode(204)
        .header("Cache-control", "no-cache, no-transform, no-store")
        .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
    }
    
    @Test
    public void updateDevice204() {
        given().contentType(ContentType.JSON)
        .body("{\"customerId\":1, \"serialNumber\":\"123\", \"type\":\"Phone\", \"brand\":\"Apple\", \"notes\":\"Cracked Screen\", \"model\":\"iPhone 17 Pro Max\"}")
        .when()
        .put("/devices/" + 1)
        .then()
        .statusCode(204)
        .header("Cache-control", "no-cache, no-transform, no-store")
        .header("Link", "<http://localhost:8081/devices/1>;rel=\"getDevice\";type=\"application/json\"");
    }
    
    @Test
    public void getDevicesEmpty200() {
        when(dSA.getDevices(any(), anyInt(), anyInt())).thenReturn(new DevicesApiResult(new ArrayList<>(), false, false));
        
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
    public void getDevicesQuery200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new DeviceDTO(3, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), false, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?query=Apple")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/1"))
        .body("[0].self.rel", equalTo("getDeviceWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].self.href", equalTo("http://localhost:8081/devices/3"))
        .body("[1].self.rel", equalTo("getDeviceWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].serialNumber", equalTo("123"))
        .body("[1].type", equalTo("Phone"))
        .body("[1].brand", equalTo("Apple"))
        .body("[1].model", equalTo("iPhone 17 Pro Max"))
        .body("[1].notes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(5));
        assertThat(links, hasItems("<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
    }
    
    @Test
    public void getDevicesQueryNext200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new DeviceDTO(3, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), true, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?query=Apple")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/1"))
        .body("[0].self.rel", equalTo("getDeviceWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].self.href", equalTo("http://localhost:8081/devices/3"))
        .body("[1].self.rel", equalTo("getDeviceWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].serialNumber", equalTo("123"))
        .body("[1].type", equalTo("Phone"))
        .body("[1].brand", equalTo("Apple"))
        .body("[1].model", equalTo("iPhone 17 Pro Max"))
        .body("[1].notes", equalTo("Cracked Screen"))
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
    public void getDevicesQueryPrev200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(5, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), false, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?query=Apple&offset=4&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(5))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/5"))
        .body("[0].self.rel", equalTo("getDeviceWithId5"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(6));
        assertThat(links, hasItems("<http://localhost:8081/devices?offset=2&size=2&query=Apple>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"clearQuery\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
    }
    
    @Test
    public void getDevicesQueryNextPrev200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new DeviceDTO(3, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), true, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?query=Apple&offset=2&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/1"))
        .body("[0].self.rel", equalTo("getDeviceWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(3))
        .body("[1].self.href", equalTo("http://localhost:8081/devices/3"))
        .body("[1].self.rel", equalTo("getDeviceWithId3"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].serialNumber", equalTo("123"))
        .body("[1].type", equalTo("Phone"))
        .body("[1].brand", equalTo("Apple"))
        .body("[1].model", equalTo("iPhone 17 Pro Max"))
        .body("[1].notes", equalTo("Cracked Screen"))
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
    public void getDevicesNext200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new DeviceDTO(2, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), true, false));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/1"))
        .body("[0].self.rel", equalTo("getDeviceWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(2))
        .body("[1].self.href", equalTo("http://localhost:8081/devices/2"))
        .body("[1].self.rel", equalTo("getDeviceWithId2"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].serialNumber", equalTo("123"))
        .body("[1].type", equalTo("Phone"))
        .body("[1].brand", equalTo("Apple"))
        .body("[1].model", equalTo("iPhone 17 Pro Max"))
        .body("[1].notes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
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
    public void getDevicesPrev200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(5, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), false, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?offset=4&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(5))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/5"))
        .body("[0].self.rel", equalTo("getDeviceWithId5"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .header("Content-Type", "application/json;charset=UTF-8")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(5));
        assertThat(links, hasItems("<http://localhost:8081/devices?offset=2&size=2>;rel=\"prev\";type=\"application/json\"",
        "<http://localhost:8081/devices?query={query}>;rel=\"getNewDeviceQuery\";type=\"application/json\"",
        "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
        "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
        "<http://localhost:8081/devices>;rel=\"createDevice\";type=\"application/json\""));
    }
    
    @Test
    public void getDevicesNextPrev200() {
        when(dSA.getDevices(any(), anyInt(), anyInt()))
        .thenReturn(new DevicesApiResult(Arrays.asList(new DeviceDTO(1, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen"),
        new DeviceDTO(2, 1L, "123", "Phone", "Apple", "iPhone 17 Pro Max", "Cracked Screen")), true, true));
        
        List<String> links = given().contentType(ContentType.JSON)
        .when().get("devices?offset=2&size=2")
        .then()
        .statusCode(200)
        .body("[0].id", equalTo(1))
        .body("[0].self.href", equalTo("http://localhost:8081/devices/1"))
        .body("[0].self.rel", equalTo("getDeviceWithId1"))
        .body("[0].self.type", equalTo("application/json"))
        .body("[0].serialNumber", equalTo("123"))
        .body("[0].type", equalTo("Phone"))
        .body("[0].brand", equalTo("Apple"))
        .body("[0].model", equalTo("iPhone 17 Pro Max"))
        .body("[0].notes", equalTo("Cracked Screen"))
        .body("[1].id", equalTo(2))
        .body("[1].self.href", equalTo("http://localhost:8081/devices/2"))
        .body("[1].self.rel", equalTo("getDeviceWithId2"))
        .body("[1].self.type", equalTo("application/json"))
        .body("[1].serialNumber", equalTo("123"))
        .body("[1].type", equalTo("Phone"))
        .body("[1].brand", equalTo("Apple"))
        .body("[1].model", equalTo("iPhone 17 Pro Max"))
        .body("[1].notes", equalTo("Cracked Screen"))
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
    
    @Test
    public void delete405() {
        given()
        .when().delete("devices")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
    }
    
    @Test
    public void put405() {
        given()
        .when().put("devices")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
    }
    
    @Test
    public void post405() {
        given()
        .when().post("devices/1")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"");
    }
}
