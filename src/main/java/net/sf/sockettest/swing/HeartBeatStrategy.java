package net.sf.sockettest.swing;

import net.sf.sockettest.callbacks.*;
import net.sf.sockettest.mode.AppMode;
import net.sf.sockettest.mode.ApplicationMode;
import net.sf.sockettest.mode.KeepAliveTimerTask;
import net.sf.sockettest.util.TextFormatter;
import net.sf.sockettest.vo.KeepAliveConfig;
import net.sf.sockettest.vo.RuleEntity;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Srikanth on 2/18/2016.
 */
public class HeartBeatStrategy extends JPanel {
    private JPanel radioPanel;
    private JPanel topPanel;
    private JPanel centerPanel;
//    private JPanel bottomPanel;
    private JPanel mainPanel;
    private JPanel optionsGrp;

    private ApplicationMode applicationMode;

    private Box bottomPanel = Box.createHorizontalBox();

    private JCheckBox checkBox= new JCheckBox("Heart Beat");

    private JRadioButton serverModeButton = new JRadioButton("Server Mode");
    private JRadioButton normalModeButton = new JRadioButton("Client Mode");

    private ButtonGroup radioGrp = new ButtonGroup();

    private JCheckBox regexCheckBox = new JCheckBox("Regex");

    private JTextField ruleName = new JTextField(50);

    private JTextField delayText = new JTextField(6);

    private JLabel reccurentRule = new JLabel("Heartbeat Message");
    private JTextArea keepAliveTextArea = new JTextArea();
    private JScrollPane keepAliveTextScroll = new JScrollPane(keepAliveTextArea);

    private JTextArea[] textAreas = new JTextArea[2];
    private JScrollPane[] scrollPanes = new JScrollPane[2];
    private String[] labels = new String[]{
            "In Format",
            "Out Format"
    };

    protected final JFrame parent;

    private Dimension textAreaDimen = new Dimension(400,80);
    final DefaultComboBoxModel<RuleEntity> model = new DefaultComboBoxModel<RuleEntity>();
    final DefaultComboBoxModel<KeepAliveConfig> keepaliveModel = new DefaultComboBoxModel<KeepAliveConfig>();
    private JComboBox rulesCombo = new JComboBox(model);
    private JComboBox keepAliveCombo = new JComboBox(keepaliveModel);

    private JTextField keepAliveName = new JTextField(25);
    private DisconnectListener listener = new DisconnectListener(){
        public void onDisconnect(){
            cancellAllTasks();
            isStartTimer = false;
            startTimer.setText("Start Heartbeat");
            serverModeButton.setSelected(true);
        }
    };
    public HeartBeatStrategy(final JFrame parent, final SocketTestServer server, final SocketTestClient client) {
        applicationMode = ApplicationMode.getInstance();
        this.server = server;
        this.client = client;

        client.addDisconnectListener(listener);
        server.addDisconnectListener(listener);

        System.out.println(applicationMode);
        this.parent = parent;
        Container cp = this;

        mainPanel = new JPanel();
        BorderLayout borderLayout = new BorderLayout(10, 0);

        mainPanel.setLayout(borderLayout);

        topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topPanelGridConstraints = new GridBagConstraints();

        centerPanel = new JPanel();
        BoxLayout layout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
        centerPanel.setLayout(layout);
        centerPanel.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Mappings for Incoming/Outgoing messages!"));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Rule Name"));
        panel.add(ruleName);
        centerPanel.add(panel);
        for (int i=0; i<scrollPanes.length; i++ ) {
            panel = new JPanel();
            scrollPanes[i] = new JScrollPane();
            scrollPanes[i].setPreferredSize(textAreaDimen);

            textAreas[i] = new JTextArea();
            textAreas[i].setLineWrap(true);
            scrollPanes[i].setViewportView(textAreas[i]);
            panel.setLayout(new FlowLayout());
            panel.add(new JLabel(labels[i]));
            panel.add(scrollPanes[i]);
            centerPanel.add(panel);
        }

        serverModeButton.setSelected(true);

        radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        radioPanel.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Mode"));
        radioGrp.add(serverModeButton);
        radioGrp.add(normalModeButton);
        radioPanel.add(serverModeButton);
        radioPanel.add(normalModeButton);

        topPanelGridConstraints.gridx=0;
        topPanelGridConstraints.gridy=0;
        topPanelGridConstraints.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(radioPanel, topPanelGridConstraints);

        //add delay stuff
        final JPanel delayPanel = new JPanel();
        delayPanel.setLayout(new FlowLayout());
        delayPanel.add(new JLabel("Delay (Heart Beat)"));
        delayText.setEnabled(false);
        delayPanel.add(delayText);

        disableKeepAliveControls();

        topPanelGridConstraints.gridx=1;
        topPanelGridConstraints.gridy=0;
        topPanel.add(delayPanel, topPanelGridConstraints);

        topPanelGridConstraints.gridx=0;
        topPanelGridConstraints.gridy=1;
        topPanelGridConstraints.ipady = 10;
        topPanelGridConstraints.fill = GridBagConstraints.HORIZONTAL;
        JPanel keepAlivePanel = new JPanel(new FlowLayout());
        keepAlivePanel.add(reccurentRule);
        keepAlivePanel.add(keepAliveName);
        topPanel.add(keepAlivePanel, topPanelGridConstraints);

        topPanelGridConstraints.gridx=0;
        topPanelGridConstraints.gridy=2;
        keepAliveTextScroll.setPreferredSize(textAreaDimen);
        topPanel.add(keepAliveTextScroll, topPanelGridConstraints);

        topPanelGridConstraints.gridx=1;
        topPanelGridConstraints.gridy=2;
        topPanelGridConstraints.fill=GridBagConstraints.NONE;
        topPanel.add(saveEditButton, topPanelGridConstraints);

        topPanelGridConstraints.gridx=0;
        topPanelGridConstraints.gridy=3;
        topPanelGridConstraints.fill=GridBagConstraints.HORIZONTAL;
        topPanel.add(keepAliveCombo, topPanelGridConstraints);

        topPanelGridConstraints.gridx=1;
        topPanelGridConstraints.gridy=3;
        topPanel.add(startTimer, topPanelGridConstraints);

        startTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(isStartTimer){
                    cancellAllTasks();
                    isStartTimer = false;
                    serverModeButton.setSelected(true);
                    return;
                }
                if(applicationMode.getEnv()!=null){
                    KeepAliveTimerTask task = null;
                    final KeepAliveConfig currentItem = (KeepAliveConfig) keepaliveModel.getSelectedItem();
                    switch (applicationMode.getEnv()){
                        case CLIENT:
                            task = new KeepAliveTimerTask(new TimerReady() {
                                public void onTimerReady() {
                                    System.out.println("Message from the text area :: "+currentItem.getMessage());
                                    client.sendMessage(TextFormatter.transformMsg(keepAliveTextArea.getText()));
                                }
                            });
                            break;
                        case SERVER:
                            task = new KeepAliveTimerTask(new TimerReady() {
                                public void onTimerReady() {
                                    server.sendMessage(TextFormatter.transformMsg(keepAliveTextArea.getText()));
                                }
                            });
                            break;
                        default:
                            return;
                    }
                    applicationMode.scheduleTimerTask(task, Integer.parseInt(delayText.getText())*1000L, true);
                    isStartTimer = true;
                    startTimer.setText("Stop Heartbeat");
                }else{
                    JOptionPane.showMessageDialog(topPanel, "Please start Server/Client first!", "Not Started", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        //delayPanel.setVisible(false);
        serverModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancellAllTasks();
                startTimer.setText("Start Heartbeat");
                isStartTimer = false;
            }
        });
        //when on normal mode, hide it
        normalModeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableKeepAliveControls();
                applicationMode.setMode(AppMode.RECIEVE_HEARTBEAT);
            }
        });

        optionsGrp = new JPanel();
        optionsGrp.setLayout(new FlowLayout());
        optionsGrp.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Controls"));
        optionsGrp.add(regexCheckBox);

        topPanel.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), "Heartbeat"));

        bottomPanel.add(Box.createVerticalGlue());


        buttonClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ruleName.setText("");
                for (JTextArea textArea: textAreas) {
                    textArea.setText("");
                }
            }
        });

        loadToModel();

        optionsGrp.add(rulesCombo);
        rulesCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                RuleEntity ruleEntity = (RuleEntity) e.getItem();
                if(ruleEntity==null) return;
                ruleName.setText(ruleEntity.getRuleName());
                textAreas[0].setText(ruleEntity.getInRule());
                textAreas[1].setText(ruleEntity.getOutRule());
                regexCheckBox.setSelected(ruleEntity.isRegex());
            }
        });
        bottomPanel.add(buttonSave);
        bottomPanel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RuleEntity ruleEntity = (RuleEntity) rulesCombo.getSelectedItem();

                try{
                    applicationMode.getDbStore().connect().delete(ruleEntity, new OnRuleProcess() {
                        public void onProcess(RuleEntity entity) {
                            model.removeElement(entity);
                            model.setSelectedItem(model.getElementAt(0));
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    try {
                        applicationMode.getDbStore().disconnect();
                    }catch (Exception exc){
                        exc.printStackTrace();
                    }
                }
            }
        });
        buttonSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ruleNameField = ruleName.getText().trim();
                String ruleInField = textAreas[0].getText().trim();
                String ruleOutField = textAreas[1].getText().trim();

                boolean isHeartBeat = serverModeButton.isSelected();
                boolean isRegex = regexCheckBox.isSelected();
                RuleEntity ruleEntity = new RuleEntity();
                ruleEntity.setRuleName(ruleNameField);
                ruleEntity.setInRule(ruleInField);
                ruleEntity.setOutRule(ruleOutField);
                ruleEntity.setHeartBeat(isHeartBeat);
                ruleEntity.setRegex(isRegex);
                try{
                    applicationMode.getDbStore().connect().save(ruleEntity, new OnRuleProcess() {
                        public void onProcess(RuleEntity entity) {
                            model.addElement(entity);
                            model.setSelectedItem(entity);
                        }
                    });
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    try {
                        applicationMode.getDbStore().disconnect();
                    }catch (Exception exc){
                        exc.printStackTrace();
                    }
                }
            }
        });
        /*bottomPanel.add(loadButton);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadToModel();
            }
        });*/
        loadToKeepAliveControls();
        keepAliveCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                KeepAliveConfig keepAliveConfig = (KeepAliveConfig) e.getItem();
                keepAliveName.setText(keepAliveConfig.getConfigName());
                delayText.setText(String.valueOf(keepAliveConfig.getDelay()));
                keepAliveTextArea.setText(keepAliveConfig.getMessage());
            }
        });
        saveEditButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    KeepAliveConfig currentItem = (KeepAliveConfig) keepaliveModel.getSelectedItem();
                    applicationMode.getDbStore().connect().saveConfig(keepAliveName.getText(), delayText.getText(), keepAliveTextArea.getText(), new OnSave<KeepAliveConfig>() {
                        public void onSave(KeepAliveConfig obj) {
                            keepaliveModel.removeElement(currentItem);
                            keepaliveModel.addElement(obj);
                        }
                    });
                }catch (Exception exc){
                    exc.printStackTrace();
                }finally {
                    try {
                        applicationMode.getDbStore().disconnect();
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
        bottomPanel.add(buttonClear);
        optionsGrp.add(bottomPanel);
        mainPanel.add(optionsGrp, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.SOUTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        //mainPanel.add(bottomPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    private JButton deleteButton = new JButton("Delete");
    private JButton buttonSave = new JButton("Save");
    private JButton buttonClear = new JButton("Clear");
    private JButton loadButton = new JButton("Load Rules");
    private JButton saveEditButton = new JButton("Save");
    private JButton startTimer = new JButton("Start Heartbeat");
    private boolean isStartTimer = false;

    private void cancellAllTasks(){
        disableKeepAliveControls();
        applicationMode.cancelAllTasks();
        applicationMode.initTimer();
        applicationMode.setMode(AppMode.SEND_HEARTBEAT);
    }

    private void loadToModel(){
        try {
            applicationMode.getDbStore().connect().getAllRules(new OnEachEntity<RuleEntity>() {
                public void onEachItem(RuleEntity entity) {
                    model.addElement(entity);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                applicationMode.getDbStore().disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void loadToKeepAliveControls(){
        try {
            applicationMode.getDbStore().connect().getConfigParam(new OnConfigFetch() {
                public void onRetrieveCOnfig(KeepAliveConfig config) {
                    keepAliveName.setText(config.getConfigName());
                    keepAliveTextArea.setText(config.getMessage());
                    delayText.setText(String.valueOf(config.getDelay()));
                    keepaliveModel.addElement(config);
                    keepaliveModel.setSelectedItem(config);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                applicationMode.getDbStore().disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void disableKeepAliveControls(){
        delayText.setEnabled(false);
        keepAliveTextArea.setEnabled(false);
        saveEditButton.setEnabled(false);
        startTimer.setEnabled(false);
        keepAliveCombo.setEnabled(false);
        keepAliveName.setEnabled(false);
    }

    private void enableKeepAliveControls(){
        delayText.setEnabled(true);
        keepAliveTextArea.setEnabled(true);
        saveEditButton.setEnabled(true);
        startTimer.setEnabled(true);
        keepAliveCombo.setEnabled(true);
        keepAliveName.setEnabled(true);
    }

    private SocketTestClient client;
    private SocketTestServer server;
}
