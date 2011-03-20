package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.util.HashMap;
import java.util.Map;


/**
 * IDVBChannel implementation that gets it's channel information from a line
 * from the channels.conf file.
 * 
 * format
 * LONDv:12224:h:S91.0W:20000:4130:4132:0:1:71:0:0:0
 * 
 * @author seans
 *
 */
public class ChannelsConfDVBChannel implements IDVBChannel {
    private Map<Field, String> values = new HashMap<Field, String>();
    private String channelString = null;

    public ChannelsConfDVBChannel(String line) {
    	this.channelString = line;
    	String flds[] = line.split("\\:");
    	values.put(Field.NAME, flds[0]);
        values.put(Field.TRANSPODER, flds[1]);
        values.put(Field.POLARITY, flds[2]);
        values.put(Field.SAT, flds[3]);
        values.put(Field.SYMBOL_RATE, flds[4]);
        values.put(Field.VPID, flds[5]);
        values.put(Field.APID, flds[6]);
        values.put(Field.SID, flds[7]);
    }

    @Override
    public String get(Field field) {
        return values.get(field);
    }

    @Override
    public void set(Field field, String val) {
        values.put(field, val);
    }
    
    public String toString() {
        return channelString;
    }
}
