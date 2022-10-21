
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static io.restassured.path.json.JsonPath.*;

import static io.restassured.response.Response.*;

public class RestAssuredClass extends BaseTest {



    @Test
    public  void LoginRequestTest(){

                given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("/login")
                .then()
                .statusCode(200)

                .body("token", notNullValue());

    }

    @Test
    public void getSingleUser(){

                given()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2))
                .body("data.email", notNullValue());

    }

    @Test
    public void deleteUserTest(){

        given()
                .delete("/users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

    }

    @Test
    public void patchUserTest(){

        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("/users/2")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getString("name");

        assertThat(nameUpdated, equalTo("morpheus"));

    }

    @Test
    public void putUserTest(){

        String jobUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .put("/users/2")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getString("job");

        assertThat(jobUpdated, equalTo("zion resident"));

    }

    @Test
    public void getAllUsersTest(){

        Response response = given().get("users?page=2");

        Headers headers= response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        System.out.println(headers);
        System.out.println(statusCode);
        System.out.println(body);
        System.out.println(contentType);

        assertThat(statusCode,equalTo(HttpStatus.SC_OK));

    }

    @Test
    public void getUsersTest(){
        String response = given().when().get("users?page=2").then().extract().body().asString();

        int page = from(response).get("page");
        int totalPages = from(response).get("total_pages");
        int idFirstUser = from(response).get("data[0].id");

        System.out.println("page" + page);
        System.out.println("total Pages" + totalPages);
        System.out.println("id First User" + idFirstUser);

        List<Map> idsMayoresa10 = from(response).get("data.findAll { user -> user.id > 10}");
        List<Map> usuario = from(response).get("data.findAll { user -> user.id > 10 && user.last_name == 'Howell'}");

        String email = idsMayoresa10.get(0).get("email").toString();



        System.out.println("email"+ email);
        System.out.println("email"+ idsMayoresa10);
        System.out.println("usuario"+ usuario);


    }

    @Test
    public void getUsersLength(){
        String response = given().when().get("users?page=2").then().extract().body().asString();

        List<Map> total = from(response).get("data.findAll {user -> user.id}");

        System.out.println("total "+ total.size());

    }

    @Test
    public void createUserTest(){
        CreateRequest user = new CreateRequest();
        user.setName("morpheus");
        user.setJob("leader");

        CreateResponse response = given()
                .when()
                .body(user)
                .post("/users")
                .then()
                .extract()
                .body()
                .as(CreateResponse.class);

        assertThat(response.getJob(), equalTo("leader"));
    }

    @Test
    public void registerUserTest(){
        RegisterRequest register = new RegisterRequest();
        register.setEmail("eve.holt@reqres.in");
        register.setPassword("pistol");

        RegisterResponse response = given()
                .when()
                .body(register)
                .post("/register")
                .then()
                .extract()
                .body()
                .as(RegisterResponse.class);

        assertThat(response.getToken(), equalTo("QpwL5tke4Pnpja7X4"));
    }


}
