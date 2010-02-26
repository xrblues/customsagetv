package bmt;

import java.util.Calendar;

import org.jdna.url.CachedUrlCleanupTask;

import sagex.phoenix.Phoenix;
import sagex.phoenix.plugin.IPlugin;
import sagex.phoenix.plugin.IPluginActivator;
import sagex.phoenix.plugin.Plugin.State;

public class BMTActivator implements IPluginActivator {
    public BMTActivator() {
    }

    public void pluginChanged(IPlugin plugin, State state) {
        if(state == State.STARTING) {
            // initialize the BMT state
            Phoenix.getInstance().getTaskManager().scheduleTask(
                    CachedUrlCleanupTask.TaskID, 
                    new CachedUrlCleanupTask(), 
                    Calendar.getInstance().getTime(), 24*60*60);
        }
    }
}
