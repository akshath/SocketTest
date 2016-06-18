package net.sf.sockettest.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.*;

import net.sf.sockettest.*;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketTestServer extends JPanel implements SocketTestServerView {
    private final String NEW_LINE = "\r\n";
    private ClassLoader cl = getClass().getClassLoader();
    public ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));
    
    private JPanel topPanel;
    private JPanel toPanel;
    
    private JPanel centerPanel;
    private JPanel textPanel;
    private JPanel buttonPanel;
    private JPanel sendPanel;
    
    private JLabel ipLabel = new JLabel("IP Address");
    private JLabel portLabel = new JLabel("Port");
    private JLabel logoLabel = new JLabel("SocketTest v 3.0", logo, JLabel.CENTER);
    private JTextField ipField = new JTextField("0.0.0.0",20);
    private JTextField portField = new JTextField("21",10);
    private JButton portButton = new JButton("Port");
    private JButton connectButton = new JButton("Start Listening");

    private JLabel convLabel = new JLabel("Conversation with Client");
    private Border connectedBorder;
    private JTextArea messagesField = new JTextArea();
    
    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();
    
    private JButton sendButton = new JButton("Send");
    private JButton disconnectButton = new JButton("Disconnect");
    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");

    private JCheckBox hexInputCheckBox = new JCheckBox("Hex Input");
    private JCheckBox hexOutputCheckBox = new JCheckBox("Hex Output");

    private GridBagConstraints gbc = new GridBagConstraints();
    private JFrame parent;

    private SocketTextServerController controller;

    public SocketTestServer() {
        buildGUI();
        showConnectionInfo("NONE");
    }

    private void buildGUI() {
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
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ActionListener ipListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                focusOnPort();
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
                controller.connect(SocketTestServer.this.ipField.getText(), SocketTestServer.this.portField.getText());
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
                PortDialog dialog = new PortDialog(parent, PortDialog.TCP);
                dialog.setVisible(true);
            }
        };
        portButton.addActionListener(portButtonListener);
        toPanel.add(portButton, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
        connectButton.addActionListener(connectListener);
        toPanel.add(connectButton, gbc);

        toPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Listen On"));
        topPanel.setLayout(new BorderLayout(10, 0));
        topPanel.add(toPanel);
        logoLabel.setVerticalTextPosition(JLabel.BOTTOM);
        logoLabel.setHorizontalTextPosition(JLabel.CENTER);
        topPanel.add(logoLabel, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,5,10));


        textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 5));
        textPanel.add(convLabel, BorderLayout.NORTH);
        messagesField.setEditable(false);
        JScrollPane jsp = new JScrollPane(messagesField);
        textPanel.add(jsp);
        textPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
        textPanel.add(hexOutputCheckBox, BorderLayout.SOUTH);

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
        sendButton.setToolTipText("Send text to client");
        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.buildMessage(sendField.getText());
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
        ActionListener disconnectListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.disconnect();
            }
        };
        gbc.gridx = 3;
        disconnectButton.addActionListener(disconnectListener);
        disconnectButton.setEnabled(false);
        sendPanel.add(disconnectButton, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        sendPanel.add(hexInputCheckBox, gbc);

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
        saveButton.setToolTipText("Save conversation with client to a file");
        saveButton.setMnemonic('S');
        ActionListener saveListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.saveFile(messagesField.getText());
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

    @Override
    public String chooseFile() {
        String fileName = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int returnVal = chooser.showSaveDialog(SocketTestServer.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
        }
        return fileName;
    }

    @Override
    public boolean confirm(String title, String message, int option) {
        int value = JOptionPane.showConfirmDialog(
                SocketTestServer.this, message,
                title,
                JOptionPane.YES_NO_OPTION);
        return value == option;
    }

    @Override
    public boolean isHexInput() {
        return hexInputCheckBox.isSelected();
    }

    public SocketTestServer setParent(JFrame parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public void focusOnPort() {
        portField.requestFocus();
        portField.selectAll();
    }

    @Override
    public void focusOnIp() {
        ipField.requestFocus();
        ipField.selectAll();
    }

    @Override
    public void connected() {
        ipField.setEditable(false);
        portField.setEditable(false);
        connectButton.setText("Stop Listening");
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Stop Listening");
        //sendButton.setEnabled(true);
        //sendField.setEditable(true);
    }

    @Override
    public void stopWaitInfo() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void startWaitInfo() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void stopped() {
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Start Listening");
        connectButton.setMnemonic('S');
        connectButton.setToolTipText("Start Listening");
    }

    public void socketSet(boolean b) {
        sendButton.setEnabled(b);
        sendField.setEditable(b);
        disconnectButton.setEnabled(b);
    }

    public void error(String error) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestServer.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void error(String error, String heading) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestServer.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void appendMessage(String msg) {
        messagesField.append(msg+NEW_LINE);
        messagesField.setCaretPosition(messagesField.getText().length());
    }

    public void resetSendField() {
        sendField.setText("");
    }

    public boolean isHexOutput() {
        return hexOutputCheckBox.isSelected();
    }

    public void showConnectionInfo(String ip) {
        connectedBorder = BorderFactory.createTitledBorder(
                    new EtchedBorder(), "Connected Client : < "+ip+" >");
        CompoundBorder cb=new CompoundBorder(
                BorderFactory.createEmptyBorder(5,10,10,10),
                connectedBorder);
        centerPanel.setBorder(cb);
        invalidate();
        repaint();
    }

    public void setController(SocketTextServerController controller) {
        this.controller = controller;
        controller.setView(this);
    }
}
