package net.sf.sockettest;

import net.sf.sockettest.callbacks.OnConfigFetch;
import net.sf.sockettest.callbacks.OnEachEntity;
import net.sf.sockettest.callbacks.OnRuleProcess;
import net.sf.sockettest.callbacks.OnSave;
import net.sf.sockettest.vo.KeepAliveConfig;
import net.sf.sockettest.vo.RuleEntity;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Srikanth on 2/19/2016.
 */
public class SqliteDBStore {
    private String jdbcUrl;
    private Connection connection;
    private String dbName;
    private HashMap<String, RuleEntity> directMappedRules;
    private LinkedList<RuleEntity> regexMappedRules;
    private String SQL_QUERY = "select * from main.response_data";

    private Map<String, KeepAliveConfig> keepAliveConfigMap;

    public synchronized RuleEntity getMappedEntity(String inRec){
        System.out.println("Recieved :: "+inRec);
        RuleEntity entity = directMappedRules.get(inRec);
        if(entity==null){
            for (RuleEntity rule: regexMappedRules) {
                if(Pattern.matches(rule.getInRule(), inRec)){
                    entity=rule;
                    break;
                }
            }
        }
        return entity;
    }

    public SqliteDBStore(String dbName){
        this.dbName = dbName;
        regexMappedRules = new LinkedList<RuleEntity>();
        directMappedRules = new HashMap<String, RuleEntity>();
        keepAliveConfigMap = new ConcurrentHashMap<String, KeepAliveConfig>();
    }

    public SqliteDBStore connect() throws ClassNotFoundException, SQLException{
        Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:socket_db.db");
 // connection = DriverManager.getConnection("jdbc:sqlite:tools/socket-test/src/main/resources/socket_db.db");
        return this;
    }

    public void delete(RuleEntity rule, OnRuleProcess ruleSaveCallback) throws SQLException{
        String INSERT_QUERY = "delete from main.response_data where rule_name=?";
        PreparedStatement statement = connection.prepareStatement(INSERT_QUERY);
        statement.setString(1, rule.getRuleName().isEmpty()?"":rule.getRuleName());

        int count = 0;
        try {
            count = statement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(count>0){
            directMappedRules.remove(rule.getInRule());
            regexMappedRules.remove(rule);
            ruleSaveCallback.onProcess(rule);
        }
    }

    public boolean isRuleAvailable(RuleEntity ruleEntity){
        boolean found = directMappedRules.values().contains(ruleEntity);
        if(!found){
            found = regexMappedRules.contains(ruleEntity);
        }
        return found;
    }
    public boolean save(RuleEntity rule, OnRuleProcess ruleSaveCallback) throws SQLException{
        String INSERT_QUERY = "insert into main.response_data values(?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(INSERT_QUERY);
        statement.setString(1, rule.getRuleName().trim().isEmpty()?null:rule.getRuleName());
        statement.setString(2, rule.getInRule().trim().isEmpty()?null:rule.getInRule());
        statement.setString(3, rule.getOutRule().trim().isEmpty()?null:rule.getOutRule());
        statement.setBoolean(4, rule.isHeartBeat());
        statement.setBoolean(5, rule.isRegex());
        boolean success = false;
        try {
            statement.execute();
            success = true;
        }catch (Exception e){
            success = false;
        }
        if(success){
            if(rule.isRegex()){
                regexMappedRules.add(rule);
            }else {
                directMappedRules.put(rule.getInRule(), rule);
            }
            ruleSaveCallback.onProcess(rule);
        }

        return success;
    }

    public synchronized void saveConfig(String configName, String delay, String message, OnSave<KeepAliveConfig> saveCallback) throws SQLException{
        KeepAliveConfig keepAliveConfig = keepAliveConfigMap.get(configName);
        String CONFIG_UPDATE_QUERY = "update main.keepalive_config set message=?, interval=? where config_name=?";
        if(keepAliveConfig==null){
            CONFIG_UPDATE_QUERY = "insert into main.keepalive_config(message, interval, config_name) values(?, ?, ?)";
        }
        PreparedStatement statement = connection.prepareStatement(CONFIG_UPDATE_QUERY);
        statement.setString(1, message.isEmpty()?null:message);
        int delayTime = Integer.parseInt(delay);
        statement.setInt(2, delayTime);
        statement.setString(3, configName);

        boolean success = false;
        try {
            statement.execute();
            success = true;
        }catch (Exception e){
            success = false;
        }

        if(success){
            if(keepAliveConfig==null) {
                keepAliveConfig = new KeepAliveConfig();
                keepAliveConfigMap.put(configName, keepAliveConfig);
                keepAliveConfig.setConfigName(configName);
            }
            keepAliveConfig.setDelay(delayTime);
            keepAliveConfig.setMessage(message);
            saveCallback.onSave(keepAliveConfig);
        }
    }

    public void getConfigParam(OnConfigFetch callback) throws SQLException{
        String QUERY_CONFIG = "select * from main.keepalive_config";

        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(QUERY_CONFIG);
        //if(result.getFetchSize()<1) return;
        while (result.next()){
            KeepAliveConfig keepAliveConfig = new KeepAliveConfig();
            keepAliveConfig.setConfigName(result.getString(1));
            keepAliveConfig.setMessage(result.getString(2));
            keepAliveConfig.setDelay(result.getInt(3));
            keepAliveConfigMap.put(keepAliveConfig.getConfigName(), keepAliveConfig);
            callback.onRetrieveCOnfig(keepAliveConfig);
        }
    }

    public void getAllRules(OnEachEntity<RuleEntity> entity) throws SQLException{
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(SQL_QUERY);
        //List<RuleEntity> rules = new ArrayList<RuleEntity>(result.getFetchSize());
        while (result.next()){
            RuleEntity rule = new RuleEntity();
            rule.setRuleName(result.getString(1));
            rule.setInRule(result.getString(2));
            rule.setOutRule(result.getString(3));
            rule.setHeartBeat(result.getBoolean(4));
            rule.setRegex(result.getBoolean(5));
            //rules.add(rule);
            if(rule.isRegex()){
                regexMappedRules.add(rule);
            }else {
                directMappedRules.put(rule.getInRule(), rule);
            }
            entity.onEachItem(rule);
        }

    }

    public void disconnect() throws SQLException{
        connection.close();
    }
}
