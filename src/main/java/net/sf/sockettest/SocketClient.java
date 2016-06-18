package net.sf.sockettest;

import java.net.*;
import java.io.*;
import net.sf.sockettest.swing.SocketTestClient;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketClient extends Thread {
    
    private static SocketClient socketClient=null;
    private Socket socket=null;
    private SocketTestClient parent;
    private BufferedInputStream in;
    private boolean disconnected =false;
    
    public synchronized void setDisconnected(boolean cr) {
        disconnected =cr;
    }
    
    private SocketClient(SocketTestClient parent, Socket s) {
        super("SocketClient");
        this.parent = parent;
        socket=s;
        setDisconnected(false);
        start();
    }
    
    public static synchronized SocketClient handle(SocketTestClient parent, Socket s) {
        if(socketClient==null)
            socketClient=new SocketClient(parent, s);
        else {
            if(socketClient.socket!=null) {
                try	{
                    socketClient.socket.close();
                } catch (Exception e)	{
                    parent.error(e.getMessage());
                }
            }
            socketClient.socket=null;
            socketClient=new SocketClient(parent,s);
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
            parent.error("Could not open socket : "+e.getMessage());
            parent.disconnect();
            return;
        }
        
        while(!disconnected) {
            try {
                String got = readInputStream(in);
                if(got==null) {
                    parent.disconnect();
                    break;
                }
                if(parent.isHexOutput()) {
                    got = DatatypeConverter.printHexBinary(got.getBytes());
                }
                parent.append(got);
            } catch(IOException e) {
                if(!disconnected) {
                    parent.error(e.getMessage(),"Connection lost");
                    parent.disconnect();
                }
                break;
            }
        }//end of while
        try	{
            is.close();
            in.close();
            //socket.close();
        } catch (Exception err) {}
        socket=null;
    }//end of run
    
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
