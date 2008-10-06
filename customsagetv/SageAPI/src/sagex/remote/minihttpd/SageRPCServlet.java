package sagex.remote.minihttpd;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import sagex.SageAPI;
import sagex.remote.MarshalUtils;
import sagex.remote.RemoteObjectRef;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;

public class SageRPCServlet implements Servlet {
	public static final String SAGE_RPC_PATH = "/sagex/rpcJava";
	public static final String CMD_ARG = "request";
	
	private Map<String, Object> objectRefs = new HashMap<String, Object>();
	
	public SageRPCServlet() {
	}

	public void doGet(Request req, Response res) throws Exception {
		RemoteResponse remResp = new RemoteResponse();
		String payload = req.getParameter(CMD_ARG);
		try {
			if (payload == null) {
				throw new Exception("Missing Payload");
			}
		} catch (Throwable t) {
			remResp.setError(500, "Missing 'request' for rpc call.", t);
		}
		
		RemoteRequest remReq=null;
		try {
			// convert object references in the request to sage references...
			remReq = (RemoteRequest) MarshalUtils.unmarshal(payload);
			Object oArr[] = remReq.getParameters();
			if (oArr!=null&& oArr.length>0) {
				for (int i=0;i<oArr.length;i++) {
					Object o = oArr[i];
					if (o instanceof RemoteObjectRef) {
						RemoteObjectRef ref = (RemoteObjectRef) o;
						// replace this reference with the real thing...
						Object oref = getReference(ref);
						// if out reference in an array, then we need to get the indexed element and not the array.
						if (oref.getClass().isArray()) {
							oArr[i] = ((Object[])oref)[ref.getIndex()];
						} else {
							oArr[i] = oref;
						}
					}
				}
			}
			
			// invoke the server
			Object oreply = SageAPI.call(remReq.getCommand(), remReq.getParameters());
			
			// convert the sage reply to something serializable, ie, convert the sage references to object references..
			Object finalReply = null;
			RemoteObjectRef replyRef = null;
			if (oreply!=null) {
				if (oreply.getClass().isArray()) {
					if (oreply.getClass().getComponentType().isPrimitive() || Serializable.class.isAssignableFrom(oreply.getClass().getComponentType())) {
						// we can send back primitives and serialiable arrays
						finalReply = oreply;
					} else {
						// non primitive / array - convert to an object reference
						replyRef = new RemoteObjectRef(((Object[])oreply).length);
						finalReply = replyRef;
					}
				} else {
					// standard objects/etc
					if (oreply.getClass().isPrimitive() || Serializable.class.isAssignableFrom(oreply.getClass())) {
						// serializiable stuff... ok with that.
						finalReply = oreply;
					} else {
						// non primitive / non serializable objects - conver to object reference
						replyRef = new RemoteObjectRef();
						finalReply = replyRef;
					}
				}
			}
			// if an object reference was created, then store it
			if (replyRef!=null) {
				setReference(replyRef, oreply);
			}
			
			// send back the data
			remResp.setData(finalReply);
		} catch (Throwable t) {
			remResp.setError(404, "Command Failed: " + (remReq!=null ? remReq.getCommand() : ""), t);
		}
		
		res.setContentType("text/plain");
		PrintWriter pw = res.getWriter();
		pw.print(MarshalUtils.marshal(remResp));
		pw.flush();
	}
	
	public Object getReference(RemoteObjectRef ref) {
		return objectRefs.get(ref.getId());
	}
	
	public void setReference(RemoteObjectRef ref, Object o) {
		objectRefs.put(ref.getId(), o);
	}

}
