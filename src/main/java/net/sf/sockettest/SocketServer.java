package net.sf.sockettest;

import java.net.*;
import java.io.*;
import net.sf.sockettest.swing.SocketTestServer;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketServer extends Thread {
    
    private static SocketServer socketServer = null;
    private Socket socket = null;
    private ServerSocket server = null;
    private SocketTestServer parent;
    private BufferedInputStream in;
    private boolean desonnected=false;
    private boolean stop = false;
    
    //disconnect client
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
    //stop server
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
    
    private SocketServer(SocketTestServer parent, ServerSocket s) {
        super("SocketServer");
        this.parent = parent;
        server=s;
        setStop(false);
        setDesconnected(false);
        start();
    }
    
    
    
    public static synchronized SocketServer handle(SocketTestServer parent,
            ServerSocket s) {
        if(socketServer==null)
            socketServer=new SocketServer(parent, s);
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
                    parent.error(e.getMessage());
                }
            }
            socketServer.server = null;
            socketServer.socket = null;
            socketServer=new SocketServer(parent,s);
        }
        return socketServer;
    }
    
    public void run() {
        while(!stop) {
            try	{
                socket = server.accept();
            } catch (Exception e) {
                if(!stop) {
                    parent.error(e.getMessage(),"Error acception connection");
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
                parent.setClientSocket(socket);
            }
        }//end of while
    }//end of run
    
    private void startServer() {
        parent.setClientSocket(socket);
        InputStream is = null;
        parent.append("> New Client: "+socket.getInetAddress().getHostAddress());
        try {
            is = socket.getInputStream();
            in = new BufferedInputStream(is);
        } catch(IOException e) {
            parent.append("> Cound't open input stream on Clinet "+e.getMessage());
            setDesconnected(true);
            return;
        }
        
        String rec=null;
        while(true) {
            rec=null;
            try	{
                rec = readInputStream(in);//in.readLine();
            } catch (Exception e) {
                setDesconnected(true);
                if(!desonnected) {
                    parent.error(e.getMessage(),"Lost Client conection");
                    parent.append("> Server lost Client conection.");
                } else
                    parent.append("> Server closed Client conection.");
                break;
            }
            
            if (rec != null) {
                if(parent.isHexOutput()) {
                    rec = DatatypeConverter.printHexBinary(rec.getBytes());
                }
                parent.appendnoNewLine(rec);
            } else {
                setDesconnected(true);
                parent.append("> Client closed conection.");
                break;
            }
        } //end of while
    } //end of startServer
    
    private static String readInputStream(BufferedInputStream _in)
    throws IOException {
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
