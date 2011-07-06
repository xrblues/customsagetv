package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

public class ConfigError implements Serializable {
	public String file;
	public String message;
	public int line;
	public int column;
	public long datetime;
	
	public ConfigError() {
	}
}
