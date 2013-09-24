package net.sf.sockettest.swing;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import net.sf.sockettest.PortModel;
import net.sf.sockettest.*;

/**
 *
 * @author Akshathkumar Shetty
 */
public class PortDialog extends JDialog {
    public static final int UDP = 1;
    public static final int TCP = 2;
    private PortModel model;
    
    /** Creates a new instance of PortDialog */
    public PortDialog(JFrame parent, int type) {
        super(parent);
        if(type==TCP) {
            setTitle("Standard TCP Port");
            model = new PortModel("tcpports.txt");
        } else {
            setTitle("Select UDP port");
            model = new PortModel("udpports.txt");
        }
        Container cp = getContentPane();
        
        JTable table = new JTable(model);
        cp.add(new JScrollPane(table));
        setSize(300,200);
        Util.centerWindow(this);
    }
    
    public String getPort() {
        return model.getPort();
    }
    
}
