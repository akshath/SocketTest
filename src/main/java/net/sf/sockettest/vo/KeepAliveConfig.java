package net.sf.sockettest.vo;

/**
 * Created by Srikanth on 2/23/2016.
 */
public class KeepAliveConfig {
    private String configName;
    private String message;
    private int delay;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return configName;
    }
}
