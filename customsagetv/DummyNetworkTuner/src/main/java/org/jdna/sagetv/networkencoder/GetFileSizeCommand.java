package org.jdna.sagetv.networkencoder;

/**
 * Command for GET_FILE_SIZE
 * 
 * @author seans
 *
 */
public class GetFileSizeCommand implements ICommand {
	public static final String CMD_NAME = "GET_FILE_SIZE";
	private String filename;

	public GetFileSizeCommand(String msgBody) {
		filename = msgBody.substring(CMD_NAME.length() + 1);
	}

	public String getFilename() {
		return filename;
	}

	public String toString() {
	    return "[" + CMD_NAME + ": " + filename +"]";
	}
}
