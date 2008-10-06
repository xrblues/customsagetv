package sagex.remote;

import java.io.Serializable;

public class RemoteRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	private String command;
	private Object[] parameters;
	public RemoteRequest(String command, Object[] parameters) {
		super();
		this.command = command;
		this.parameters = parameters;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public RemoteRequest() {
	}
}
