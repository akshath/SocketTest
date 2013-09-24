package net.sf.sockettest.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.net.*;
import java.io.*;

import net.sf.sockettest.*;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketTestUdp extends JPanel /*JFrame*/ {
    private final String NEW_LINE = "\r\n";
    private ClassLoader cl = getClass().getClassLoader();
    public ImageIcon logo = new ImageIcon(
            cl.getResource("icons/logo.gif"));
    
    private JPanel northPanel;
    private JPanel serverPanel;
    
    private JPanel convPanel;
    
    private JPanel clientPanel;
    private JPanel buttonPanel;
    
    private JLabel ipLabel1 = new JLabel("IP Address");
    private JLabel portLabel1 = new JLabel("Port");
    private JLabel logoLabel = new JLabel("SocketTest v 3.0", logo,
            JLabel.CENTER);
    
    private JTextField ipField1 = new JTextField("0.0.0.0",20);
    private JTextField portField1 = new JTextField("21",5);
    private JButton portButton1 = new JButton("Port");
    private JButton connectButton = new JButton("Start Listening");
    
    private Border convBorder = BorderFactory.createTitledBorder(new EtchedBorder(),"Conversation");
    private JTextArea messagesField = new JTextArea();
    
    private JLabel ipLabel2 = new JLabel("IP Address");
    private JLabel portLabel2 = new JLabel("Port");
    private JTextField ipField2 = new JTextField("127.0.0.1");
    private JTextField portField2 = new JTextField("21",5);
    private JButton portButton2 = new JButton("Port");
    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();
    private JButton sendButton = new JButton("Send");
    
    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");
    
    private GridBagConstraints gbc = new GridBagConstraints();
    
    private DatagramSocket server,client;
    private DatagramPacket packet;
    private UdpServer udpServer;
    private DatagramPacket pack;
    private byte buffer[];
    
    protected final JFrame parent;
    
    public SocketTestUdp(final JFrame parent) {
        //Container cp = getContentPane();
        this.parent = parent;
        Container cp = this;
        
        northPanel = new JPanel();
        serverPanel = new JPanel();
        serverPanel.setLayout(new GridBagLayout());
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        serverPanel.add(ipLabel1, gbc);
        
        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener1 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portField1.requestFocus();
            }
        };
        ipField1.addActionListener(ipListener1);
        serverPanel.add(ipField1, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        serverPanel.add(portLabel1, gbc);
        
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener connectListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listen();
            }
        };
        portField1.addActionListener(connectListener);
        serverPanel.add(portField1, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        portButton1.setMnemonic('P');
        portButton1.setToolTipText("View Standard Ports");
        ActionListener portButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PortDialog dia = new PortDialog(parent, PortDialog.UDP);
                dia.show();
            }
        };
        portButton1.addActionListener(portButtonListener);
        serverPanel.add(portButton1, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        connectButton.addActionListener(connectListener);
        serverPanel.add(connectButton, gbc);
        
        serverPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),"Server"));
        northPanel.setLayout(new BorderLayout(10,0));
        northPanel.add(serverPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        northPanel.add(logoLabel,BorderLayout.EAST);
        northPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));
        
        convPanel = new JPanel();
        convPanel.setLayout(new BorderLayout(0,5));
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        convPanel.add(jsp);
        convPanel.setBorder(new CompoundBorder(
                BorderFactory.createEmptyBorder(5,10,5,10),
                convBorder));
        
        clientPanel = new JPanel();
        clientPanel.setLayout(new GridBagLayout());
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(ipLabel2, gbc);
        
        gbc.weightx = 1.0; //streach
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portField2.requestFocus();
            }
        };
        ipField2.addActionListener(ipListener2);
        clientPanel.add(ipField2, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(portLabel2, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        ActionListener portListener2 = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendField.requestFocus();
            }
        };
        portField2.addActionListener(portListener2);
        clientPanel.add(portField2, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 0;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        portButton2.setMnemonic('P');
        portButton2.setToolTipText("View Standard Ports");
        portButton2.addActionListener(portButtonListener);
        clientPanel.add(portButton2, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        clientPanel.add(sendLabel, gbc);
        
        gbc.weightx = 1.0;
        gbc.gridy = 1;
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String msg = sendField.getText();
                if(!msg.equals(""))
                    sendMessage(msg);
                else {
                    int value = JOptionPane.showConfirmDialog(
                            SocketTestUdp.this,  "Send Blank Line ?",
                            "Send Data To Server",
                            JOptionPane.YES_NO_OPTION);
                    if (value == JOptionPane.YES_OPTION)
                        sendMessage(msg);
                }
            }
        };
        sendField.addActionListener(sendListener);
        clientPanel.add(sendField, gbc);
        
        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        sendButton.addActionListener(sendListener);
        clientPanel.add(sendButton, gbc);
        clientPanel.setBorder(
                new CompoundBorder(
                BorderFactory.createEmptyBorder(0,0,0,3),
                BorderFactory.createTitledBorder("Client")));
        
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
        buttonPanel.add(clientPanel, gbc);
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        saveButton.setToolTipText("Save conversation with client to a file");
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
                int returnVal = chooser.showSaveDialog(SocketTestUdp.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    fileName=chooser.getSelectedFile().getAbsolutePath();
                    try {
                        Util.writeFile(fileName,text);
                    } catch (Exception ioe) {
                        JOptionPane.showMessageDialog(SocketTestUdp.this,
                                ""+ioe.getMessage(),
                                "Error saving to file..",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }//end of if
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with client");
        clearButton.setMnemonic('C');
        ActionListener clearListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messagesField.setText("");
            }
        };
        clearButton.addActionListener(clearListener);
        buttonPanel.add(clearButton, gbc);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
        
        cp.setLayout(new BorderLayout(10,0));
        cp.add(northPanel,BorderLayout.NORTH);
        cp.add(convPanel,BorderLayout.CENTER);
        cp.add(buttonPanel,BorderLayout.SOUTH);
        
    }
    
    /////////////////////
    //action & helper methods
    /////////////////////
    private void listen() {
        if(server!=null) {
            stop();
            return;
        }
        String ip=ipField1.getText();
        String port=portField1.getText();
        if(ip==null || ip.equals("")) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "No IP Address. Please enter IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField1.requestFocus();
            ipField1.selectAll();
            return;
        }
        if(port==null || port.equals("")) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "No Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField1.requestFocus();
            portField1.selectAll();
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(!Util.checkHost(ip)) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "Bad IP Address",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            ipField1.requestFocus();
            ipField1.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        int portNo = 0;
        try	{
            portNo=Integer.parseInt(port);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    "Bad Port number. Please enter Port number",
                    "Error connecting", JOptionPane.ERROR_MESSAGE);
            portField1.requestFocus();
            portField1.selectAll();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
		
		boolean multicase = false;
        try {
            InetAddress bindAddr=null;
			
			int i = ip.indexOf(".");
			int startBit = 0;
			if(i!=-1) {
				startBit = Integer.parseInt(ip.substring(0, i));
			}
			
			//if 224.x.x.x - 239.x.x.x - multi cast
			if(startBit>= 224 && startBit<=239) {
				MulticastSocket socket =  new MulticastSocket(portNo); // must bind receive side
				socket.joinGroup(InetAddress.getByName(ip));
				server = socket;
				multicase = true;
			} else {
				if(!ip.equals("0.0.0.0")) {
					bindAddr = InetAddress.getByName(ip);
					server = new DatagramSocket(portNo, bindAddr);
				} else {
					bindAddr = null;
					server = new DatagramSocket(portNo);
				}
			}			
            
            ipField1.setEditable(false);
            portField1.setEditable(false);
            
            connectButton.setText("Stop Listening");
            connectButton.setMnemonic('S');
            connectButton.setToolTipText("Stop Listening");
        } catch (Exception e) {
            error(e.getMessage(), "Starting Server at "+portNo);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if(multicase) {
			messagesField.setText("> MultiCase Server Joined on Port : "+portNo+NEW_LINE);
		} else {
			messagesField.setText("> Server Started on Port : "+portNo+NEW_LINE);
		}
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        udpServer = UdpServer.handle(this,server);
    }
    
    public synchronized void stop() {
        try {
            udpServer.setStop(true);
        } catch (Exception e) {}
        server=null;
        
        ipField1.setEditable(true);
        portField1.setEditable(true);
        
        connectButton.setText("Start Listening");
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        append("> Server stopped");
        append("> ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    public void sendMessage(String s) {
        try	{
            String ip=ipField2.getText();
            String port=portField2.getText();
            if(ip==null || ip.equals("")) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "No IP Address. Please enter IP Address",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                ipField2.requestFocus();
                ipField2.selectAll();
                return;
            }
            if(port==null || port.equals("")) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "No Port number. Please enter Port number",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                portField2.requestFocus();
                portField2.selectAll();
                return;
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if(!Util.checkHost(ip)) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "Bad IP Address",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                ipField2.requestFocus();
                ipField2.selectAll();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            int portNo = 0;
            try	{
                portNo=Integer.parseInt(port);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(SocketTestUdp.this,
                        "Bad Port number. Please enter Port number",
                        "Error connecting", JOptionPane.ERROR_MESSAGE);
                portField2.requestFocus();
                portField2.selectAll();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            
            InetAddress toAddr=null;
            toAddr = InetAddress.getByName(ip);
            
            if(client==null) {
                client = new DatagramSocket();
                UdpServer.handleClient(this,client); //listen for its response
            }
            buffer = s.getBytes();
            pack = new DatagramPacket(buffer, buffer.length, toAddr, portNo);
            append("S["+toAddr.getHostAddress()+":"+portNo+"]: "+s);
            client.send(pack);
            sendField.setText("");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(SocketTestUdp.this,
                    e.getMessage(),"Error Sending Message",
                    JOptionPane.ERROR_MESSAGE);
            client=null;
        }
    }
    
    public void error(String error) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestUdp.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void error(String error, String heading) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestUdp.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }
    
    public void append(String msg) {
        messagesField.append(msg+NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }
    
}
