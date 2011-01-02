package sagex.phoenix.rest;

/**
 * Service Exception class.  getServiceMessage will return a message structure that can used
 * to send back to a client.
 * 
 * @author seans
 *
 */
public class ServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private ErrorMessage message = null;
	
	public ServiceException() {
		this(null,null,null);
	}

	public ServiceException(String messageId, String message) {
		this(messageId, message, null);
	}

	public ServiceException(String messageId, String message, Throwable error) {
		super(message, error);
		this.message=new ErrorMessage(messageId, message, (error==null)?this:error);
	}

	public String getMessageId() {
		return message.errorCode;
	}
	
	public Object getServiceMessage() {
		return message;
	}
}
