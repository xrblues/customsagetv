package sagex.remote;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
			// convert object references in the request into real sage references...
			Object oArr[] = request.getParameters();
			if (oArr != null && oArr.length > 0) {
				for (int i = 0; i < oArr.length; i++) {
					Object o = oArr[i];
					if (o==null) continue;
					if (o.getClass().isArray() && RemoteObjectRef.class.isAssignableFrom(o.getClass().getComponentType())) {
						System.out.println("Converting Remote Object Reference Array into a Sage Array.");
						// check if the incomming object parameter is an array of RemoteObjectReferences
						// if so, then convert the array, into a real array
						Object oo[] = (Object[])o;
						if (oo.length>0) {
							// arrays are stored under the same ref id as each of the children
							oArr[i] = getReference((RemoteObjectRef) oo[0]);
						} else {
							// empty object array;
							oArr[i] = new Object[0];
						}
					} else if (o instanceof RemoteObjectRef) {
						System.out.println("Converting Remote Object Reference into a Sage Reference.");
						RemoteObjectRef ref = (RemoteObjectRef) o;
						// replace this reference with the real thing...
						Object oref = getReference(ref);
						// if out reference in an array, then we need to get the
						// indexed element and not the array.
						if (oref.getClass().isArray() && ref.getIndex()!=-1) {
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
						System.out.println("Converting Sage Object Array into a Remote Object Reference Array.");
						// non primitive / array - convert to an object reference
						replyRef = new RemoteObjectRef(((Object[]) oreply));
						
						// our reply should be an array of remote object references
						finalReply = replyRef.getRemoteObjectReferenceArray();
					}
				} else {
					// standard objects/etc
					if (oreply.getClass().isPrimitive() || Serializable.class.isAssignableFrom(oreply.getClass())) {
						// serializiable stuff... ok with that.
						finalReply = oreply;
					} else {
						System.out.println("Converting Sage Object into a Remote Object Reference.");
						// non primitive / non serializable objects - convert to
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
			System.out.printf("----------- Sage Handling of a Remote Command Failed: %s ---------\n", request);
			t.printStackTrace(System.out);
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
