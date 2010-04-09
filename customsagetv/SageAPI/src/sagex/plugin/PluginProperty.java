package sagex.plugin;

public class PluginProperty {
    private String[] options;
    private String defaultValue;
    private int type;
    private String help;
    private String label;
    private String setting;
    private String optionSep;
    private String visibleOnSetting;

    public PluginProperty(int type, String setting, String defaultValue, String label, String help) {
        this(type, setting, defaultValue, label, help, null, null);
    }

    public PluginProperty(int type, String setting, String defaultValue, String label, String help, String[] options) {
        this(type, setting, defaultValue, label, help, options, ";");
    }
    
    public PluginProperty(int type, String setting, String defaultValue, String label, String help, String[] options, String optionSep) {
        super();
        this.type = type;
        this.setting = setting;
        this.defaultValue = defaultValue;
        this.label = label;
        this.help = help;
        this.options = options;
        this.optionSep = optionSep;
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

    public void setOptionSep(String optionSep) {
        this.optionSep = optionSep;
    }

    public String getOptionSep() {
        return optionSep;
    }

    public void setVisibleOnSetting(String visibleOnSetting) {
        this.visibleOnSetting = visibleOnSetting;
    }

    public String getVisibleOnSetting() {
        return visibleOnSetting;
    }

}
