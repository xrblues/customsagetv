package org.jdna.sagetv.networkencoder.impl.dvb.linux;

/**
 * Represents a channel that can be tuned.
 */
public interface IDVBChannel {
    public static enum Field {NAME, TRANSPODER, POLARITY, SAT, SYMBOL_RATE, VPID, APID, SID, FEC};
    public String get(Field field);
    public void set(Field field, String val);
}
