package sagex.studio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Snippet {
    public class STV {
        private String body;
        private String name;
        private String insert;
        private boolean requireSelection = true;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getInsert() {
            return insert;
        }
        public void setInsert(String insert) {
            this.insert = insert;
        }
        public boolean isRequireSelection() {
            return requireSelection;
        }
        public void setRequireSelection(boolean requireSelection) {
            this.requireSelection = requireSelection;
        }
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
        
        public String toString() {
            return "STV; Name: " + getName() + "; Body: " + body;
        }
    }
    
    public class FormInput {
        private String type = "text";
        private String label = "";
        private String value = "";
        private String name = null;
        private boolean required = false;
        public boolean isRequired() {
            return required;
        }
        public void setRequired(boolean required) {
            this.required = required;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public boolean isCheckbox() {
            return "checkbox".equals(getType());
        }
        public boolean isText() {
            return getType()==null || "text".equals(getType());
        }
    }

    private File file = null;
    private long lastModified=0;
    
    private String script = "";
    private String label = "";
    private String icon = null;
    private String location = null;
    private String onBeforeInsert = null;
    private String onAfterInsert = null;
    private Map<String, STV> stvs = new HashMap<String, STV>();
    private List<FormInput> inputs = new ArrayList<FormInput>();
    
    public Snippet() {
    }
    
    public Snippet(File f) {
        this.file=f;
    }
    
    public void reloadSnippet() {
        lastModified=0;

        script="";
        label="";
        icon=null;
        onAfterInsert=null;
        onBeforeInsert=null;
        stvs.clear();
        inputs.clear();
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.parse(file);
            
            Element e = doc.getDocumentElement();
            label=getAttribute(e, "label",file.getName());
            icon = getAttribute(e, "icon", null);
            location = getAttribute(e,"location","both");
            onBeforeInsert = getAttribute(e, "onBeforeInsert", null);
            onAfterInsert = getAttribute(e, "onAfterInsert", null);
            
            NodeList nl = doc.getElementsByTagName("script");
            for (int i=0;i<nl.getLength();i++) {
                script += nl.item(i).getTextContent();
            }
            
            nl = doc.getElementsByTagName("input");
            for (int i=0;i<nl.getLength();i++) {
                e = (Element) nl.item(i);
                FormInput fi = new FormInput();
                fi.setLabel(getAttribute(e, "label", "Input " + i));
                fi.setType(getAttribute(e, "type", "text"));
                fi.setValue(getAttribute(e, "value", ""));
                fi.setName(getAttribute(e, "name", "input"+i));
                fi.setRequired(Boolean.parseBoolean(getAttribute(e, "required", "false")));
                inputs.add(fi);
            }
            
            nl = doc.getElementsByTagName("stv");
            for (int i=0;i<nl.getLength();i++) {
                e = (Element) nl.item(i);
                STV stv = new STV();
                stv.setBody(e.getTextContent());
                stv.setName(getAttribute(e, "name", "main"));
                stv.setInsert(getAttribute(e, "insert", "child"));
                stv.setRequireSelection(Boolean.parseBoolean(getAttribute(e, "requireSelection", "true")));
                stvs.put(stv.getName(), stv);
            }
            
            lastModified=file.lastModified();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAttribute(Element e, String attr, String defVal) {
        String v = e.getAttribute(attr);
        if (v==null || v.trim().length()==0) {
            v=defVal;
        }
        return v;
        
    }
    
    public boolean hasInputs() {
        return inputs.size()>0;
    }
    
    public boolean hasScript() {
        return script!=null && script.trim().length()>0;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOnBeforeInsert() {
        return onBeforeInsert;
    }

    public void setOnBeforeInsert(String onBeforeInsert) {
        this.onBeforeInsert = onBeforeInsert;
    }

    public String getOnAfterInsert() {
        return onAfterInsert;
    }

    public void setOnAfterInsert(String onAfterInsert) {
        this.onAfterInsert = onAfterInsert;
    }

    public List<FormInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<FormInput> inputs) {
        this.inputs = inputs;
    }

    public STV getSTV(String name) {
        return stvs.get(name);
    }
    
    public STV getSTV() {
        return getSTV("main");
    }

    public boolean isUsingFile(File f) {
        return (f.equals(file));
    }
    
    public boolean isModified() {
        return file.lastModified()>lastModified;
    }

    public boolean isTopMenu() {
        return getLocation()==null || getLocation().trim().length()==0 || "top".equals(getLocation()) || "both".equals(getLocation());
    }

    public boolean isContextMenu() {
        return getLocation()==null || getLocation().trim().length()==0 || "context".equals(getLocation()) || "both".equals(getLocation());
    }

    public FormInput createInput() {
        return new FormInput();
    }
}
