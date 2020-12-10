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
package vlibtour.vlibtour_tour_management.bean;

import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import vlibtour.vlibtour_tour_management.entity.Tour;
import vlibtour.vlibtour_tour_management.entity.POI;
import vlibtour.vlibtour_tour_management.api.VlibTourTourManagement;

/**
 * This class defines the EJB Bean of the VLibTour tour management system.
 * 
 * @author Denis Conan
 */
@Stateless
public class VlibTourTourManagementBean implements VlibTourTourManagement {
	

	/**
	 * the reference to the entity manager, which persistence context is "pu1".
	 */
	@PersistenceContext(unitName = "pu1")
	private EntityManager em;

	@Override
	public String testInsert() {
		
		
		// Create a new Tour
		Tour tour1 = new Tour();
		tour1.setTid("1");
		tour1.setName("The unusual Paris");
		tour1.setDescription("An unusual trip!");
		// Persist the tour
		em.persist(tour1);
		// Create 3 POIs
		POI poi1 = new POI();
		poi1.setPoid("1");
		poi1.setPoiName("Musée Grévin");
		poi1.setPoiDescription("Musée de cire sur l’histoire de France");
		poi1.setLatitude(48.871799);
		poi1.setLongitude(2.342355);
		poi1.setDuration(60);
		
		POI poi2 = new POI();
		poi2.setPoid("2");
		poi2.setPoiName("Pyramide du Louvres");
		poi2.setPoiDescription("a  pyramide  du  Louvre  est  une  pyramide  constituée  "
			+ "de verre et de métal, située au milieu de la cour Napol ́eon du mus ́ee du Louvre `a Paris");
		poi2.setLatitude(48.860959);
		poi2.setLongitude(2.335757);
		poi2.setDuration(20);
		
		POI poi3 = new POI();
		poi3.setPoid("3");
		poi3.setPoiName("Les catacombes de Paris");
		poi3.setPoiDescription("Labyrinthe  ́eclairé dans une ancienne mine de calcaire "
				+ "avec des millionsde squelettes macabres entassées");
		poi3.setLatitude(48.833566);
		poi3.setLongitude(2.332416);
		poi3.setDuration(60);
		
		// Associate pois with tour.
		tour1.getPOIs().add(poi1);
		poi1.getTours().add(tour1);
		tour1.getPOIs().add(poi2);
		poi2.getTours().add(tour1);
		tour1.getPOIs().add(poi3);
		poi3.getTours().add(tour1);

		return "OK";
	}

	@Override
	public String verifyInsert() {
		Tour t = findTour("The unusual Paris");
		Collection<POI> pois = t.getPOIs();
		if (pois == null || pois.size() != 3) {
			throw new RuntimeException(
					"Unexpected number of pois: " + ((pois == null) ? "null" : "" + pois.size()));
		}
		return "OK";
	}

	@Override
	public String testDelete(final Tour t) {
		// Merge the customer to the new persistence context
		Tour t0 = em.merge(t);
		// Delete all records.
		em.remove(t0);
		return "OK";
	}

	@Override
	public String verifyDelete() {
		Query q = em.createQuery("select t from Tour t");
		@SuppressWarnings("rawtypes")
		List results = q.getResultList();
		if (results == null || results.size() != 0) {
			throw new RuntimeException("Unexpected number of tours after delete results : " + results.size());
		}
		q = em.createQuery("select p from POI p");
		results = q.getResultList();
		if (results == null || results.size() != 0) {
			throw new RuntimeException("Unexpected number of pois after delete");
		}
		return "OK";
	}

	
	@Override
	public Tour findTour(final String name) {
		Query q = em.createQuery("select t from Tour t where t.name = :name");
		q.setParameter("name", name);
		return (Tour) q.getSingleResult();
	}
	
	@Override
	public POI findPoi(final String name) {
		Query q = em.createQuery("select p from POI p where p.name = :name");
		q.setParameter("name", name);
		return (POI) q.getSingleResult();
	}
	
	@Override
	public Collection<Tour> listTours() {
		Query q = em.createQuery("select * from Tour");
		return (Collection<Tour>) q.getResultList();
	}
	
	@PersistenceContext(unitName = "pu1")
	private EntityManager em1;

	@Override
	public String createTour(String id, String name, String desc) {
		
		// Create a new Tour
		Tour tour1 = new Tour();
		tour1.setTid(id);
		tour1.setName(name);
		tour1.setDescription(desc);
		// Persist the tour
		em1.persist(tour1);
		return("Ok");
	}
	
	@Override
	public String addPoi(POI poi, String name) {
		
		// Create a new Tour.
		Tour tour1 = findTour(name);
		tour1.getPOIs().add(poi);
		return("Ok");
	}
	
	@PersistenceContext(unitName = "pu1")
	private EntityManager em2;

	@Override
	public String createPoi(String id, String name, String desc, double longi, double lat, int dur) {
		
		// Create a new poi.
		POI poi1 = new POI();
		poi1.setPoid(id);
		poi1.setPoiName(name);
		poi1.setPoiDescription(desc);
		poi1.setLongitude(longi);
		poi1.setLatitude(lat);
		poi1.setDuration(dur);
		
		// Persist the poi.
		em2.persist(poi1);
		return("Ok");
	}
	
}
