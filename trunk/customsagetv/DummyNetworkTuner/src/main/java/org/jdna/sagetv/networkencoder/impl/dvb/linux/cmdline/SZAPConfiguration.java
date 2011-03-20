package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="SZAP Configuration", path = "jdna/dvb/tuner", description = "Linux SZAP Tuner configuration")
public class SZAPConfiguration extends GroupProxy {
    @AField(label="mencoder path", description = "Path to the mencoder binary")
    private FieldProxy<String>  mencoderPath   = new FieldProxy<String>("/usr/bin/mencoder");

    @AField(label="szap path", description = "Path to the szap binary")
    private FieldProxy<String>  szapPath   = new FieldProxy<String>("/usr/bin/szap");

    @AField(label="dvbstream path", description = "Path to the dvbstream binary")
    private FieldProxy<String>  dvbStreamPath   = new FieldProxy<String>("./dvbstream");

    @AField(label="DVB Adapter", description = "DVB Adapter (numeric adapter #)")
    private FieldProxy<Integer>  dvbAdapter   = new FieldProxy<Integer>(1);

    @AField(label="LNB Settings", description = "szap lnb settings (low,high,switch or predefined values, such as DBS)")
    private FieldProxy<String>  lnb   = new FieldProxy<String>("11250,11250,12700");

    @AField(label="channels.conf file", description = "Full path to the channels.conf file")
    private FieldProxy<String>  channelsConf   = new FieldProxy<String>("channels.conf");

    @AField(label="DisEQc Port", description = "Port, 0 based.")
    private FieldProxy<Integer>  diseqc   = new FieldProxy<Integer>(0);

    public SZAPConfiguration() {
        super();
        init();
    }

    public String getSzapPath() {
        return szapPath.getString();
    }

    public void setDefaultEncoderClass(String defaultEncoderClass) {
        this.szapPath.set(defaultEncoderClass);
    }

    /**
     * @return the dvbAdapter
     */
    public int getDvbAdapter() {
        return dvbAdapter.getInt();
    }

    /**
     * @param dvbAdapter the dvbAdapter to set
     */
    public void setDvbAdapter(int dvbAdapter) {
        this.dvbAdapter.set(dvbAdapter);
    }

    /**
     * @return the lnb
     */
    public String getLnb() {
        return lnb.get();
    }

    /**
     * @param lnb the lnb to set
     */
    public void setLnb(String lnb) {
        this.lnb.set(lnb);
    }

    /**
     * @return the channelsConf
     */
    public String getChannelsConf() {
        return channelsConf.get();
    }

    /**
     * @param channelsConf the channelsConf to set
     */
    public void setChannelsConf(String channelsConf) {
        this.channelsConf.set(channelsConf);
    }

    /**
     * @return the diseqc
     */
    public int getDiseqc() {
        return diseqc.getInt();
    }

    /**
     * @param diseqc the diseqc to set
     */
    public void setDiseqc(int diseqc) {
        this.diseqc.set(diseqc);
    }

    /**
     * @return the mencoderPath
     */
    public String getMencoderPath() {
        return mencoderPath.get();
    }

    /**
     * @param mencoderPath the mencoderPath to set
     */
    public void setMencoderPath(String mencoderPath) {
        this.mencoderPath.set(mencoderPath);
    }

    public String getDvbStreamPath() {
        return dvbStreamPath.get();
    }

    /**
     * @param mencoderPath the mencoderPath to set
     */
    public void setDvbStreamPath(String path) {
        this.dvbStreamPath.set(path);
    }
}
