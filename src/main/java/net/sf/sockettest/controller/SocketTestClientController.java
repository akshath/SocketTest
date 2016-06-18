package net.sf.sockettest.controller;

import net.sf.sockettest.MyTrustManager;
import net.sf.sockettest.SocketClient;
import net.sf.sockettest.Util;
import net.sf.sockettest.swing.SocketTestClientView;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;

import static sun.management.Agent.error;

public class SocketTestClientController {
    private boolean secure;
    private Socket socket;
    private PrintWriter out;
    private SocketClient socketClient;
    private SocketTestClientView view;

    public SocketTestClientController() {
        secure = false;
    }

    public void setView(SocketTestClientView view) {
        this.view = view;
    }

    public void toggleSecure() {
        secure = !secure;
    }

    public void connect(String ip, String port) {
        if(socket!=null) {
            disconnect();
            return;
        }
        if(ip ==null || ip.equals("")) {
            error("No IP Address. Please enter IP Address",
                    "Error connecting");
            view.focusOnIp();
            return;
        }
        if(port ==null || port.equals("")) {
            error("No Port number. Please enter Port number",
                    "Error connecting");
            view.focusOnPort();
            return;
        }
        view.startWaitInfo();
        if(!Util.checkHost(ip)) {
            error("Bad IP Address",
                    "Error connecting");
            view.focusOnIp();
            view.stopWaitInfo();
            return;
        }
        int portNo;
        try {
            portNo=Integer.parseInt(port);
        } catch (Exception e) {
            error("Bad Port number. Please enter Port number",
                    "Error connecting");
            view.focusOnPort();
            view.stopWaitInfo();
            return;
        }
        try {
            if(!secure) {
                System.out.println("Connecting in normal mode : "+ ip +":"+portNo);
                socket = new Socket(ip,portNo);
            } else {
                System.out.println("Connecting in secure mode : "+ ip +":"+portNo);
                //SocketFactory factory = SSLSocketFactory.getDefault();

                TrustManager[] tm = new TrustManager[] { new MyTrustManager(view) };

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(new KeyManager[0], tm, new SecureRandom());

                SSLSocketFactory factory = context.getSocketFactory();
                socket = factory.createSocket(ip,portNo);
            }

            view.connected();
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage(), "Opening connection");
            view.stopWaitInfo();
            return;
        }
        view.showConectionInfo(" "+socket.getInetAddress().getHostName()+
                " ["+socket.getInetAddress().getHostAddress()+"] ");
        view.stopWaitInfo();
        view.clearMessages();
        socketClient = SocketClient.handle(view, this, socket);
        view.focusSendField();
    }

    public synchronized void disconnect() {
        try {
            socketClient.setDisconnected(true);
            socket.close();
        } catch (Exception e) {
            System.err.println("Error closing client : "+e);
        }
        socket=null;
        out=null;
        view.disconnected();
    }

    public void buildMessage(String text, boolean hexInput) {
        String msg;
        if (hexInput) {
            try {
                msg = new String(DatatypeConverter.parseHexBinary(text));
            } catch (Exception ex) {
                error(ex.getMessage());
                return;
            }
        } else {
            msg = StringEscapeUtils.unescapeJava(text);
        }
        if(!msg.equals(""))
            sendMessage(msg);
        else {
            if (view.confirm("Send Data To Server", "Send Blank Line ?", JOptionPane.YES_OPTION)) {
                sendMessage(msg);
            }
        }
    }

    public void sendMessage(String s) {
        view.startWaitInfo();
        try {
            if(out==null) {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
            }
            if (view.isHexOutput()) {
                view.appendMessage("S: " + DatatypeConverter.printHexBinary(s.getBytes()));
            } else {
                view.appendMessage("S: "+s);
            }
            out.print(StringEscapeUtils.escapeJava(s));
            out.flush();
            view.resetSend();
            view.stopWaitInfo();
        } catch (Exception e) {
            view.stopWaitInfo();
            error(e.getMessage(),"Error Sending Message");
            disconnect();
        }
    }

    public void saveMessages() {
        String text = view.getMessages();
        if(text.equals("")) {
            error("Nothing to save","Save to file");
            return;
        }
        String fileName = view.chooseFile();
        if (fileName != null) {
            view.saveText(text, fileName);
        }
    }
}
