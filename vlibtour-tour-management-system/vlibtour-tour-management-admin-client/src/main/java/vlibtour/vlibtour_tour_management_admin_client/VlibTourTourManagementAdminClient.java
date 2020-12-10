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
package vlibtour.vlibtour_tour_management_admin_client;

import javax.naming.InitialContext;

import vlibtour.vlibtour_tour_management.entity.Tour;
import vlibtour.vlibtour_tour_management.entity.POI;
import vlibtour.vlibtour_tour_management.api.VlibTourTourManagement;

/**
 * This class defines the administration client of the Tour Management System.
 * <ul>
 * <li>USAGE:
 * <ul>
 * <li>vlibtour.vlibtour_tour_management_admin_client.VlibTourAdminClient
 * populate toursAndPOIs</li>
 * <li>vlibtour.vlibtour_tour_management_admin_client.VlibTourAdminClient empty
 * toursAndPOIs</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Denis Conan
 */
public class VlibTourTourManagementAdminClient {
	/**
	 * constructs an instance of the administration client.
	 * 
	 * @throws Exception the exception thrown by the lookup.
	 */
	public VlibTourTourManagementAdminClient() throws Exception {
		throw new UnsupportedOperationException("Not implemented, yet.");
	}

	/**
	 * the main of the administration client.
	 * 
	 * @param args the command line arguments. See class documentation of this
	 *             class.
	 * @throws Exception the exception that can be thrown (none is treated).
	 */
	public static void main(final String[] args) throws Exception {
		VlibTourTourManagement sb;
		Tour t;
		try {
			InitialContext ic = new InitialContext();
			sb = (VlibTourTourManagement) ic.lookup("vlibtour.vlibtour_tour_management.api.VlibTourTourManagement");
			System.out.println("Inserting Tour and POIs... " + sb.testInsert());
			// Test query and navigation
			System.out.println("Verifying that all are inserted... " + sb.verifyInsert());
			// Get a detached instance
			t = sb.findTour("The unusual Paris");
			// Remove entity
			//System.out.println("Removing entity... " + sb.testDelete(c));
			// Query the results
			//System.out.println("Verifying that all are removed... " + sb.verifyDelete());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

