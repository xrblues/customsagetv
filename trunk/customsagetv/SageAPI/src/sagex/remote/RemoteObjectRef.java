package sagex.remote;

import java.io.Serializable;

/**
 * Wrapper class the wraps a remote object instance on the server. The id that
 * is provided should be unique across all objects on the server, the the server
 * must be able to resolve the object reference from the id that is stored.
 * 
 * All remote api calls that return a sage object will actually return an object
 * reference.
 * 
 * @author seans
 * 
 */
public class RemoteObjectRef implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private int index = -1;
	private int arraySize;
	private boolean isarray;

	/**
	 * created a standard remote object reference
	 */
	public RemoteObjectRef() {
		this.id = String.valueOf(hashCode());
	}

	/**
	 * creates a array remote object reference
	 * 
	 * @param arrayLen
	 */
	public RemoteObjectRef(int arrayLen) {
		this.id = String.valueOf(hashCode());
		this.arraySize = arrayLen;
		this.isarray = true;
	}

	/**
	 * creates an array item remote object reference
	 * 
	 * @param r
	 *            parent object array reference
	 * @param index
	 *            index of this item in the parent object array reference
	 */
	public RemoteObjectRef(RemoteObjectRef r, int index) {
		this.id = r.getId();
		this.arraySize = r.getArraySize();
		this.isarray = false;
		this.index = index;
	}

	public int getArraySize() {
		return arraySize;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getIndex() {
		return index;
	}

	public boolean isArray() {
		return isarray;
	}

	public void setIndex(int idx) {
		this.index = idx;
	}

	public String toString() {
		String v;
		if (index != -1) {
			v = getId() + ":" + getIndex();
		} else {
			v = getId();
		}
		return "RemoteObjectRef[" + v + "];";
	}
}
