package com.br.flavioreboucassantos.devops.test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.br.flavioreboucassantos.devops.controller.ControllerArea;
import com.br.flavioreboucassantos.devops.dto.DtoArea;
import com.br.flavioreboucassantos.devops.entity.EntityArea;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.transaction.Transactional;

@QuarkusTest
@TestHTTPEndpoint(ControllerArea.class)
public class TestControllerArea extends BaseTest {

	static final long idNotFound = Long.MAX_VALUE;

	static EntityArea entityAreaToUniquePositive;
	static EntityArea entityAreaToRemovePositive;
	static EntityArea entityAreaToFindByIdPositive;
	static EntityArea entityAreaToUpdateByIdPositive;

	@BeforeAll
	@Transactional
	static public void setUP() {
		entityAreaToUniquePositive = new EntityArea();
		entityAreaToUniquePositive.rawData = rndStr();
		entityAreaToUniquePositive.uniqueData = rndStr();
		entityAreaToUniquePositive.highlighted = true;
		entityAreaToUniquePositive.persist();

		entityAreaToRemovePositive = new EntityArea();
		entityAreaToRemovePositive.rawData = rndStr();
		entityAreaToRemovePositive.uniqueData = rndStr();
		entityAreaToRemovePositive.highlighted = true;
		entityAreaToRemovePositive.persist();

		entityAreaToFindByIdPositive = new EntityArea();
		entityAreaToFindByIdPositive.rawData = rndStr();
		entityAreaToFindByIdPositive.uniqueData = rndStr();
		entityAreaToFindByIdPositive.highlighted = true;
		entityAreaToFindByIdPositive.persist();

		entityAreaToUpdateByIdPositive = new EntityArea();
		entityAreaToUpdateByIdPositive.rawData = rndStr();
		entityAreaToUpdateByIdPositive.uniqueData = rndStr();
		entityAreaToUpdateByIdPositive.highlighted = true;
		entityAreaToUpdateByIdPositive.persist();
	}

	@Test
	@DisplayName("createPost-CREATED")
	public void createArea_CREATED() {
		DtoArea dtoArea = new DtoArea(-1, rndStr(), rndStr(), true);

		Response response = given()
				.contentType(ContentType.JSON)
				.body(dtoArea)
				.post()
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_CREATED, response.statusCode());
		assertNotNull(response.jsonPath().getString("idArea"));
	}

	@Test
	@DisplayName("createPost-NOT_MODIFIED")
	public void createArea_NOT_MODIFIED() {
		DtoArea dtoArea = new DtoArea(-1, rndStr(), entityAreaToUniquePositive.uniqueData, true);

		Response response = given()
				.contentType(ContentType.JSON)
				.body(dtoArea)
				.post()
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_MODIFIED, response.statusCode());
	}

	@Test
	@DisplayName("removeArea-id_NOT_FOUND")
	public void removeArea_id_NOT_FOUND() {
		Response response = given()
				.contentType(ContentType.JSON)
				.pathParam("id", idNotFound)
				.delete("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_FOUND, response.statusCode());
		assertEquals("removeArea-id_NOT_FOUND", response.body().asString());
	}

	@Test
	@DisplayName("removeArea-OK")
	public void removeArea_OK() {
		Response response = given()
				.contentType(ContentType.JSON)
				.pathParam("id", entityAreaToRemovePositive.idArea)
				.delete("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_OK, response.statusCode());
	}

	@Test
	@DisplayName("findByIdArea-NOT_FOUND")
	public void findByIdArea_NOT_FOUND() {
		Response response = given()
				.contentType(ContentType.JSON)
				.pathParam("id", idNotFound)
				.get("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_FOUND, response.statusCode());
		assertEquals("findByIdArea-id_NOT_FOUND", response.body().asString());
	}

	@Test
	@DisplayName("findByIdArea-OK")
	public void findByIdArea_OK() {
		Response response = given()
				.contentType(ContentType.JSON)
				.pathParam("id", entityAreaToFindByIdPositive.idArea)
				.get("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_OK, response.statusCode());
		assertNotNull(response.jsonPath().getString("idArea"));
	}

	@Test
	@DisplayName("updateByIdArea-NOT_FOUND")
	public void updateByIdArea_NOT_FOUND() {
		Response response = given()
				.contentType(ContentType.JSON)
				.body(entityAreaToUpdateByIdPositive)
				.pathParam("id", idNotFound)
				.put("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_FOUND, response.statusCode());
		assertEquals("updateByIdArea-id_NOT_FOUND", response.body().asString());
	}

	@Test
	@DisplayName("updateByIdArea-NOT_MODIFIED")
	public void updateByIdArea_NOT_MODIFIED() {
		entityAreaToUpdateByIdPositive.uniqueData = entityAreaToUniquePositive.uniqueData;
		Response response = given()
				.contentType(ContentType.JSON)
				.body(entityAreaToUpdateByIdPositive)
				.pathParam("id", entityAreaToUpdateByIdPositive.idArea)
				.put("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_MODIFIED, response.statusCode());
	}

	@Test
	@DisplayName("updateByIdArea-OK")
	public void updateByIdArea_OK() {
		entityAreaToUpdateByIdPositive.rawData = rndStr();
		entityAreaToUpdateByIdPositive.uniqueData = rndStr();
		Response response = given()
				.contentType(ContentType.JSON)
				.body(entityAreaToUpdateByIdPositive)
				.pathParam("id", entityAreaToUpdateByIdPositive.idArea)
				.put("/{id}")
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_OK, response.statusCode());
		assertTrue(!isList(response.jsonPath().get("")));
		assertNotNull(response.jsonPath().getString("idArea"));
	}

	@Test
	@DisplayName("findAllArea-OK")
	public void findAllArea_OK() {
		Response response = given()
				.contentType(ContentType.JSON)
				.get()
				.then()
				.extract()
				.response();

		assertEquals(HTTP_STATUSCODE_NOT_MODIFIED, response.statusCode());
		assertTrue(isList(response.jsonPath().get("")));
		assertNotNull(response.jsonPath().getString("idArea"));
	}

}
