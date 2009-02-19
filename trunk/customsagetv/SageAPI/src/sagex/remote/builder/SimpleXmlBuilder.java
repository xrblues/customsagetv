package sagex.remote.builder;

public class SimpleXmlBuilder implements BuilderHandler {
    private StringBuilder sb = new StringBuilder();
    
    public SimpleXmlBuilder() {
    }

    public void handleField(String name, Object data) {
        sb.append("    ").append("<").append(name).append(">");
        if (data==null) {
            sb.append("");
        } else if (data.getClass().isPrimitive() || Number.class.isAssignableFrom(data.getClass()) || Boolean.class.isAssignableFrom(data.getClass())) {
            sb.append(data);
        } else {
            sb.append("<![CDATA[").append(data).append("]]>");
        }
        sb.append("</").append(name).append(">\n");
    }

    public void beginArray(String name, int size) {
        sb.append("<").append(name).append(" size=\"").append(size).append("\">");
    }

    public void endArray(String name) {
        sb.append("</").append(name).append(">");
    }

    public void beginObject(String name) {
        sb.append("<").append(name).append(">");
    }

    public void endObject(String name) {
        sb.append("</").append(name).append(">\n");
    }

    public void handleError(String msg, Exception e) throws Exception {
        throw new Exception(msg, e);
    }

    public String toString() {
        return sb.toString();
    }
}
