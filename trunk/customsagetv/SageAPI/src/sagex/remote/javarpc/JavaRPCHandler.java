package sagex.remote.javarpc;

import java.io.IOException;

import sagex.remote.AbstractRPCHandler;
import sagex.remote.MarshalUtils;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;

public class JavaRPCHandler extends AbstractRPCHandler {

	public String handleRPCCall(String payload) {
		RemoteResponse response = new RemoteResponse();
		if (payload == null) {
			response.setError(500, "Request did not contain a RPC Call.", new Exception("Missing Payload"));
		} else {
			try {
				RemoteRequest request = (RemoteRequest) MarshalUtils.unmarshal(payload);
				handleRPCCall(request, response);
			} catch (Exception e) {
				response.setError(500, "Failed to process the RPC Call!", e);
			}
		}

		try {
			return MarshalUtils.marshal(response);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}
}
