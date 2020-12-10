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
package vlibtour.vlibtour_tour_management.entity;

import java.util.ArrayList;
import java.util.Collection;
import static javax.persistence.CascadeType.ALL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


import java.io.Serializable;

/**
 * The entity bean defining a point of interest (POI). A {@link Tour} is a
 * sequence of points of interest.
 * 
 * For the sake of simplicity, we suggest that you use named queries.
 * 
 * @author Denis Conan
 * 
 */
@Entity
@Table(name = "POI")
public class POI implements Serializable {
	
	/**
	 * the identifier.
	 */
	@Id
	@GeneratedValue
	private String poid;
	/**
	 * the name of POI.
	 */
	private String poiName;
	/**
	 * the description of the POI.
	 */
	private String poiDescription;
	
	/**
	 * the longitude of the location.
	 */
	private double longitude;

	/**
	 * the latitude of the location.
	 */
	private double latitude;

	/**
	 * the longitude of the location.
	 */
	private int duration;
	/**
	/**
	 * the collection of tours.
	 */
	@ManyToMany(cascade = ALL,fetch = FetchType.EAGER, mappedBy = "pois")
	private Collection<Tour> tours = new ArrayList<Tour>();
	
	@Column(name = "POI_ID")
	public String getPoid() {
		return poid;
	}

	public void setPoid(String poid) {
		this.poid = poid;
	}

	public String getPoiName() {
		return poiName;
	}

	public void setPoiName(String poiName) {
		this.poiName = poiName;
	}

	public String getPoiDescription() {
		return poiDescription;
	}

	public void setPoiDescription(String poiDescription) {
		this.poiDescription = poiDescription;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}


	public void setTours(Collection<Tour> tours) {
		this.tours = tours;
	}

	/**
	 * gets the collection of tours.
	 * 
	 * @return the collection.
	 */
	public Collection<Tour> getTours() {
		return tours;
	}



}
