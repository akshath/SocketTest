package net.sf.sockettest.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import net.sf.sockettest.*;


public class SplashScreen extends JWindow {
    
    protected ImageIcon logo;
    protected JLabel productName;
    
    public SplashScreen() {
	ClassLoader cl = getClass().getClassLoader();
        logo = new ImageIcon(cl.getResource("icons/logo.gif"));
        productName = new JLabel("<html><font face=\"Verdana\" size=10>"+
                "Socket Test",logo,JLabel.CENTER);
        //productName.setBackground(Color.white);
        productName.setOpaque(true);
        
        productName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10,10,10,10),
                BorderFactory.createLineBorder(Color.black) ));
        getContentPane().add(productName);
        Dimension dim=productName.getPreferredSize();
        dim.setSize(dim.getWidth()+10,dim.getHeight()+10);
        setSize(dim);
        Util.centerWindow(this);
        setVisible(true);
    }
    
    public void kill() {
        dispose();
    }
    
}
