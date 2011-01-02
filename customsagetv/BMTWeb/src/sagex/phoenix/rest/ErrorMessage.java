package sagex.phoenix.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Simple Error Message container
 * 
 * @author seans
 *
 */
public class ErrorMessage {
	public String errorCode;
	public String errorMessage;
	public String exception;
	
	public ErrorMessage(String code, String msg, Throwable t) {
		this.errorCode=code;
		if (msg==null && t!=null) {
			this.errorMessage=t.getMessage();
		} else {
			this.errorMessage=msg;
		}
		
		if (t!=null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			pw.flush();
			exception = sw.toString();
		}
	}

	@Override
	public String toString() {
		return "ErrorMessage [errorCode=" + errorCode + ", errorMessage="
				+ errorMessage + ", exception=" + exception + "]";
	}
}
