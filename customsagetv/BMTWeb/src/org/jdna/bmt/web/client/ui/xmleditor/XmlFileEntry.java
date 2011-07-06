package org.jdna.bmt.web.client.ui.xmleditor;

import java.io.Serializable;

import org.jdna.bmt.web.client.ui.xmleditor.PhoenixConfiguration.ConfigType;

public class XmlFileEntry implements Serializable {
	public static enum FileType {System, Plugin, User};
	private static final long serialVersionUID = 1L;

	public static class ParserError implements Serializable {
		private static final long serialVersionUID = 1L;
		public int line;
		public int col;
		public String message;
	}
	
	public FileType fileType;
	public ConfigType configType;
	public String file;
	public String name;
	public String contents;
	public ParserError error;
}
