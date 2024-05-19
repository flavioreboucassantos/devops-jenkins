package com.br.flavioreboucassantos.devops.test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.br.flavioreboucassantos.devops.dto.DtoArea;
import com.br.flavioreboucassantos.devops.entity.EntityArea;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;

@QuarkusTest
public class TestControllerArea {

//	@TestHTTPEndpoint(ControllerArea.class)
//	@TestHTTPResource // BUG java.net.MalformedURLException
//	URL baseUrlRestApi;

	final String pathApi = "/api";

	@BeforeAll
	@Transactional
	static public void setUP() {
		final EntityArea entityAreaUniquePositive = new EntityArea();
		entityAreaUniquePositive.rawData = "rawData";
		entityAreaUniquePositive.uniqueData = "uniqueData";
		entityAreaUniquePositive.highlighted = true;
		entityAreaUniquePositive.persist();
		
		final EntityArea entityAreaToRemovePositive = new EntityArea();
		
		final EntityArea entityAreaToRemoveNegative = new EntityArea();
		
	}

	@Test
	@DisplayName("createPost-CREATED")
	public void createArea_CREATED() {
		DtoArea dtoArea = new DtoArea(-1, "createArea_CREATED", "createArea_CREATED", true);

		Response response = given()
				.contentType(ContentType.JSON)
				.body(dtoArea)
				.when()
				.post(pathApi)
				.then()
				.extract()
				.response();

		assertEquals(jakarta.ws.rs.core.Response.Status.CREATED.getStatusCode(), response.statusCode());
		assertNotNull(response.jsonPath().getString("idArea"));
	}
	
	@Test
	@DisplayName("createPost-NOT_MODIFIED")
	public void createArea_NOT_MODIFIED() {
		DtoArea dtoArea = new DtoArea(-1, "createArea_NOT_MODIFIED", "uniqueData", true);

		Response response = given()
				.contentType(ContentType.JSON)
				.body(dtoArea)
				.when()
				.post(pathApi)
				.then()
				.extract()
				.response();

		assertEquals(jakarta.ws.rs.core.Response.Status.NOT_MODIFIED.getStatusCode(), response.statusCode());		
	}
	
	@Test
	@DisplayName("removeArea-id-NOT_FOUND")
	public void removeArea_id_NOT_FOUND() {
	}

}
