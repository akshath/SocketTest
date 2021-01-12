package net.sf.sockettest.callbacks;

import java.net.Socket;

/**
 * Created by Srikanth on 2/20/2016.
 */
public interface OnMessageRecieve {
    void onRecieveMsg(Socket socket, String message);
}
