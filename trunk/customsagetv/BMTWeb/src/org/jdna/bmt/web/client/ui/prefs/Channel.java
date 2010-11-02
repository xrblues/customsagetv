package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class Channel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Property<Boolean> enabled = new Property<Boolean>();
	private String name, number, network, description;

	private int stationId;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String station) {
		this.number = station;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Channel() {
	}

	public Property<Boolean> enabled() {
		return enabled;
	}

	public void setStationId(int stationId) {
		this.stationId=stationId;
	}

	public int getStationId() {
		return stationId;
	}
}
