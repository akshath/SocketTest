package net.sf.sockettest;

import java.net.*;
import java.io.*;
import java.awt.*;

/**
 *
 * @author Akshathkumar Shetty
 */
public class Util {
    
    public static void centerWindow(Window win) {
        Dimension dim = win.getToolkit().getScreenSize();
        win.setLocation(dim.width/2 - win.getWidth()/2,
                dim.height/2 - win.getHeight()/2);
    }
    
    public static boolean checkHost(String host) {
        try {
            InetAddress.getByName(host);
            return(true);
        } catch(UnknownHostException uhe) {
            return(false);
        }
    }
    
    public static void writeFile(String fileName, String text)
    throws IOException {
        PrintWriter out = new PrintWriter(
                new BufferedWriter(new FileWriter(fileName)));
        out.print(text);
        out.close();
    }
    
    public static String readFile(String fileName, Object parent)
    throws IOException {
        StringBuffer sb = new StringBuffer();
        ClassLoader cl = parent.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String s;
        while((s = in.readLine()) != null) {
            sb.append(s);
            sb.append("\n");
        }
        in.close();
        return sb.toString();
    }    
}
