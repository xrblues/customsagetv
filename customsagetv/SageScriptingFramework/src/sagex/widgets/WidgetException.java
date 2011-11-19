package sagex.widgets;

/**
 * Exception for processing the Widget xml.
 * 
 * @author seans
 */
public class WidgetException extends Exception {
    private String file;
    
    public WidgetException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WidgetException(String msg, String file) {
        super(msg);
        this.file = file;
    }

    public WidgetException(String string) {
        super(string);
    }

    public String getFile() {
        return file;
    }
}
