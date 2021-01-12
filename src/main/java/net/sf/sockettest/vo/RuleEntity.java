package net.sf.sockettest.vo;

import java.util.HashMap;

/**
 * Created by Srikanth on 2/19/2016.
 */
public class RuleEntity {
    private String ruleName;
    private String inRule;
    private String outRule;
    private boolean isHeartBeat;
    private boolean isRegex;
    private HashMap<String, String> placeHolders;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getInRule() {
        return inRule;
    }

    public void setInRule(String inRule) {
        this.inRule = inRule;
    }

    public String getOutRule() {
        return outRule;
    }

    public void setOutRule(String outRule) {
        this.outRule = outRule;
    }

    public boolean isHeartBeat() {
        return isHeartBeat;
    }

    public void setHeartBeat(boolean heartBeat) {
        isHeartBeat = heartBeat;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public void setRegex(boolean regex) {
        isRegex = regex;
    }

    @Override
    public String toString() {
        return ruleName;
    }

    @Override
    public boolean equals(Object obj) {
        return ruleName.equals(((RuleEntity)obj).getRuleName());
    }

    @Override
    public int hashCode() {
        return ruleName.hashCode();
    }
}
