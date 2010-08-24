package org.jdna.bmt.web.client.ui.browser;

public enum BatchOperation {
	WATCHED("Watch"),
	UNWATCHED("UnWatch"),
	ARCHIVE("Archive"),
	UNARCHIVE("UnArchive"),
	IMPORTASRECORDING("Import As Recording"),
	UNIMPORTASRECORDING("Move to Video Library"),
	CLEANFANART("Remove Fanart Files"),
	CLEANPROPERTIES("Remove .properties Files"),
	CLEARCUSTOMMETADATA("Clear Custom Metadata Fields")
	;
	String label;
	BatchOperation(String label) {
		this.label=label;
	}
	
	public String label() {
		return label;
	}
}
