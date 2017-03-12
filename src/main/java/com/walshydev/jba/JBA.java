package com.walshydev.jba;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.walshydev.jba.commands.Command;
import com.walshydev.jba.sql.SQLTask;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class JBA {

    private static JBA instance;

    private JDA[] clients;
    private int shards;
    private String prefix;
    private static String version;

    private List<Command> commands = new CopyOnWriteArrayList<>();
    public static final Logger LOGGER = LoggerFactory.getLogger("JBA");

    /**
     * Use this method to create the bot, this will make a client for you and passing the bot prefix will setup the command system to use a given prefix.
     *
     * If you want to use custom prefix's for example override the {@link JBA#getPrefix(Guild)} method and make that return whatever is needed.
     * @param type The {@link AccountType} of bot.
     * @param token The bot token.
     * @param botPrefix The prefix of your bot.
     */
    public JDA init(AccountType type, String token, String botPrefix){
        return init(new JDABuilder(type).setToken(token), botPrefix);
    }

    /**
     * Use this method to create the bot, this will make a client for you.
     *
     * If you want to use custom prefix's for example override the {@link JBA#getPrefix(Guild)} method and make that return whatever is needed.
     * @param type The {@link AccountType} of bot.
     * @param token The bot token.
     */
    public JDA init(AccountType type, String token){
        return init(new JDABuilder(type).setToken(token), null);
    }

    /**
     * Use this method to create the bot, pass the JDA client and bot prefix to setup the command system to use a given prefix.
     *
     * If you want to use custom prefix's for example override the {@link #getPrefix(Guild)} method and make that return whatever is needed.
     * @param jdaBuilder JDABuilder, this is good if you want to set an audio factory for example.
     * @param botPrefix The prefix of your bot.
     */
    public JDA init(JDABuilder jdaBuilder, String botPrefix){
        return init(jdaBuilder, 1, botPrefix);
    }

    /**
     * Use this method to create the bot, this will make a client for you and passing the bot prefix will setup the command system to use a given prefix.<br>
     * This method will also shard with the given count.
     *
     * If you want to use custom prefix's for example override the {@link JBA#getPrefix(Guild)} method and make that return whatever is needed.
     * @param type The {@link AccountType} of bot.
     * @param token The bot token.
     * @param botPrefix The prefix of your bot.
     */
    public JDA init(AccountType type, String token, String botPrefix, int shards){
        return init(new JDABuilder(type).setToken(token), shards, botPrefix);
    }

    /**
     * Use this method to create the bot, this will make a client for you.<br>
     * This method will also shard with the given count.
     *
     * If you want to use custom prefix's for example override the {@link JBA#getPrefix(Guild)} method and make that return whatever is needed.
     * @param type The {@link AccountType} of bot.
     * @param token The bot token.
     */
    public JDA init(AccountType type, String token, int shards){
        return init(new JDABuilder(type).setToken(token), shards);
    }

    /**
     * Use this method to create the bot, pass the JDA client and bot prefix to setup the command system to use a given prefix.
     *
     * If you want to use custom prefix's for example override the {@link #getPrefix(Guild)} method and make that return whatever is needed.
     * @param jdaBuilder JDABuilder, this is good if you want to set an audio factory for example.
     */
    public JDA init(JDABuilder jdaBuilder, int shards){
        return init(jdaBuilder, shards, null);
    }

    /**
     * Use this method to create the bot, pass the JDA client and bot prefix to setup the command system to use a given prefix.
     *
     * If you want to use custom prefix's for example override the {@link #getPrefix(Guild)} method and make that return whatever is needed.
     * @param jdaBuilder JDABuilder, this is good if you want to set an audio factory for example.
     * @param shards The amount of shards you with to use.
     * @param botPrefix The prefix of your bot.
     */
    public JDA init(JDABuilder jdaBuilder, int shards, String botPrefix) {
        instance = this;
        jdaBuilder.addListener(new JBAListener());
        clients = new JDA[shards];
        this.shards = shards;
        if(clients.length == 1) {
            try {
                this.clients[0] = jdaBuilder.buildBlocking();
            } catch ( RateLimitedException e) {
                LOGGER.error("Failed to build client! Retrying in " + e.getRetryAfter() + " milliseconds", e);
                try {
                    Thread.sleep(e.getRetryAfter());
                } catch (InterruptedException e1) {}
            } catch (LoginException | InterruptedException e) {
                LOGGER.error("Failed to login!", e);
            }
        }else{
            for(int i = 0; i < clients.length; i++){
                while(true) {
                    try{
                        clients[i] = jdaBuilder.useSharding(i, clients.length).buildAsync();
                    } catch (RateLimitedException e) {
                        LOGGER.error("Failed to build client! Retrying in " + e.getRetryAfter() + " milliseconds", e);
                        try {
                            Thread.sleep(e.getRetryAfter());
                        } catch (InterruptedException e1) {}
                    } catch (LoginException e) {
                        LOGGER.error("Failed to login!", e);
                    }
                }
            }
        }
        this.prefix = botPrefix;
        return clients[0];
    }

    /**
     * Set's up a MySQL dataSource so that you can run {@link SQLController#runSqlTask(SQLTask)}
     * @param user MySQL User
     * @param password MySQL user password
     * @param serverName Server name
     * @param dbName Database name
     */
    public void setupMySQL(String user, String password, String serverName, String dbName){
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(serverName);
        dataSource.setPort(3306);
        dataSource.setPortNumber(3306);
        dataSource.setURL(dataSource.getURL() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
        dataSource.setUrl(dataSource.getUrl() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&autoReconnect=true&useSSL=false");
        SQLController.setDataSource(dataSource);
    }

    /**
     * This is called when the ReadyEvent is fired, call the {@link #registerCommand(Command)} method in this method.<br>
     * If sharding is used this will always return the first shard.
     */
    public abstract void run();

    public JDA getClient(){
        return this.clients[0];
    }

    public JDA[] getClients(){
        return this.clients;
    }

    public int getTotalShards(){
        return this.shards;
    }

    /**
     * You can override this to return the prefix of what a guild is using.
     * @param guild The guild currently trying to get the prefix for.
     * @return The prefix given from the init method.
     */
    public String getPrefix(Guild guild){
        return prefix;
    }

    public void registerCommand(Command command){
        this.commands.add(command);
    }

    public List<Command> getCommands(){
        return this.commands;
    }

    protected static JBA getInstance(){
        return instance;
    }

    public static String getJBAVersion(){
        if(version == null){
            Properties p = new Properties();
            try {
                p.load(instance.getClass().getClassLoader().getResourceAsStream("version.properties"));
            } catch (IOException e) {
                LOGGER.error("There was an error trying to load the version!", e);
                return null;
            }
            version = p.getProperty("version");
        }
        return version;
    }
}
