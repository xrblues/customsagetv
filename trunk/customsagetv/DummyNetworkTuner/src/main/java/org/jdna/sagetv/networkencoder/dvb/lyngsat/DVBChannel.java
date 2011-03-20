package org.jdna.sagetv.networkencoder.dvb.lyngsat;

import java.util.HashMap;
import java.util.Map;

import org.jdna.sagetv.networkencoder.impl.dvb.linux.IDVBChannel;

public class DVBChannel implements IDVBChannel {
    private Map<Field, String> fields = new HashMap<Field, String>();

    public DVBChannel() {
    }
    
    public DVBChannel(IDVBChannel chan) {
        for (Field f: Field.values()) {
            set(f, chan.get(f));
        }
    }

    @Override
    public String get(Field field) {
        return fields.get(field);
    }

    @Override
    public void set(Field field, String val) {
        fields.put(field, val);
    }
}
