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

Initial developer(s): Denis Conan
Contributor(s):
 */
package vlibtour.vlibtour_tour_management.api;

import javax.ejb.Remote;
import java.util.Collection;
import vlibtour.vlibtour_tour_management.entity.Tour;
import vlibtour.vlibtour_tour_management.entity.POI;

/**
 * This interface defines the operation for managing POIs and Tours. This is the
 * interface of the VLibTour Tour Management System.
 * 
 * @author Denis Conan
 */
@Remote
public interface VlibTourTourManagement {
	
	/**
	 * Insert Customer and Orders.
	 * 
	 * @return the string "OK" if there is no problem.
	 */
	String testInsert();

	/**
	 * verifies the insertion.
	 * 
	 * @return the string "OK" if there is no problem.
	 */
	String verifyInsert();

	/**
	 * deletes the given customer.
	 * 
	 * @param t
	 *            the customer and the associated orders.
	 * @return the string "OK" if there is no problem.
	 */
	String testDelete(Tour t);

	/**
	 * verifies the deletion.
	 * 
	 * @return the string "OK" if there is no problem.
	 */
	String verifyDelete();

	
	/**
	 * gets a detached instance of a Tour.
	 * 
	 * @param name
	 *            the name of the tour to search for.
	 * @return the tour object.
	 */
	Tour findTour(String name);
	
	/**
	 * gets a detached instance of a Poi.
	 * 
	 * @param name
	 *            the name of the Poi to search for.
	 * @return the Poi object.
	 */
	POI findPoi(String name);
	
	/**
	 * gets the set of all tours.
	 * 
	 * @return the set of tours.
	 */
	Collection<Tour> listTours();
	
	/**
	 * creates a new tour.
	 * 
	 * @return Ok.
	 */
	String createTour(String id, String name, String desc);
	
	/**
	 * Adds a Poi to a tour.
	 * 
	 * @return Ok.
	 */
	String addPoi(POI poi, String name);
	
	/**
	 * creates a new Poi.
	 * 
	 * @return Ok.
	 */
	String createPoi(String id, String name, String desc, double longi, double lat, int dur);
}
