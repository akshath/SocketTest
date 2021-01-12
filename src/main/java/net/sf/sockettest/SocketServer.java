package net.sf.sockettest;

import net.sf.sockettest.callbacks.OnMessageRecieve;
import net.sf.sockettest.mode.ApplicationMode;
import net.sf.sockettest.swing.SocketTestServer;
import net.sf.sockettest.vo.RuleEntity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketServer extends Thread {
    
    private static SocketServer socketServer = null;
    private ApplicationMode applicationMode;
    private Socket socket = null;
    private ServerSocket server = null;
    private SocketTestServer parent;
    private BufferedInputStream in;
    private boolean desonnected=false;
    private boolean stop = false;
    private OnMessageRecieve callback;
    
    //disconnect client
    public synchronized void setDesonnected(boolean cr) {
        if(socket!=null && cr==true) {
            try	{
                socket.close();
            } catch (Exception e) {
                System.err.println("Error closing clinet : setDesonnected : "+e);
            }
        }
        desonnected=cr;
        //parent.setClientSocket(null);
    }
    //stop server
    public synchronized void setStop(boolean cr) {
        stop=cr;
        if(server!=null && cr==true) {
            try	{
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : "+e);
            }
        }
    }
    
    private SocketServer(SocketTestServer parent, ServerSocket s) {
        super("SocketServer");
        applicationMode = ApplicationMode.getInstance();
        this.parent = parent;
        server=s;
        setStop(false);
        setDesonnected(false);
        start();
    }
    
    
    
    public static synchronized SocketServer handle(SocketTestServer parent,
            ServerSocket s) {
        if(socketServer==null)
            socketServer=new SocketServer(parent, s);
        else {
            if(socketServer.server!=null) {
                try	{
                    socketServer.setDesonnected(true);
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
//            new Thread(new ServerConnection(socket)).start();
            startServer();
            if(socket!=null) {
                try	{
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Error closing client socket : "+e);
                }
                socket=null;
                parent.setClientSocket(socket);
            }
        }//end of while
    }//end of run

    public void addMessageListener(OnMessageRecieve callback){
        this.callback = callback;
    }

    /*public class ServerConnection implements Runnable{
        private Socket socketSer;
        public ServerConnection(Socket socketSer){
            this.socketSer = socketSer;
        }
        public void run() {
            startServer();
        }*/

        private void startServer() {
            parent.setClientSocket(socket);
            InputStream is = null;
            parent.append("> New Client: "+socket.getInetAddress().getHostAddress());
            while(true) {
                try {
                    is = socket.getInputStream();
                    in = new BufferedInputStream(is);
                } catch (IOException e) {
                    parent.append("> Cound't open input stream on Clinet " + e.getMessage());
                    setDesonnected(true);
                    return;
                }

                String rec = null;
                while (true) {
                    rec = null;
                    try {
                        rec = SocketServer.readInputStream(in);//in.readLine();
                    } catch (Exception e) {
                        setDesonnected(true);
                        if (!desonnected) {
                            parent.error(e.getMessage(), "Lost Client conection");
                            parent.append("> Server lost Client conection.");
                        } else
                            parent.append("> Server closed Client conection.");
                        break;
                    }

                    if (rec != null) {
                        
                        parent.appendnoNewLine("Recieved:: "+rec+"\r\n");
                        RuleEntity entity = applicationMode.getDbStore().getMappedEntity(rec);
                        System.out.println(applicationMode);
                        if (entity != null) {
                            String response = null;
                            if (entity.isRegex()) {
                                response = rec.replaceAll(entity.getInRule(), entity.getOutRule());
                            } else {
                                response = entity.getOutRule();
                            }
                            //send message
                            if (callback != null) {
                                callback.onRecieveMsg(socket, response);
                            }
                        }

                    } else {
                        setDesonnected(true);
                        parent.append("> Client closed conection.");
                        break;
                    }
                } //end of while
            }
        } //end of startServer


//    }

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
        return data;
    }
}
