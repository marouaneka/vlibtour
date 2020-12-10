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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.io.Serializable;



/**
 * The entity bean defining a tour in the VLibTour case study. A tour is a
 * sequence of points of interest ({@link POI}).
 * 
 * For the sake of simplicity, we suggest that you use named queries.
 * 
 * @author Denis Conan
 */
@Entity
@Table(name = "TOUR")
public class Tour implements Serializable {
	/**
	 * the identifier.
	 */
	@Id
	@GeneratedValue
	private String tid;
	/**
	 * the shipping address.
	 */
	private String name;
	/**
	 * the description of the tour.
	 */
	private String description;
	/**
	 * the collection of POIs.
	 */
	@ManyToMany(cascade = ALL, fetch = FetchType.EAGER)
	@JoinTable( name = "Tours_Pois",
    joinColumns = @JoinColumn( name = "tid" ),
    inverseJoinColumns = @JoinColumn( name = "poid" ) )
	private Collection<POI> pois = new ArrayList<POI>();
	
	@Column(name = "TOUR_ID")
	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public void setPOIs(Collection<POI> pois) {
		this.pois = pois;
	}
	
	/**
	 * gets the collection of POIs.
	 * 
	 * @return the collection.
	 */
	public Collection<POI> getPOIs() {
		return pois;
	}

}
