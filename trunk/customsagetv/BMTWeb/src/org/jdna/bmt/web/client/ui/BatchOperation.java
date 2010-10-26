package org.jdna.bmt.web.client.ui;

import java.io.Serializable;

/**
 * Builder Style Class for building up a Batch Operation
 * 
 * @author seans
 */
public class BatchOperation implements Serializable {
	private static final long serialVersionUID = 1L;

	private String label;
	private String visitorClass;
	private String confirm;
	private boolean background = false;
	private String startMessage;
	private String completeMessage;
	private Serializable visitorArg;
	
	public BatchOperation() {
	}

	public String getLabel() {
		return label;
	}

	public BatchOperation setLabel(String label) {
		this.label = label;
		return this;
	}

	public String getVisitorClass() {
		return visitorClass;
	}

	public BatchOperation setVisitorClass(String visitorClass) {
		this.visitorClass = visitorClass;
		return this;
	}

	public String getConfirm() {
		return confirm;
	}

	public BatchOperation setConfirm(String confirm) {
		this.confirm = confirm;
		return this;
	}

	public boolean isBackground() {
		return background;
	}

	public BatchOperation setBackground(boolean background) {
		this.background = background;
		return this;
	}

	public String getStartMessage() {
		return startMessage;
	}

	public BatchOperation setStartMessage(String startMessage) {
		this.startMessage = startMessage;
		return this;
	}

	public String getCompleteMessage() {
		return completeMessage;
	}

	public BatchOperation setCompleteMessage(String completeMessage) {
		this.completeMessage = completeMessage;
		return this;
	}

	public Serializable getVisitorArg() {
		return visitorArg;
	}

	public void setVisitorArg(Serializable visitorArg) {
		this.visitorArg = visitorArg;
	}
}
