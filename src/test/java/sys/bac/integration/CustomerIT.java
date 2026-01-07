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
public class CustomerIT {

    @Inject TestUtils u;
    
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
        void resetDB() {
            u.clearCustomers();
        }
    
    @Test
    @TestTransaction
    public void createCustomer201Test() {
        String location =
        given().contentType(ContentType.JSON)
        .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
        .post("/customers")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("content-length", "0")
        .extract()
        .header("Location");

        int id = Location.extractId(location, "customers");
        assertEquals(location, "http://localhost:8081/customers/" + id);
    }
    
    @Test
    @TestTransaction
    public void post405() {
        given()
        .when().post("customers/1")
        .then()
        .statusCode(405)
        .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
    }
    
    @Test
    @TestTransaction
    public void getCustomer200() {
        String location = 
        given().contentType(ContentType.JSON)
        .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when().post("/customers")
        .then().statusCode(201)
        .extract().header("Location");
        int id = Location.extractId(location, "customers");
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/customers/" + id)
        .then()
        .statusCode(200)
        .body("id", equalTo(id))
        .body("self.href", equalTo("http://localhost:8081/customers/" + id))
        .body("self.rel", equalTo("getCustomerWithId" + id))
        .body("self.type", equalTo("application/json"))
        .body("surname", equalTo("Bond"))
        .body("name", equalTo("James"))
        .body("email", equalTo("test@test.co.uk"))
        .body("phone", equalTo("+44 12312345678"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, 
            hasItems("<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/customers/" + id + ">;rel=\"updateCustomer\";type=\"application/json\"", 
            "<http://localhost:8081/customers/" + id + ">;rel=\"deleteCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void deleteCustomer204() {
            String location = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id = Location.extractId(location, "customers");
            
            given().contentType(ContentType.JSON)
            .when()
            .delete("/customers/" + id)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void delete405() {
            given()
            .when().delete("customers")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void updateCustomer204() {
            String location = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id = Location.extractId(location, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when()
            .put("/customers/" + id)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/customers/" + id + ">;rel=\"getCustomer\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void put405() {
            given()
            .when().put("customers")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }
        
        @Test
        @TestTransaction
        public void getCustomersEmpty200() {            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers")
            .then()
            .statusCode(200)
            .body(equalTo("[]"))
            .contentType(ContentType.JSON)
            .header("content-length", equalTo("2"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(4));
            assertThat(links, hasItems("<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersQuery200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "customers");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("surname", hasItems("Bond", "Moneypenny"))
            .body("name", hasItems("James", "James"))
            .body("email", hasItems("test@test.co.uk", "test@test.co.uk"))
            .body("phone", hasItems("+44 12312345678", "+44 12312345678"))
            .body("self.href", hasItems("http://localhost:8081/customers/" + id1, "http://localhost:8081/customers/" + id3))
            .body("self.rel", hasItems("getCustomerWithId" + id1, "getCustomerWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersQueryNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blofield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("surname", hasItems("Bond", "Blofield"))
            .body("name", hasItems("James", "James"))
            .body("email", hasItems("test@test.co.uk", "test@test.co.uk"))
            .body("phone", hasItems("+44 12312345678", "+44 12312345678"))
            .body("self.href", hasItems("http://localhost:8081/customers/" + id1, "http://localhost:8081/customers/" + id2))
            .body("self.rel", hasItems("getCustomerWithId" + id1, "getCustomerWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=2&size=2&query=James>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersQueryPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blofield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "customers");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id3))
            .body("[0].surname", equalTo("Moneypenny"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.co.uk"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=0&size=2&query=James>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersQueryNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneyfield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blofield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "customers");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blopenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("surname", hasItems("Bond", "Moneypenny"))
            .body("name", hasItems("James", "James"))
            .body("email", hasItems("test@test.co.uk", "test@test.co.uk"))
            .body("phone", hasItems("+44 12312345678", "+44 12312345678"))
            .body("self.href", hasItems("http://localhost:8081/customers/" + id1, "http://localhost:8081/customers/" + id3))
            .body("self.rel", hasItems("getCustomerWithId" + id1, "getCustomerWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(7));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=4&size=2&query=James>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/customers?offset=0&size=2&query=James>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersNext200() {
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "customers");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id2))
            .body("surname", hasItems("Bond", "Moneypenny"))
            .body("name", hasItems("James", "James"))
            .body("email", hasItems("test@test.co.uk", "test@test.co.uk"))
            .body("phone", hasItems("+44 12312345678", "+44 12312345678"))
            .body("self.href", hasItems("http://localhost:8081/customers/" + id1, "http://localhost:8081/customers/" + id2))
            .body("self.rel", hasItems("getCustomerWithId" + id1, "getCustomerWithId" + id2))
            .body("self.type", hasItems("application/json", "application/json"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=2&size=2>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"Eve\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location2 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id2 = Location.extractId(location2, "customers");
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(id2))
            .body("[0].surname", equalTo("Moneypenny"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.co.uk"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[0].self.href", equalTo("http://localhost:8081/customers/" + id2))
            .body("[0].self.rel", equalTo("getCustomerWithId" + id2))
            .body("[0].self.type", equalTo("application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
        
        @Test
        @TestTransaction
        public void getCustomersNextPrev200() {
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneyfield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blofield\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            
            String location1 = 
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id1 = Location.extractId(location1, "customers");
            
            String location3 =
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            int id3 = Location.extractId(location3, "customers");
            
            given().contentType(ContentType.JSON)
            .body("{\"surname\":\"Blopenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
            .when().post("/customers")
            .then().statusCode(201)
            .extract().header("Location");
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("id", hasItems(id1, id3))
            .body("surname", hasItems("Bond", "Moneypenny"))
            .body("name", hasItems("James", "James"))
            .body("email", hasItems("test@test.co.uk", "test@test.co.uk"))
            .body("phone", hasItems("+44 12312345678", "+44 12312345678"))
            .body("self.href", hasItems("http://localhost:8081/customers/" + id1, "http://localhost:8081/customers/" + id3))
            .body("self.rel", hasItems("getCustomerWithId" + id1, "getCustomerWithId" + id3))
            .body("self.type", hasItems("application/json", "application/json"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=4&size=2>;rel=\"next\";type=\"application/json\"",
            "<http://localhost:8081/customers?offset=0&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }
    }
    