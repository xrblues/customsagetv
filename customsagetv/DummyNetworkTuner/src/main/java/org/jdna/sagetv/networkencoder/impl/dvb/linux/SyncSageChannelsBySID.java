package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.io.File;

import sagex.api.ChannelAPI;
import sagex.api.Global;

public class SyncSageChannelsBySID {
    public static void main(String args[]) {
        String lineups[] = Global.GetAllLineups();
        
        
        try {
            Object[] chans = ChannelAPI.GetAllChannels();
            ChannelsConfChannelProvider prov = new ChannelsConfChannelProvider();
            prov.load(new File("channels.conf"));
            for (Object o : chans) {
                String sid = ChannelAPI.GetChannelNumber(o);
                boolean visble = false;
                try {
                    if (prov.getChannelBySID(sid)!=null) {
                        visble=true;
                    }
                } catch (Exception e) {
                    // ignore means no cofigured sid
                }
                setVisibleForLineups(o, sid, lineups, visble);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    private static void setVisibleForLineups(Object value, String sid, String[] lineups, boolean b) {
        for (String s : lineups) {
            System.out.println("Setting Channel Visible; Chan: " + sid + "; Visible: " + b);
            //ChannelAPI.SetChannelViewabilityForChannelOnLineup(value, s, b);
            ChannelAPI.SetChannelViewabilityForChannelNumberOnLineup(value, sid, s, b);
        }
    }
}
