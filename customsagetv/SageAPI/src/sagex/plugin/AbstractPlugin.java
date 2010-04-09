package sagex.plugin;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sagex.api.Configuration;
import sagex.util.ILog;
import sagex.util.LogProvider;
import sagex.util.TypesUtil;

/**
 * AbstractPlugin intends to make it even easier for a plugin developer to create Plugin Activator classes for SageTV
 * 
 * Typically a developer will subclass this Plugin class and call addProperty() for each managed property in their
 * plugin's constructor.  That will enable SageTV to be able to configure their plugin.
 * 
 * Developers can subscribe to events using the {@link SageEvent} annotation on a method.  You can name you methods
 * whatever you want, but they must accept either no arguments, or the standard sage event args, which is a string and 
 * a map.
 * 
 * Your events are automatically registered when you plugin starts and unregistered when your plugin stops.  For this 
 * reason, if you override the start() and stop() methods, you should call super.start() and supert.stop() accordingly.
 * 
 * @author seans
 */
public class AbstractPlugin implements SageTVPlugin {
    protected ILog                        log   = LogProvider.getLogger(this.getClass());
    protected Map<String, PluginProperty> props = new LinkedHashMap<String, PluginProperty>();

    protected SageTVPluginRegistry pluginRegistry;
    
    public AbstractPlugin(SageTVPluginRegistry registry) {
        this.pluginRegistry = registry;
    }

    protected void registerEvents() {
        for (Method m: this.getClass().getMethods()) {
            SageEvent evt = m.getAnnotation(SageEvent.class);
            if (evt!=null) {
                pluginRegistry.eventSubscribe(this, evt.value());
            }
        }
    }

    protected void unregisterEvents() {
        for (Method m: this.getClass().getMethods()) {
            SageEvent evt = m.getAnnotation(SageEvent.class);
            if (evt!=null) {
                pluginRegistry.eventUnsubscribe(this, evt.value());
            }
        }
    }
    
    protected void addProperty(PluginProperty prop) {
        props.put(prop.getSetting(), prop);
    }
    
    public PluginProperty addProperty(int type, String setting, String defaultValue, String label, String help) {
        return addProperty(type, setting, defaultValue, label, help, null, null);
    }

    public PluginProperty addProperty(int type, String setting, String defaultValue, String label, String help, String[] options) {
        return addProperty(type, setting, defaultValue, label, help, options, ";");
    }
    
    public PluginProperty addProperty(int type, String setting, String defaultValue, String label, String help, String[] options, String optionSep) {
        PluginProperty p = new PluginProperty(type, setting, defaultValue, label, help, options, optionSep);
        addProperty(p);
        return p;
    }
    
    public void destroy() {
    }

    protected PluginProperty getPluginPropertyForSetting(String setting) {
        PluginProperty f = props.get(setting);
        if (f != null) {
            return f;
        }
        log.warn("Missing PluginProperty: " + setting);
        return null;
    }

    public void start() {
        registerEvents();
    }

    public void stop() {
        unregisterEvents();
    }

    /**
     * To handle events, create methods in your class that accept these
     * parameters but are annotated using the {@link SageEvent} annotation.
     * 
     * A sage event method can be defined to accept no args, both args, or just the Map arg.
     */
    public void sageEvent(String eventName, Map eventVars) {
        boolean fired = false;
        for (Method m : this.getClass().getMethods()) {
            SageEvent e = m.getAnnotation(SageEvent.class);
            try {
                if (e != null && e.value().equals(eventName)) {
                    Class cls[] = m.getParameterTypes();
                    m.setAccessible(true);
                    if (cls.length == 0) {
                        m.invoke(this, (Object[])null);
                    } else {
                        if (cls.length==2) {
                            m.invoke(this, new Object[] {eventName, eventVars});
                        } else {
                            if (cls[0]==String.class) {
                                m.invoke(this, new Object[] {eventName});
                            } else {
                                m.invoke(this, new Object[] {eventVars});
                            }
                        }
                    }
                    fired=true;
                }
            } catch (Exception ex) {
                log.warn("Failed to dispatch event for: " + eventName + " to method " + m.getName()  + " in class " + this.getClass().getName(), ex);
            }
        }
        
        if (!fired) {
            log.warn("Failed to handled SageEvent: '" + eventName + "' in class: " + this.getClass().getName());
        }
    }

    
    /**
     * propertChanged is ONLY called when setConfigValue is called with a value that is different than the current value
     * or default.  ie, it's only called when a property value changes.
     * 
     * Note, subclasses should not override this method, but rather use the {@link ConfigValueChangeHandler} annotation
     * on methods that need to be notified about config value changes.
     * 
     * The {@link ConfigValueChangeHandler} method can accept either no parameters or just a single string parameter which
     * will be the config setting, not the value.
     * 
     * @param setting
     */
    protected void propertyChanged(String setting) {
        boolean fired = false;
        for (Method m : this.getClass().getMethods()) {
            ConfigValueChangeHandler e = m.getAnnotation(ConfigValueChangeHandler.class);
            try {
                if (e != null && e.value().equals(setting)) {
                    Class cls[] = m.getParameterTypes();
                    m.setAccessible(true);
                    if (cls.length == 0) {
                        m.invoke(this, (Object[])null);
                        fired=true;
                    } else {
                        if (cls.length==1) {
                            m.invoke(this, setting);
                            fired=true;
                        }
                    }
                }
            } catch (Exception ex) {
                log.warn("Failed to dispatch change event for: " + setting + " to method " + m.getName()  + " in class " + this.getClass().getName(), ex);
            }
        }
        
        if (!fired) {
            log.warn("Failed to handle ConfigValueChanged event: '" + setting + "' in class: " + this.getClass().getName());
        }        
    }
    
    public String getConfigHelpText(String setting) {
        PluginProperty c = getPluginPropertyForSetting(setting);
        if (c == null) return null;
        return c.getHelp();
    }

    public String getConfigLabel(String setting) {
        PluginProperty c = getPluginPropertyForSetting(setting);
        if (c == null) return null;
        return c.getLabel();
    }

    public String[] getConfigOptions(String setting) {
        PluginProperty c = getPluginPropertyForSetting(setting);
        if (c == null) return null;
        return c.getOptions();
    }

    /**
     * Will only return the 'visible' settings.
     */
    public String[] getConfigSettings() {
        List<String> settings = new ArrayList<String>();
        for (PluginProperty p : props.values()) {
            boolean visible=true;
            if (p.getVisibleOnSetting()!=null) {
                visible = getConfigBoolValue(p.getVisibleOnSetting());
            }
            if (visible) settings.add(p.getSetting());
        }
        return settings.toArray(new String[] {});
    }

    public int getConfigType(String setting) {
        PluginProperty c = getPluginPropertyForSetting(setting);
        if (c == null) return 0;
        return c.getType();
    }

    public String getConfigValue(String setting) {
        PluginProperty p = getPluginPropertyForSetting(setting);
        if (p != null) {
            return Configuration.GetProperty(setting, p.getDefaultValue());
        }
        return null;
    }

    public String[] getConfigValues(String setting) {
        PluginProperty p = getPluginPropertyForSetting(setting);
        String s = getConfigValue(setting);
        if (s == null) return null;
        return s.split("\\s*"+p.getOptionSep()+"\\s*");
    }

    public void resetConfig() {
        for (Map.Entry<String, PluginProperty> me : props.entrySet()) {
            setConfigValue(me.getKey(), me.getValue().getDefaultValue());
        }
    }

    public void setConfigValue(String setting, String value) {
        PluginProperty p = getPluginPropertyForSetting(setting);
        if (p == null) return;
        String val = getConfigValue(setting);
        if (val!=null && !val.equals(value)) {
            Configuration.SetProperty(setting, value);
            propertyChanged(setting);
        }
    }

    public void setConfigValues(String setting, String[] values) {
        PluginProperty p = getPluginPropertyForSetting(setting);
        if (values != null) {
            String v = "";
            for (int i = 0; i < values.length; i++) {
                if (i > 0) v += p.getOptionSep();
                v += values[i];
            }
            setConfigValue(setting, v);
        } else {
            setConfigValue(setting, null);
        }
    }
    
    public int getConfigIntValue(String setting) {
        return TypesUtil.toInt(getConfigValue(setting), 0);
    }
    
    public boolean getConfigBoolValue(String setting) {
        return TypesUtil.toBoolean(getConfigValue(setting), false);
    }
}
