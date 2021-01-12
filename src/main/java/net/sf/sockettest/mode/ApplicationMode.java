package net.sf.sockettest.mode;

import net.sf.sockettest.SqliteDBStore;

import java.util.Timer;

/**
 * Created by Srikanth on 2/19/2016.
 */
public class ApplicationMode {
    private AppMode mode;
    private ActiveEnv env;
    private SqliteDBStore dbStore;
    private static volatile ApplicationMode self = null;
    private Timer keepAliveTimer;
    private ApplicationMode(){
        mode = AppMode.RECIEVE_HEARTBEAT;
        dbStore = new SqliteDBStore("response_data");
//        keepAliveTimer = new Timer();
    }

    public ActiveEnv getEnv() {
        return env;
    }

    public synchronized void setEnv(ActiveEnv env) {
        this.env = env;
    }

    public void cancelTask(KeepAliveTimerTask task){
        task.cancel();
    }

    public void initTimer(){
        keepAliveTimer = new Timer();
    }

    public void cancelAllTasks(){
        keepAliveTimer.cancel();
    }
    public void scheduleTimerTask(KeepAliveTimerTask task, long delay, boolean reccurent){
        if(reccurent)
        keepAliveTimer.scheduleAtFixedRate(task, delay, delay);
        else
            keepAliveTimer.schedule(task, delay);
    }

    public static ApplicationMode getInstance(){
        if(self==null){
            synchronized (ApplicationMode.class){
                if(self==null){
                    self = new ApplicationMode();
                }
                return self;
            }
        }else return self;
    }

    public SqliteDBStore getDbStore() {
        return dbStore;
    }

    public AppMode getMode() {
        return mode;
    }

    public synchronized void setMode(AppMode mode) {
        this.mode = mode;
    }
}
