/**
This file is part of the course CSC5002.

Copyright (C) 2017-2020 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the muDEBS platform. If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Chantal Taconet and Denis Conan
Contributor(s):
 */
package vlibtour.vlibtour_visit_emulation_proxy;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.MediaType;


import vlibtour.vlibtour_common.ExampleOfAVisitWithTwoTourists;
import vlibtour.vlibtour_common.Position;



/**
 * The REST Proxy of the VLibTour Visit Emulation Server.
 */
public final class VLibTourVisitEmulationProxy {
	private static WebTarget service;

public VLibTourVisitEmulationProxy() {
	Client client = ClientBuilder.newClient();
	URI uri = UriBuilder.fromUri(ExampleOfAVisitWithTwoTourists.BASE_URI_WEB_SERVER).build();
	 setService(client.target(uri));
 
}

public synchronized Position getNextPOIPosition(final String user) {
	Response jsonResponse = service
			.path("visitemulation/getNextPOIPosition/" + user).request()
			.accept(MediaType.APPLICATION_JSON).get();
	Position position = jsonResponse.readEntity(Position.class);
	return position;
}
public synchronized Position getCurrentPosition(final String user) {
	Response jsonResponse = service
			.path("visitemulation/getCurrentPosition/" + user).request()
			.accept(MediaType.APPLICATION_JSON).get();
	Position position = jsonResponse.readEntity(Position.class);
	return position;
}
public synchronized Position stepInCurrentPath(final String user) {
	Response jsonResponse = service
			.path("visitemulation/stepInCurrentPath/" + user).request()
			.accept(MediaType.APPLICATION_JSON).get();
	Position position = jsonResponse.readEntity(Position.class);
	return position;
}
public synchronized Position stepsInVisit(final String user) {
	Response jsonResponse = service
			.path("visitemulation/stepsInVisit/" + user).request()
			.accept(MediaType.APPLICATION_JSON).get();
	Position position = jsonResponse.readEntity(Position.class);
	return position;
}


public WebTarget getService() {
	return service;
}

public void setService(WebTarget service) {
	VLibTourVisitEmulationProxy.service = service;
}

}

