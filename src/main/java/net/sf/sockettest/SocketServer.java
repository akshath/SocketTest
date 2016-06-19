package net.sf.sockettest;

import java.net.*;
import java.io.*;
import net.sf.sockettest.swing.SocketTestServer;
import net.sf.sockettest.swing.SocketTestServerView;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketServer extends Thread {
    
    private static SocketServer socketServer = null;
    private Socket socket = null;
    private final SocketTestServerView view;
    private final SocketTextServerController controller;
    private ServerSocket server = null;
    private BufferedInputStream in;
    private boolean desonnected=false;
    private boolean stop = false;

    public synchronized void setDesconnected(boolean cr) {
        if(socket!=null && cr) {
            try	{
                socket.close();
            } catch (Exception e) {
                System.err.println("Error closing clinet : setDesconnected : "+e);
            }
        }
        desonnected=cr;
        //parent.setClientSocket(null);
    }

    public synchronized void setStop(boolean cr) {
        stop=cr;
        if(server!=null && cr) {
            try	{
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : "+e);
            }
        }
    }
    
    private SocketServer(SocketTestServerView view, SocketTextServerController controller, ServerSocket s) {
        super("SocketServer");
        this.view = view;
        this.controller = controller;
        server=s;
        setStop(false);
        setDesconnected(false);
        start();
    }
    
    
    
    public static synchronized SocketServer handle(SocketTestServerView view, SocketTextServerController controller,
            ServerSocket s) {
        if(socketServer==null)
            socketServer=new SocketServer(view, controller, s);
        else {
            if(socketServer.server!=null) {
                try	{
                    socketServer.setDesconnected(true);
                    socketServer.setStop(true);
                    if(socketServer.socket!=null)
                        socketServer.socket.close();
                    if(socketServer.server!=null)
                        socketServer.server.close();
                } catch (Exception e)	{
                    view.error(e.getMessage());
                }
            }
            socketServer.server = null;
            socketServer.socket = null;
            socketServer=new SocketServer(view, controller, s);
        }
        return socketServer;
    }
    
    public void run() {
        while(!stop) {
            try	{
                socket = server.accept();
            } catch (Exception e) {
                if(!stop) {
                    view.error(e.getMessage(),"Error acception connection");
                    stop=true;
                }
                continue;
            }
            startServer();
            if(socket!=null) {
                try	{
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Erro closing client socket : "+e);
                }
                socket=null;
                controller.setClientSocket(socket);
            }
        }
    }
    
    private void startServer() {
        controller.setClientSocket(socket);
        InputStream is = null;
        view.appendMessage("> New Client: "+socket.getInetAddress().getHostAddress());
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch(IOException e) {
            view.appendMessage("> Cound't open input stream on Clinet "+e.getMessage());
            setDesconnected(true);
            return;
        }
        
        String rec;
        while(true) {
            try {
                rec = readInputStream(in);
            } catch (Exception e) {
                setDesconnected(true);
                if(!desonnected) {
                    view.error(e.getMessage(),"Lost Client conection");
                    view.appendMessage("> Server lost Client conection.");
                } else
                    view.appendMessage("> Server closed Client conection.");
                break;
            }
            
            if (rec != null) {
                if(view.isHexOutput()) {
                    rec = DatatypeConverter.printHexBinary(rec.getBytes());
                }
                view.appendMessage(rec);
            } else {
                setDesconnected(true);
                view.appendMessage("> Client closed conection.");
                break;
            }
        }
    }
    
    private static String readInputStream(BufferedInputStream in)
    throws IOException {
        String data = "";
        int s = in.read();
        if(s == -1) {
            return null;
        }
        data += ""+(char)s;
        int len = in.available();
        System.out.println("Len got : "+len);
        if(len > 0) {
            byte[] byteData = new byte[len];
            in.read(byteData);
            data += new String(byteData);
        }
        return StringEscapeUtils.unescapeJava(data);
    }
}
