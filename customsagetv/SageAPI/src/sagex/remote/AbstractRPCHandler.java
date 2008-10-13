package sagex.remote;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import sagex.SageAPI;

/**
 * Accepts RemoteRequest object and returns a RemoteResponse object.
 * 
 * The RemoteRequest is decoded and passed off to the SageAPI. The response from
 * sage is then placed into the RemoteResponse.
 * 
 * All native sage objects are converted into RemoteObjectRef objects, so they
 * are never sent back directly. All object arrays that are not serializable are
 * also sent back as RemoteObjectRef objects. Serializable Objects and Arrays
 * are sent back in full.
 * 
 * Local Object References are stored in a WeakHashMap so there is the change
 * that objects in the Map will disappear before they are accessed. This will
 * have undetermined results in the calling application.
 * 
 * @author seans
 * 
 */
public abstract class AbstractRPCHandler implements IRCPHandler {
	// for now we are using a weak hashmap, but we really should use a thread
	// and clean out stale items ourself.
	private Map<String, Object> objectRefs = new HashMap<String, Object>();

	public void handleRPCCall(RemoteRequest request, RemoteResponse response) {
		try {
			// convert object references in the request to sage references...
			Object oArr[] = request.getParameters();
			if (oArr != null && oArr.length > 0) {
				for (int i = 0; i < oArr.length; i++) {
					Object o = oArr[i];
					if (o instanceof RemoteObjectRef) {
						RemoteObjectRef ref = (RemoteObjectRef) o;
						// replace this reference with the real thing...
						Object oref = getReference(ref);
						// if out reference in an array, then we need to get the
						// indexed element and not the array.
						if (oref.getClass().isArray()) {
							oArr[i] = ((Object[]) oref)[ref.getIndex()];
						} else {
							oArr[i] = oref;
						}
					}
				}
			}

			// invoke the server
			Object oreply = null;
			if (request.getContext() != null) {
				oreply = SageAPI.call(request.getContext(), request.getCommand(), request.getParameters());
			} else {
				oreply = SageAPI.call(request.getCommand(), request.getParameters());
			}

			// convert the sage reply to something serializable, ie, convert the
			// sage references to object references..
			Object finalReply = null;
			RemoteObjectRef replyRef = null;
			if (oreply != null) {
				if (oreply.getClass().isArray()) {
					if (oreply.getClass().getComponentType().isPrimitive() || Serializable.class.isAssignableFrom(oreply.getClass().getComponentType())) {
						// we can send back primitives and serialiable arrays
						finalReply = oreply;
					} else {
						// non primitive / array - convert to an object
						// reference
						replyRef = new RemoteObjectRef(((Object[]) oreply).length);
						finalReply = replyRef;
					}
				} else {
					// standard objects/etc
					if (oreply.getClass().isPrimitive() || Serializable.class.isAssignableFrom(oreply.getClass())) {
						// serializiable stuff... ok with that.
						finalReply = oreply;
					} else {
						// non primitive / non serializable objects - conver to
						// object reference
						replyRef = new RemoteObjectRef();
						finalReply = replyRef;
					}
				}
			}
			// if an object reference was created, then store it
			if (replyRef != null) {
				setReference(replyRef, oreply);
			}

			// send back the data
			response.setData(finalReply);
		} catch (Throwable t) {
			response.setError(404, "Command Failed: " + (request != null ? request.getCommand() : ""), t);
		}

	}

	public Object getReference(RemoteObjectRef ref) {
		if (ref == null)
			throw new RuntimeException("Object Reference is null");
		try {
			return objectRefs.get(ref.getId());
		} catch (Throwable t) {
			throw new RuntimeException("Invalid Object Reference: " + ref.getId() + "; It may be that the reference has been cleaned up.");
		}
	}

	public void setReference(RemoteObjectRef ref, Object o) {
		objectRefs.put(ref.getId(), o);
	}
}
