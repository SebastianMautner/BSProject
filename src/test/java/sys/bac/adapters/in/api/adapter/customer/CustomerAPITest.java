package sys.bac.adapters.in.api.adapter.customer;

import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import sys.bac.adapters.in.api.models.CustomerDTO;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class CustomerAPITest {

    @InjectMock
    CustomerServiceAdapter cSA;

    static long id = 1; 
    @Test
    public void createCustomer201Test() {
        
        when(cSA.createCustomer(any()))
        .thenReturn(new CustomerDTO(id++, "Bond", "James", "test@test.co.uk", "+44 123 12345678"));
        
        given().contentType(ContentType.JSON)
            .body("{\"surname\":\"James\", \"name\":\"James\", \"email\":\"test@test.co.uk\", \"phone\":\"+44 12312345678\"}")
        .when()
            .post("/customers")
        .then()
            .statusCode(201)
            .header("Cache-Control", "no-cache, no-transform, no-store")
            .header("Location", "<http://localhost:8081/customers/1>;rel=\"getCustomer\";type=\"application/json\"")
            .header("content-length", "0");
    }
}
