package net.sf.sockettest.swing;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.io.*;

import net.sf.sockettest.*;
import net.sf.sockettest.controller.SocketTestClientController;

/**
 *
 * @author Akshathkumar Shetty
 */
public class SocketTestClient extends JPanel implements SocketTestClientView {
    
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
    private JLabel logoLabel = new JLabel("SocketTest v 3.0", logo,
            JLabel.CENTER);
    private JTextField ipField = new JTextField("127.0.0.1",20);
    private JTextField portField = new JTextField("21",10);
    private JButton portButton = new JButton("Port");
    private JButton connectButton = new JButton("Connect");
    
    private JLabel convLabel = new JLabel("Conversation with host");
    private Border connectedBorder;
    private JTextArea messagesField = new JTextArea();
    
    private JLabel sendLabel = new JLabel("Message");
    private JTextField sendField = new JTextField();
    
    private JButton sendButton = new JButton("Send");
    private JButton saveButton = new JButton("Save");
    private JButton clearButton = new JButton("Clear");
    
    private JCheckBox secureButton = new JCheckBox("Secure");
    private JCheckBox hexInputCheckBox = new JCheckBox("Hex Input");
    private JCheckBox hexOutputCheckBox = new JCheckBox("Hex Output");
    private GridBagConstraints gbc = new GridBagConstraints();
    private JFrame parent;
    private SocketTestClientController controller;

    public SocketTestClient() {
        buildGUI();
        showConectionInfo("NONE");
    }

    public void setController(SocketTestClientController controller) {
        this.controller = controller;
        controller.setView(this);
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
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ipField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                portField.requestFocus();
            }
        });
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
        ActionListener connectActionListener =  new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                controller.connect(ipField.getText(), portField.getText());
            }
        };
        portField.addActionListener(connectActionListener);
        toPanel.add(portField, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        portButton.setMnemonic('P');
        portButton.setToolTipText("View Standard Ports");
        portButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PortDialog dialog = new PortDialog(parent, PortDialog.TCP);
                dialog.setVisible(true);
            }
        });
        toPanel.add(portButton, gbc);

        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        connectButton.addActionListener(connectActionListener);
        toPanel.add(connectButton, gbc);


        gbc.weightx = 0.0;
        gbc.gridy = 1;
        gbc.gridx = 4;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        secureButton.setToolTipText("Set Has Secure");
        secureButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                controller.toggleSecure();
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
        sendButton.setToolTipText("Send text to host");
        ActionListener sendListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String text = sendField.getText();
                boolean hexInput = hexInputCheckBox.isSelected();
                controller.buildMessage(text, hexInput);
            }
        };
        sendButton.addActionListener(sendListener);
        sendField.addActionListener(sendListener);
        sendPanel.add(sendButton, gbc);
        sendPanel.setBorder(
                new CompoundBorder(
                BorderFactory.createEmptyBorder(0,0,0,3),
                BorderFactory.createTitledBorder("Send")));

        gbc.gridx = 1;
        gbc.gridy = 1;
        sendPanel.add(hexInputCheckBox, gbc);
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
                controller.saveMessages();
            }
        };
        saveButton.addActionListener(saveListener);
        buttonPanel.add(saveButton, gbc);
        gbc.gridy = 1;
        clearButton.setToolTipText("Clear conversation with host");
        clearButton.setMnemonic('C');
        ActionListener clearListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearMessages();
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

    public void clearMessages() {
        messagesField.setText("");
    }

    public String chooseFile() {
        String fileName = null;
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        int returnVal = chooser.showSaveDialog(SocketTestClient.this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            fileName = chooser.getSelectedFile().getAbsolutePath();
        }
        return fileName;
    }

    public void saveText(String text, String fileName) {
        try {
            Util.writeFile(fileName, text);
        } catch (Exception ioe) {
            error(""+ioe.getMessage(), "Error saving to file..");
        }
    }

    public String getMessages() {
        return messagesField.getText();
    }

    public SocketTestClient setParent(JFrame parent) {
        this.parent = parent;
        return this;
    }

    public boolean confirm(String title, String message, int option) {
        return JOptionPane.showConfirmDialog(this, message,
                title,
                JOptionPane.YES_NO_OPTION) == option;
    }

    public void focusSendField() {
        sendField.requestFocus();
    }

    public void connected() {
        ipField.setEditable(false);
        portField.setEditable(false);
        connectButton.setText("Disconnect");
        connectButton.setMnemonic('D');
        connectButton.setToolTipText("Stop Connection");
        sendButton.setEnabled(true);
        sendField.setEditable(true);
    }

    public void focusOnPort() {
        portField.requestFocus();
        portField.selectAll();
    }

    public void focusOnIp() {
        ipField.requestFocus();
        ipField.selectAll();
    }

    public void startWaitInfo() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void error(String error) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    @Override
    public void error(String error, String heading) {
        if(error==null || error.equals(""))
            return;
        JOptionPane.showMessageDialog(SocketTestClient.this,
                error, heading, JOptionPane.ERROR_MESSAGE);
    }
    
    public void append(String msg) {
        messagesField.append(msg+NEW_LINE);
        messagesField.setCaretPosition(getMessages().length());
    }

    public void stopWaitInfo() {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void showConectionInfo(String ip) {
        connectedBorder = BorderFactory.createTitledBorder(
                new EtchedBorder(), "Connected To < "+ip+" >");
        CompoundBorder cb=new CompoundBorder(
                BorderFactory.createEmptyBorder(5,10,10,10),
                connectedBorder);
        centerPanel.setBorder(cb);
        invalidate();
        repaint();
    }

    public void disconnected() {
        showConectionInfo("NONE");
        ipField.setEditable(true);
        portField.setEditable(true);
        connectButton.setText("Connect");
        connectButton.setMnemonic('C');
        connectButton.setToolTipText("Start Connection");
        sendButton.setEnabled(false);
        sendField.setEditable(false);
    }

    public boolean isHexOutput() {
        return hexOutputCheckBox.isSelected();
    }

    public void appendMessage(String message) {
        append(message);
    }

    public void resetSend() {
        sendField.setText("");
    }
}