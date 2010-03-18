package sagex.api.metadata;

/**
 * Interface that defines a SageTV Property, All Updatable Metadata fields should accept ISageProperty
 * 
 * @author seans
 *
 */
public interface ISageProperty {
    public static enum Type {Int, Long, Float, String, Boolean, Date, DateTime, StringList} 
    public String key();
    public Type type();
}
