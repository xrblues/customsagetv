package sagex.plugin;

public class PluginProperty {
    private String[] options;
    private String defaultValue;
    private int type;
    private String help;
    private String label;
    private String setting;

    public PluginProperty(int type, String setting, String defaultValue, String label, String help, String[] options) {
        super();
        this.type = type;
        this.setting = setting;
        this.defaultValue = defaultValue;
        this.label = label;
        this.help = help;
        this.options = options;
    }

    public String getSetting() {
        return setting;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getHelp() {
        return help;
    }
    
    public int getType() {
        return type;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public String[] getOptions() {
        return options;
    }
}
