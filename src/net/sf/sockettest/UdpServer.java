package net.sf.sockettest;

import java.net.*;
import java.io.*;
import net.sf.sockettest.swing.SocketTestUdp;
/**
 *
 * @author Akshathkumar Shetty
 */
public class UdpServer extends Thread {    
    public static int BUFFER_SIZE = 1024;
    
    private static UdpServer udpServer = null;
    //for listening for client responses
    private static UdpServer udpServer2 = null;
    
    
    private DatagramSocket server;
    private SocketTestUdp parent;
    private boolean stop = false;
    private byte buffer[] = new byte[BUFFER_SIZE];
    
    //stop server
    public synchronized void setStop(boolean cr) {
        stop=cr;
        if(server!=null && cr==true) {
			/*
			if(server instanceof MulticastSocket) {
				MulticastSocket ms = (MulticastSocket) server;
				ms.leaveGroup(?);
			}
			 */
            try	{
                server.close();
            } catch (Exception e) {
                System.err.println("Error closing server : setStop : "+e);
            }
        }
    }
    
    private UdpServer(SocketTestUdp parent, DatagramSocket s) {
        super("SocketUdp");
        this.parent = parent;
        server=s;
        setStop(false);
        start();
    }
    
    public static synchronized UdpServer handle(SocketTestUdp parent,
            DatagramSocket s) {
        if(udpServer==null)
            udpServer=new UdpServer(parent, s);
        else {
            if(udpServer.server!=null) {
                try	{
                    udpServer.setStop(true);
                    if(udpServer.server!=null)
                        udpServer.server.close();
                } catch (Exception e)	{
                    parent.error(e.getMessage());
                }
            }
            udpServer.server = null;
            udpServer=new UdpServer(parent,s);
        }
        return udpServer;
    }
    
    public static synchronized UdpServer handleClient(SocketTestUdp parent,
            DatagramSocket s) {
        if(udpServer2==null)
            udpServer2=new UdpServer(parent, s);
        else {
            if(udpServer2.server!=null) {
                try	{
                    udpServer2.setStop(true);
                    if(udpServer2.server!=null)
                        udpServer2.server.close();
                } catch (Exception e)	{
                    parent.error(e.getMessage());
                }
            }
            udpServer2.server = null;
            udpServer2=new UdpServer(parent,s);
        }
        return udpServer2;
    }
    
    public void run() {
        DatagramPacket pack = new DatagramPacket(buffer,buffer.length);
        while(!stop) {
            try	{
                server.receive(pack);
				if(udpServer!=null) {
					if(server == udpServer.server) {
						parent.append("R[" + pack.getAddress().getHostAddress()+":"
								+pack.getPort()+"]: " +
								new String(pack.getData(),0,pack.getLength()) );
					} else {
						parent.append("R: " +
								new String(pack.getData(),0,pack.getLength()) );
					}
				} else {
					parent.append("R: " +new String(pack.getData(),0,pack.getLength()) );
				}
            } catch (Exception e) {
                if(!stop) {
                    parent.error(e.getMessage(),"Error acception connection");
                    stop=true;
                }
                continue;
            }
        }//end of while
    }//end of run
    
}
