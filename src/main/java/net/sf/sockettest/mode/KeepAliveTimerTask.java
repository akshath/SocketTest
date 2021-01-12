package net.sf.sockettest.mode;

import net.sf.sockettest.callbacks.TimerReady;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TimerTask;

/**
 * Created by Srikanth on 2/24/2016.
 */
public class KeepAliveTimerTask extends TimerTask {
    private String message;
    private TimerReady callback;
    public KeepAliveTimerTask(TimerReady ready){
        callback = ready;
    }
    @Override
    public void run() {
        callback.onTimerReady();
    }


}
