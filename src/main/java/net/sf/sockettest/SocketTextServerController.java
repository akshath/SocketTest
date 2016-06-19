package net.sf.sockettest;

import net.sf.sockettest.swing.SocketTestServerView;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketTextServerController {
    private Socket socket;
    private ServerSocket server;
    private SocketServer socketServer;
    private PrintWriter out;

    private SocketTestServerView view;

    public void setView(SocketTestServerView view) {
        this.view = view;
    }

    public void connect(String ip, String port) {
        if(server!=null) {
            stop();
            return;
        }
        if(ip ==null || ip.equals("")) {
            view.error("No IP Address. Please enter IP Address",
                    "Error connecting");
            view.focusOnIp();
            return;
        }
        if(port ==null || port.equals("")) {
            view.error("No Port number. Please enter Port number",
                    "Error connecting");
            view.focusOnPort();
            return;
        }
        view.startWaitInfo();
        if(!Util.checkHost(ip)) {
            view.error("Bad IP Address",
                    "Error connecting");
            view.focusOnIp();
            view.stopWaitInfo();
            return;
        }
        int portNo;
        try	{
            portNo=Integer.parseInt(port);
        } catch (Exception e) {
            view.error("Bad Port number. Please enter Port number",
                    "Error connecting");
            view.focusOnPort();
            view.stopWaitInfo();
            return;
        }
        try {
            InetAddress bindAddr;
            if(!ip.equals("0.0.0.0"))
                bindAddr = InetAddress.getByName(ip);
            else
                bindAddr = null;
            server = new ServerSocket(portNo,1,bindAddr);

            view.connected();
        } catch (Exception e) {
            view.error(e.getMessage(), "Starting Server at "+portNo);
            view.stopWaitInfo();
            return;
        }
        view.stopWaitInfo();
        view.appendMessage("> Server Started on Port: "+portNo);
        view.appendMessage("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        socketServer = SocketServer.handle(view, this, server);
    }

    public void sendMessage(String s) {
        view.startWaitInfo();
        try	{
            if(out==null) {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
            }
            if (view.isHexOutput()) {
                view.appendMessage("S: " + DatatypeConverter.printHexBinary(s.getBytes()));
            } else {
                view.appendMessage("S: " + s);
            }
            out.print(StringEscapeUtils.escapeJava(s));
            out.flush();
            view.resetSendField();
            view.stopWaitInfo();
        } catch (Exception e) {
            view.stopWaitInfo();
            view.error(e.getMessage(),"Error Sending Message");
            disconnect();
        }
    }

    public void buildMessage(String text) {
        String msg;
        if (view.isHexInput()) {
            try {
                msg = new String(DatatypeConverter.parseHexBinary(text));
            } catch (Exception ex) {
                view.error(ex.getMessage());
                return;
            }
        } else {
            msg = StringEscapeUtils.unescapeJava(text);
        }

        if(!msg.equals(""))
            sendMessage(msg);
        else {
            if (view.confirm("Send Data To Client", "Send Blank Line ?", JOptionPane.YES_OPTION)) {
                sendMessage(msg);
            }
        }
    }

    public void saveFile(String text) {
        if (text.equals("")) {
            view.error("Nothing to save", "Save to file");
            return;
        }
        String fileName = view.chooseFile();
        if (fileName != null) {
            try {
                Util.writeFile(fileName, text);
            } catch (Exception ioe) {
                view.error("" + ioe.getMessage(),
                        "Error saving to file..");
            }
        }
    }

    public synchronized void disconnect() {
        try {
            socketServer.setDesconnected(true);
        } catch (Exception e) {}
    }

    public synchronized void stop() {
        try {
            disconnect(); //close any client
            socketServer.setStop(true);
        } catch (Exception e) {}
        server=null;
        view.stopped();
        view.appendMessage("> Server stopped");
        view.appendMessage("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public synchronized void setClientSocket(Socket s) {

        if(s==null) {
            out=null;
            socket = null;
            view.showConnectionInfo("NONE");
            view.socketSet(false);
        } else {
            socket = s;
            view.showConnectionInfo(" "+socket.getInetAddress().getHostName()+
                    " ["+socket.getInetAddress().getHostAddress()+"] ");
            view.socketSet(true);
        }
    }
}
