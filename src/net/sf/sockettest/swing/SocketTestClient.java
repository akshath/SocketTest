package net.sf.sockettest.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.security.SecureRandom;

import java.net.*;
import java.io.*;

import javax.net.*;
import javax.net.ssl.*;

import net.sf.sockettest.*;
/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketTestClient extends JPanel {
    
    private final String NEW_LINE = "\r\n";
    private ClassLoader cl = getClass().getClassLoader();
    public ImageIcon logo = new ImageIcon(
            cl.getResource("icons/logo.gif"));
    
    private JPanel topPanel;
    private JPanel toPanel;
    
    private JPanel centerPanel;
    private JPanel textPanel;
    private JPanel buttonPanel;
    private JPanel sendPanel;
    
    private JLabel ipLabel = new JLabel("IP Address");
    private JLabel portLabel = new JLabel("Port");
    private JLabel logoLabel = new JLabel("SocketTest v 3.0", logo,
            JLabel.CENTER);
    private JTextField ipField = new JTextField("127.0.0.1",20);
    private JTextField portField = new JTextField("21",10);
    private JButton portButton = new JButton("Port");
    private JButton connectButton = new JButton("Connect");
    
    private JLabel convLabel = new JLabel("Conversation with host");
    private Border connectedBorder = BorderFactory.createTitledBorder(new EtchedBorder(),"Connected To < NONE >");
    private JTextArea messagesField = new JTextArea();
    
    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();
    
    private JButton sendButton = new JButton("Send");
    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");
    
    private JCheckBox secureButton = new JCheckBox("Secure");
    private boolean isSecure = false;
    private GridBagConstraints gbc = new GridBagConstraints();
    
    private Socket socket;
    private PrintWriter out;
    private SocketClient socketClient;
    protected final JFrame parent;
    
    public SocketTestClient(final JFrame parent) {
        //Container cp = getContentPane();
        this.parent = parent;
        Container cp = this;
        
        topPanel = new JPanel();
        toPanel = new JPanel();
        toPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(ipLabel, gbc);
        
        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portField.requestFocus();
            }
        };
        ipField.addActionListener(ipListener);
        toPanel.add(ipField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        toPanel.add(portLabel, gbc);
        
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener connectListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        };
        portField.addActionListener(connectListener);
        toPanel.add(portField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        portButton.setMnemonic('P');
        portButton.setToolTipText("View Standard Ports");
        ActionListener portButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PortDialog dia = new PortDialog(parent, PortDialog.TCP);
                dia.show();
            }
        };
        portButton.addActionListener(portButtonListener);
        toPanel.add(portButton, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        connectButton.addActionListener(connectListener);
        toPanel.add(connectButton, gbc);
        
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        secureButton.setToolTipText("Set Has Secure");
        secureButton.addItemListener(
                new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                isSecure = !isSecure;
            }
        });
        toPanel.add(secureButton, gbc);
        
        
        toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Connect To"));
        topPanel.setLayout(new BorderLayout(10,0));
        topPanel.add(toPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        topPanel.add(logoLabel,BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        
        
        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0,5));
        textPanel.add(convLabel,BorderLayout.NORTH);
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        textPanel.add(jsp);
        textPanel.setBorder(BorderFactory.createEmptyBorder(3,3,0,3));
        
        sendPanel = new JPanel();
        sendPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        sendPanel.add(sendLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sendField.setEditable(false);
        sendPanel.add(sendField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        sendButton.setEnabled(false);
        sendButton.setToolTipText("Send text to host");
        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = sendField.getText();
                if(!msg.equals(""))
                    sendMessage(msg);
                else {
                    int value = JOptionPane.showConfirmDialog(
                            SocketTestClient.this,  "Send Blank Line ?",
                            "Send Data To Server",
                            JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION)
                        sendMessage(msg);
                }
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
        sendPanel.setBorder(
                new CompoundBorder(
                BorderFactory.createEmptyBorder(0,0,0,3),
                BorderFactory.createTitledBorder("Send")));
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        buttonPanel.add(sendPanel, gbc);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setToolTipText("Save conversation with host to a file");
        saveButton.setMnemonic('S');
        ActionListener saveListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = messagesField.getText();
                if(text.equals("")) {
                    error("Nothing to save","Save to file");
                    return;
                }
                String fileName="";
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                int returnVal = chooser.showSaveDialog(SocketTestClient.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName=chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Util.writeFile(fileName,text);
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(SocketTestClient.this,
                                ""+ioe.getMessage(),
                                "Error saving to file..",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with host");
        clearButton.setMnemonic('C');
        ActionListener clearListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messagesField.setText("");
            }
        };
        clearButton.addActionListener(clearListener);
        buttonPanel.add(clearButton, gbc);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3,0,0,3));
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(0,10));
        centerPanel.add(buttonPanel,BorderLayout.SOUTH);
        centerPanel.add(textPanel,BorderLayout.CENTER);
        
        CompoundBorder cb=new CompoundBorder(
                BorderFactory.createEmptyBorder(5,10,10,10),
                connectedBorder);
        centerPanel.setBorder(cb);
        
        cp.setLayout(new BorderLayout(10,0));
        cp.add(topPanel,BorderLayout.NORTH);
        cp.add(centerPanel,BorderLayout.CENTER);
    }
    
        /*
        public static void main(String args[]) {
                SocketTestClient client=new SocketTestClient();
                client.setTitle("SocketTest Client");
                //client.pack();
                client.setSize(500,400);
                Util.centerWindow(client);
                client.setDefaultCloseOperation(EXIT_ON_CLOSE);
                client.setIconImage(client.logo.getImage());
                client.setVisible(true);
        }
         */
    
    /////////////////
    //action methods
    //////////////////
    private void connect() {
        if(socket!=null) {
            disconnect();
            return;
        }
        String ip=ipField.getText();
        String port=portField.getText();
        if(ip==null || ip.equals("")) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            return;
        }
        if(port==null || port.equals("")) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField.requestFocus();
            ipField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        int portNo = 0;
        try	{
            portNo=Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        try {
            if(isSecure==false) {
                System.out.println("Connectig in normal mode : "+ip+":"+portNo);
                socket = new Socket(ip,portNo);
            } else {
                System.out.println("Connectig in secure mode : "+ip+":"+portNo);
                //SocketFactory factory = SSLSocketFactory.getDefault();
				
				TrustManager[] tm = new TrustManager[] { new MyTrustManager(SocketTestClient.this) }; 

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(new KeyManager[0], tm, new SecureRandom());

                SSLSocketFactory factory = context.getSocketFactory();
                socket = factory.createSocket(ip,portNo);
            }
            
            ipField.setEditable(false);
            portField.setEditable(false);
            connectButton.setText("Disconnect");
            connectButton.setMnemonic('D');
            connectButton.setToolTipText("Stop Connection");
            sendButton.setEnabled(true);
            sendField.setEditable(true);
        } catch (Exception e) {
			e.printStackTrace();
            error(e.getMessage(), "Opening connection");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        changeBorder(" "+socket.getInetAddress().getHostName()+
                " ["+socket.getInetAddress().getHostAddress()+"] ");
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        messagesField.setText("");
        socketClient=SocketClient.handle(this,socket);
        sendField.requestFocus();
    }
    
    public synchronized void disconnect() {
        try {
            socketClient.setDesonnected(true);
            socket.close();
        } catch (Exception e) {
            System.err.println("Error closing client : "+e);
        }
        socket=null;
        out=null;
        changeBorder(null);
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Connect");
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        sendButton.setEnabled(false);
        sendField.setEditable(false);
    }
    public void error(String error) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void error(String error, String heading) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }
    
    public void append(String msg) {
        messagesField.append(msg+NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }
    
    public void appendnoNewLine(String msg) {
        messagesField.append(msg);
        messagesField.setCaretPosition(messagesField.getText().length());
    }
    
    public void sendMessage(String s) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try	{
            if(out==null) {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())), true);
            }
            append("S: "+s);
            out.print(s+NEW_LINE);
            out.flush();
            sendField.setText("");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            JOptionPane.showMessageDialog(SocketTestClient.this,
                    e.getMessage(),"Error Sending Message",
                    JOptionPane.ERROR_MESSAGE);
            disconnect();
        }
    }
    
    private void changeBorder(String ip) {
        if(ip==null || ip.equals(""))
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected To < NONE >");
        else
            connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected To < "+ip+" >");
        CompoundBorder cb=new CompoundBorder(
                BorderFactory.createEmptyBorder(5,10,10,10),
                connectedBorder);
        centerPanel.setBorder(cb);
        invalidate();
        repaint();
    }
    
}
