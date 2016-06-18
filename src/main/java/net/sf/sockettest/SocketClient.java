package net.sf.sockettest;

import java.net.*;
import java.io.*;

import net.sf.sockettest.controller.SocketTestClientController;
import net.sf.sockettest.swing.SocketTestClientView;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketClient extends Thread {
    
    private static SocketClient socketClient=null;
    private SocketTestClientController controller;
    private Socket socket=null;
    private SocketTestClientView view;
    private BufferedInputStream in;
    private boolean disconnected =false;
    
    public synchronized void setDisconnected(boolean cr) {
        disconnected =cr;
    }
    
    private SocketClient(SocketTestClientView view, SocketTestClientController controller, Socket s) {
        super("SocketClient");
        this.view = view;
        this.controller = controller;
        socket=s;
        setDisconnected(false);
        start();
    }
    
    public static synchronized SocketClient handle(SocketTestClientView view, SocketTestClientController controller, Socket s) {
        if(socketClient==null)
            socketClient=new SocketClient(view, controller, s);
        else {
            if(socketClient.socket!=null) {
                try	{
                    socketClient.socket.close();
                } catch (Exception e)	{
                    view.error(e.getMessage());
                }
            }
            socketClient.socket=null;
            socketClient=new SocketClient(view, controller, s);
        }
        return socketClient;
    }
    
    public void run() {
        InputStream is = null;
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch(IOException e) {
            try {
                socket.close();
            } catch(IOException e2) {
                System.err.println("Socket not closed :"+e2);
            }
            view.error("Could not open socket : "+e.getMessage());
            controller.disconnect();
            return;
        }
        
        while(!disconnected) {
            try {
                String got = readInputStream(in);
                if(got==null) {
                    controller.disconnect();
                    break;
                }
                if(view.isHexOutput()) {
                    got = DatatypeConverter.printHexBinary(got.getBytes());
                }
                view.appendMessage(got);
            } catch(IOException e) {
                if(!disconnected) {
                    view.error(e.getMessage(),"Connection lost");
                    controller.disconnect();
                }
                break;
            }
        }
        try	{
            is.close();
            in.close();
        } catch (Exception err) {}
        socket=null;
    }
    
    private static String readInputStream(BufferedInputStream _in) throws IOException {
        String data = "";
        int s = _in.read();
        if(s==-1)
            return null;
        data += ""+(char)s;
        int len = _in.available();
        System.out.println("Len got : "+len);
        if(len > 0) {
            byte[] byteData = new byte[len];
            _in.read(byteData);
            data += new String(byteData);
        }
        return StringEscapeUtils.unescapeJava(data);
    }
}
