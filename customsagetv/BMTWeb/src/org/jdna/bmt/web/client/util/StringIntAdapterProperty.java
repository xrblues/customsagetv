package org.jdna.bmt.web.client.util;

public class StringIntAdapterProperty extends Property<String> {
	private static final long serialVersionUID = 1L;
	private Property<Integer> prop = null;
    public StringIntAdapterProperty(Property<Integer> prop) {
        super();
        this.prop=prop;
    }
    
    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.util.Property#get()
     */
    @Override
    public String get() {
        return String.valueOf(prop.get());
    }
    
    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.util.Property#set(java.lang.Object)
     */
    @Override
    public void set(String value) {
        prop.set(Integer.parseInt(value));
    }
}
