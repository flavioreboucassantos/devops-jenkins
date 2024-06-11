package com.br.flavioreboucassantos.devops.route;

import io.quarkus.vertx.web.RouteFilter;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Flávio Rebouças Santos - flavioReboucasSantos@gmail.com
 */
public final class MyFilters {

	@RouteFilter(100)
	public void myFilter(final RoutingContext rc) {
		final SocketAddress remoteAddress = rc.request().remoteAddress();
		final HttpServerResponse response = rc.response();

		response.putHeader("X-Header", String.format("intercepting the request from %s:%s", remoteAddress.host(), remoteAddress.port()));
		rc.next();

//		response.setStatusCode(Response.Status.TOO_MANY_REQUESTS.getStatusCode());
//		rc.end();		
	}

}
