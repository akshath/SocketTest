package net.sf.sockettest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by Srikanth on 2/24/2016.
 */
public class TextFormatter {
    private static long seqNum = 1;

    public static String getSeqNum(){
        return String.format("%06d", seqNum++);
    }

    public static String transformMsg(String msg){
        if(msg.contains("{__SEQ__}") || msg.contains("{__seq__}")){
            msg = msg.replaceAll("(?i)\\{__SEQ__\\}", getSeqNum());
        }else if(msg.contains("{__NULL_}") || msg.contains("{__null__}")){
            msg = msg.replaceAll("(?)\\{__NULL_}", "\0");
        }

        msg = msg.replaceAll("\\\\n", "\n").replaceAll("\\\\r", "\r")
        .replaceAll("\\\\t", "\t")
        .replaceAll("\\\\0", "\0");
        
        return msg;
    }

    private static String toHex(String hexString){
        int hexInt = Integer.parseInt(hexString.substring(2), 16);
        return new String(Character.toChars(hexInt));
    }
}