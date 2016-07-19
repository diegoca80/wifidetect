package com.example.cardosod.wifidetect;

/**
 * Created by cardosod on 23/03/2016.
 */
public class WifiInfo {
    private String bssid;
    private String ssid;
    private int signal;
    private int frequency;
    private long timestamp;

    public WifiInfo(String bssid, String ssid, int signal,long timestamp) {
        this.bssid = bssid;
        this.ssid = ssid;
        this.signal = signal;
        this.timestamp = timestamp;
    }

    public WifiInfo() {

    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public long getTimestamp(){return timestamp;}

    public void setTimestamp(long timestamp){this.timestamp=timestamp;}

    public String toPipe() {
        String data = "";
        data += this.bssid;
        data += ";" + this.ssid;
        data += ";" + this.signal;
        data += ";" + this.frequency;
        data += ";" + this.timestamp;
        return data;
    }
}
