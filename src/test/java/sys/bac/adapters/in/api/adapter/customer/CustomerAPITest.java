package sys.bac.adapters.in.api.adapter.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import sys.bac.adapters.in.api.models.CustomerDTO;
import sys.bac.adapters.in.api.models.CustomersApiResult;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerAPITest {
    
    @InjectMock
    CustomerServiceAdapter cSA;
    
    @Test
    public void createCustomer201Test() {
        
        when(cSA.createCustomer(any()))
        .thenReturn(new CustomerDTO(1, "Bond", "James", "test@test.co.uk", "+44 123 12345678"));
        
        given().contentType(ContentType.JSON)
        .body("{\"surname\":\"Bond\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
        .post("/customers")
        .then()
        .statusCode(201)
        .header("Cache-Control", "no-cache, no-transform, no-store")
        .header("Location", "http://localhost:8081/customers/1")
        .header("content-length", "0");
    }
    
    @Test
    public void getCustomer200() {
        when(cSA.getCustomerById(anyLong()))
        .thenReturn(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"));
        
        List<String> links = 
        given().contentType(ContentType.JSON)
        .when()
        .get("/customers/" + 1)
        .then()
        .statusCode(200)
        .body("id", equalTo(1))
        .body("self.href", equalTo("http://localhost:8081/customers/1"))
        .body("self.rel", equalTo("getCustomerWithId1"))
        .body("self.type", equalTo("application/json"))
        .body("surname", equalTo("Bond"))
        .body("name", equalTo("James"))
        .body("email", equalTo("test@test.de"))
        .body("phone", equalTo("+44 123 12345678"))
        .header("content-type", containsString("application/json"))
        .header("Cache-Control", "must-revalidate, no-transform, max-age=30, private")
        .extract()
        .headers()
        .getValues("Link");
        assertThat(links.size(), is(3));
        assertThat(links, 
            hasItems("<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"",
            "<http://localhost:8081/customers/1>;rel=\"updateCustomer\";type=\"application/json\"", 
            "<http://localhost:8081/customers/1>;rel=\"deleteCustomer\";type=\"application/json\""));
        }
        
        @Test
        public void deleteCustomer204() {
            given().contentType(ContentType.JSON)
            .when()
            .delete("/customers/" + 1)
            .then()
            .statusCode(204)
            .header("Cache-control", "no-cache, no-transform, no-store")
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }
        
            @Test
    public void updateCustomer204() {

        when(cSA.getCustomerById(eq(1L)))
        .thenReturn(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"));

        String etag =
        given().contentType(ContentType.JSON)
        .when()
        .get("/customers/" + 1)
        .then()
        .statusCode(200)
        .header("ETag", notNullValue())
        .extract()
        .header("ETag");

        given().contentType(ContentType.JSON)
        .header("If-Match", etag)
        .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
        .put("/customers/" + 1)
        .then()
        .statusCode(204)
        .header("Cache-control", "no-cache, no-transform, no-store")
        .header("Link", "<http://localhost:8081/customers/1>;rel=\"getCustomer\";type=\"application/json\"");
    }

    @Test
    public void conditionalGet304_whenIfNoneMatchMatches() {
        when(cSA.getCustomerById(eq(1L)))
        .thenReturn(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"));

        String etag =
        given()
        .when()
        .get("/customers/" + 1)
        .then()
        .statusCode(200)
        .header("ETag", notNullValue())
        .extract()
        .header("ETag");

        given()
        .header("If-None-Match", etag)
        .when()
        .get("/customers/" + 1)
        .then()
        .statusCode(304);
    }

    @Test
    public void conditionalPut428_whenIfMatchMissing() {
        given().contentType(ContentType.JSON)
        .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
        .put("/customers/" + 1)
        .then()
        .statusCode(428);
    }

    @Test
    public void conditionalPut412_whenIfMatchWrong() {

        when(cSA.getCustomerById(eq(1L)))
        .thenReturn(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 123 12345678"));

        given().contentType(ContentType.JSON)
        .header("If-Match", "W/\"invalid-etag\"")
        .body("{\"surname\":\"Moneypenny\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
        .put("/customers/" + 1)
        .then()
        .statusCode(412);
    }
        
        @Test
        public void getCustomersEmpty200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(new ArrayList<>(), false, false));
            
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
        public void getCustomersQuery200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 12312345678"),
            new CustomerDTO(3, "Moneypenny", "James", "test@test.de", "+44 12312345678")), false, false));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(1))
            .body("[0].surname", equalTo("Bond"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[1].id", equalTo(3))
            .body("[1].surname", equalTo("Moneypenny"))
            .body("[1].name", equalTo("James"))
            .body("[1].email", equalTo("test@test.de"))
            .body("[1].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("397"))
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
        public void getCustomersQueryNext200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 12312345678"),
            new CustomerDTO(3, "Moneypenny", "James", "test@test.de", "+44 12312345678")), true, false));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(1))
            .body("[0].surname", equalTo("Bond"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[1].id", equalTo(3))
            .body("[1].surname", equalTo("Moneypenny"))
            .body("[1].name", equalTo("James"))
            .body("[1].email", equalTo("test@test.de"))
            .body("[1].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("397"))
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
        public void getCustomersQueryPrev200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(5, "Moneypenny", "James", "test@test.de", "+44 12312345678")), false, true));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James&offset=4&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(5))
            .body("[0].surname", equalTo("Moneypenny"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("202"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(6));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=2&size=2&query=James>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"clearQuery\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }

        @Test
        public void getCustomersQueryNextPrev200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(3, "Bond", "James", "test@test.de", "+44 12312345678"),
                new CustomerDTO(4, "Moneypenny", "James", "test@test.de", "+44 12312345678")), true, true));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?query=James&offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(3))
            .body("[0].surname", equalTo("Bond"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[1].id", equalTo(4))
            .body("[1].surname", equalTo("Moneypenny"))
            .body("[1].name", equalTo("James"))
            .body("[1].email", equalTo("test@test.de"))
            .body("[1].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("397"))
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
        public void getCustomersNext200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(1, "Bond", "James", "test@test.de", "+44 12312345678"),
            new CustomerDTO(3, "Moneypenny", "James", "test@test.de", "+44 12312345678")), true, false));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(1))
            .body("[0].surname", equalTo("Bond"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[1].id", equalTo(3))
            .body("[1].surname", equalTo("Moneypenny"))
            .body("[1].name", equalTo("James"))
            .body("[1].email", equalTo("test@test.de"))
            .body("[1].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("397"))
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
        public void getCustomersPrev200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(5, "Moneypenny", "James", "test@test.de", "+44 12312345678")), false, true));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?offset=4&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(5))
            .body("[0].surname", equalTo("Moneypenny"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("202"))
            .extract()
            .headers()
            .getValues("Link");
            assertThat(links.size(), is(5));
            assertThat(links, hasItems("<http://localhost:8081/customers?offset=2&size=2>;rel=\"prev\";type=\"application/json\"",
            "<http://localhost:8081/customers?query={query}>;rel=\"getNewCustomerQuery\";type=\"application/json\"",
            "<http://localhost:8081/orders>;rel=\"getAllOrders\";type=\"application/json\"",
            "<http://localhost:8081/devices>;rel=\"getAllDevices\";type=\"application/json\"",
            "<http://localhost:8081/customers>;rel=\"createCustomer\";type=\"application/json\""));
        }

        @Test
        public void getCustomersNextPrev200() {
            when(cSA.getCustomers(any(), anyInt(), anyInt()))
            .thenReturn(new CustomersApiResult(Arrays.asList(new CustomerDTO(3, "Bond", "James", "test@test.de", "+44 12312345678"),
                new CustomerDTO(4, "Moneypenny", "James", "test@test.de", "+44 12312345678")), true, true));
            
            List<String> links = given().contentType(ContentType.JSON)
            .when().get("customers?offset=2&size=2")
            .then()
            .statusCode(200)
            .body("[0].id", equalTo(3))
            .body("[0].surname", equalTo("Bond"))
            .body("[0].name", equalTo("James"))
            .body("[0].email", equalTo("test@test.de"))
            .body("[0].phone", equalTo("+44 12312345678"))
            .body("[1].id", equalTo(4))
            .body("[1].surname", equalTo("Moneypenny"))
            .body("[1].name", equalTo("James"))
            .body("[1].email", equalTo("test@test.de"))
            .body("[1].phone", equalTo("+44 12312345678"))
            .header("Content-Type", "application/json;charset=UTF-8")
            .header("content-length", equalTo("397"))
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

        @Test
        public void delete405() {
            given()
            .when().delete("customers")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }

        @Test
        public void put405() {
            given()
            .when().put("customers")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }

        @Test
        public void post405() {
            given()
            .when().post("customers/1")
            .then()
            .statusCode(405)
            .header("Link", "<http://localhost:8081/customers>;rel=\"getAllCustomers\";type=\"application/json\"");
        }
    }