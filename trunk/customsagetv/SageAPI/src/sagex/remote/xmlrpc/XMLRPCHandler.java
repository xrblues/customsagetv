package sagex.remote.xmlrpc;

import sagex.remote.AbstractRPCHandler;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;
import sagex.remote.factory.request.SageRPCRequestFactory;

/**
 * Handles a XML RPC Request and Returns back an xml response
 * 
 * @author seans
 * 
 */
public class XMLRPCHandler extends AbstractRPCHandler {
	public String handleRPCCall(String api, String command, String context, String args[]) {
		RemoteResponse response = new RemoteResponse();
		try {
			// decode the string parameters and convert them to a real request
			RemoteRequest request = SageRPCRequestFactory.createRequest(context, api, command, args);

			// handle the call
			handleRPCCall(request, response);

			// serialize the remoteresponse into xml for the reply.
			return XmlEncoderHelper.createXmlResponse(api, command, request, response);
		} catch (Throwable t) {
			response.setError(501, "Xml RPC Failed!", t);
			return XmlEncoderHelper.createXmlResponse(api, command, null, response);
		}
	}
}
