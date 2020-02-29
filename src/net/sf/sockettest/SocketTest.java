package net.sf.sockettest;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import net.sf.sockettest.swing.About;
import net.sf.sockettest.swing.SocketTestClient;
import net.sf.sockettest.swing.SocketTestServer;
import net.sf.sockettest.swing.SocketTestUdp;
import net.sf.sockettest.swing.SplashScreen;

/**
 * @author Akshathkumar Shetty
 */
public class SocketTest extends JFrame {
    private static final String CLIENT = "c";
    private static final String SERVER = "s";
    private static final String UDP = "u";
    private ClassLoader cl = getClass().getClassLoader();
    public ImageIcon logo = new ImageIcon(cl.getResource("icons/logo.gif"));
    public ImageIcon ball = new ImageIcon(cl.getResource("icons/ball.gif"));
    private JTabbedPane tabbedPane;

    private static Map<String, NetService> services = new HashMap<>();

    /**
     * Creates a new instance of SocketTest
     */
    public SocketTest() {
        Container cp = getContentPane();

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        SocketTestClient client = new SocketTestClient(this);
        SocketTestServer server = new SocketTestServer(this);
        SocketTestUdp udp = new SocketTestUdp(this);
        About about = new About();

        services.put(CLIENT, client);
        services.put(SERVER, server);
        services.put(UDP, udp);

        tabbedPane.addTab("Client", ball, (Component) client, "Test any server");
        tabbedPane.addTab("Server", ball, server, "Test any client");
        tabbedPane.addTab("Udp", ball, udp, "Test any UDP Client or Server");
        tabbedPane.addTab("About", ball, about, "About SocketTest");

        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cp.add(tabbedPane);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("net.sourceforge.mlf.metouia.MetouiaLookAndFeel");
        } catch (Exception e) {
            //e.printStackTrace();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ee) {
                System.out.println("Error setting native LAF: " + ee);
            }
        }

        boolean toSplash = true;
        if (args.length > 0) {
            if ("nosplash".equals(args[0])) toSplash = false;
        }

        SplashScreen splash = null;
        if (toSplash) splash = new SplashScreen();

        SocketTest st = new SocketTest();
        st.setTitle("SocketTest v 3.0.1");
        st.setSize(600, 500);
        Util.centerWindow(st);
        st.setDefaultCloseOperation(EXIT_ON_CLOSE);
        st.setIconImage(st.logo.getImage());

        setUpArgParams(args);

        if (toSplash) splash.kill();
        st.setVisible(true);
    }

    private static void setUpArgParams(String[] configuration) {
        if (configuration.length < 1) {
            return;
        }

        //c:ip:port
        //s:ip:port
        //u:ip:port
        for (String arg : configuration) {
            String[] params = arg.split(":");
            if (params.length == 3) {
                String service = params[0];
                String ip = params[1];
                String port = params[2];
                services.get(service).setUpConfiguration(ip, port);
            }
        }
    }

}


