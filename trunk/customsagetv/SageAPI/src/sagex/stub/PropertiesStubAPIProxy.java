package sagex.stub;

import java.util.Properties;

public class PropertiesStubAPIProxy implements StubAPIProxy {
    private Properties props = new Properties();
    
    public Object call(String cmd, Object[] args) {
        if ("GetProperty".equals(cmd) || "GetServerProperty".equals(cmd)) {
            String prop = props.getProperty((String)args[0], (String)(args.length>1 ? args[1] : null));
            if (prop!=null) {
                props.setProperty((String)args[0], prop);
            }
            System.out.printf("GetProperty: %s; Value: %s\n", args[0], prop);
            return prop;
        }
        
        if ("SetProperty".equals(cmd) || "SetServerProperty".equals(cmd)) {
            props.setProperty((String)args[0], (String)args[1]);
            System.out.printf("SetProperty: %s; Value: %s\n", args[0], args[1]);
            return null;
        }
        
        if ("AddGlobalContext".equals(cmd) || "AddStaticContext".equals(cmd)) {
            props.setProperty((String)args[0], (String)args[1]);
            System.out.printf("AddXXXContext: %s; Value: %s\n", args[0], args[1]);
            return null;
        }
        
        System.out.println("PropertiesStubAPIProxy: Not Handled: " + cmd);
        return null;
    }
    
    public Properties getProperties() {
        return props;
    }
    
    public void attach(StubSageAPI api) {
        api.addProxy("GetProperty", this);
        api.addProxy("GetServerProperty", this);
        api.addProxy("SetProperty", this);
        api.addProxy("SetServerProperty", this);
        api.addProxy("AddStaticContext", this);
        api.addProxy("AddGlobalContext", this);
    }
}
