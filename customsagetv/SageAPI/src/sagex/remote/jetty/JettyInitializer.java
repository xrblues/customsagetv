package sagex.remote.jetty;

import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.Context;

import sagex.plugin.impl.SagexConfiguration;
import sagex.remote.SagexServlet;

public class JettyInitializer {
	public static void initJetty(org.mortbay.jetty.servlet.Context ctx) {
		// <Set name="securityHandler">
		// <New class="org.mortbay.jetty.security.SecurityHandler">
		// <Set name="userRealm">
		// <New class="org.mortbay.jetty.security.HashUserRealm">
		// <Set name="name">SageTV Web Interface</Set>
		// <Set name="config"><SystemProperty name="jetty.home"
		// default="."/>/etc/realm.properties</Set>
		// </New>
		// </Set>
		// <Set name="checkWelcomeFiles">true</Set>
		// <Set name="constraintMappings">
		// <Array type="org.mortbay.jetty.security.ConstraintMapping">
		// <Item>
		// <New class="org.mortbay.jetty.security.ConstraintMapping">
		// <Set name="constraint">
		// <New class="org.mortbay.jetty.security.Constraint">
		// <Set name="name">BASIC</Set>
		// <Set name="roles">
		// <Array type="java.lang.String">
		// <Item>user</Item>
		// <Item>admin</Item>
		// <Item>moderator</Item>
		// </Array>
		// </Set>
		// <!-- Uncomment below to force all connections onto SSL port -->
		// <!-- <Set name="dataConstraint">2</Set> -->
		// <Set name="authenticate">true</Set>
		// </New>
		// </Set>
		// <Set name="pathSpec">/*</Set>
		// </New>
		// </Item>
		// </Array>
		// </Set>
		// </New>
		// </Set>

//		if (ctx == null) {
//			ctx = (Context) SagexServlet.getUserData().get("jetty_context");
//			if (ctx==null) {
//				System.out.println("SAGEX-API: No Jetty Context, cannot configure security.");
//				return;
//			}
//		}
		
		SagexServlet.getUserData().put("jetty_context", ctx);
		updateAuthentication();
		
//		try {
//			SagexConfiguration config = new SagexConfiguration();
//			if (config.getBoolean(SagexConfiguration.PROP_SECURE_HTTP, true)) {
//				System.out.println("SAGEX-API: Securing Sagex Apis");
//				HashUserRealm hr = new HashUserRealm();
//				hr.setName("Sagex Services");
//				hr.setConfig(System.getProperty("jetty.home", ".")
//						+ "/etc/realm.properties");
//				SecurityHandler sh = new SecurityHandler();
//				sh.setUserRealm(hr);
//				sh.setCheckWelcomeFiles(true);
//
//				ConstraintMapping map[] = new ConstraintMapping[1];
//				ConstraintMapping cm = new ConstraintMapping();
//				map[0] = cm;
//				Constraint c = new Constraint();
//				c.setName("BASIC");
//				c.setRoles(new String[] { "user", "admin", "moderator" });
//				c.setAuthenticate(true);
//				cm.setConstraint(c);
//				cm.setPathSpec("/api");
//				sh.setConstraintMappings(map);
//				ctx.setSecurityHandler(sh);
//			} else {
//				System.out.println("SAGEX-API: Security Disabled");
//				ctx.setSecurityHandler(null);
//			}
//		} catch (Exception e) {
//			System.out.println("SAGEX-API: Security Failed!");
//			e.printStackTrace();
//		}
	}
	
	public static void updateAuthentication() {
		SagexConfiguration config = new SagexConfiguration();
		Context ctx = (Context) SagexServlet.getUserData().get("jetty_context");
		if (ctx==null) {
			System.out.println("SAGEX-API: Can't configure authentication; No Jetty Context.");
			return;
		}
		SecurityHandler sh = ctx.getSecurityHandler();
		if (sh==null) {
			System.out.println("SAGEX-API: Can't configure authentication; No Security Handler.");
			return;
		}
		ConstraintMapping cmap[] = sh.getConstraintMappings();
		if (cmap==null) {
			System.out.println("SAGEX-API: Can't configure authentication; No Constraints.");
			return;
		}
		
		boolean auth = config.getBoolean(SagexConfiguration.PROP_SECURE_HTTP, true);
		for (ConstraintMapping c: cmap) {
			Constraint cs = c.getConstraint();
			if (cs!=null) {
				System.out.println("SAGEX-API: Updated Constrant Authentication: " + auth);
				cs.setAuthenticate(auth);
			}
		}
	}
}
