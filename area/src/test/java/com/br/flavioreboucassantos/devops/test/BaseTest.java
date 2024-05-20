package com.br.flavioreboucassantos.devops.test;

import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

public abstract class BaseTest {

	static final int HTTP_STATUSCODE_CREATED = 201;
	static final int HTTP_STATUSCODE_NOT_MODIFIED = 304;
	static final int HTTP_STATUSCODE_NOT_FOUND = 404;
	static final int HTTP_STATUSCODE_OK = 200;

	static public boolean isList(Object obj) {
		return obj instanceof List;
	}

	static public String getRandomString() {
		return UUID.randomUUID().toString();
	}

	static public String rndStr() {
		return getRandomString();
	}

	static public void out(final String x) {
		System.out.println(x);
	}

	static public void out(final long x) {
		System.out.println(x);
	}

	static public void out(Object list) {
		System.out.println(list);
	}

}
