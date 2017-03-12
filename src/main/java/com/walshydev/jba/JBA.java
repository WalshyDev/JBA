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

    private JDA client;
    private String prefix;
    private String version;

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
     * Use this method to create the bot, pass the JDA client and bot prefix to setup the command system to use a given prefix.
     *
     * If you want to use custom prefix's for example override the {@link #getPrefix(Guild)} method and make that return whatever is needed.
     * @param jdaBuilder
     * @param botPrefix
     */
    public JDA init(JDABuilder jdaBuilder, String botPrefix){
        instance = this;
        jdaBuilder.addListener(new JBAListener());
        try {
            this.client = jdaBuilder.buildBlocking();
        } catch (LoginException | InterruptedException | RateLimitedException e) {
            LOGGER.error("Failed to build client", e);
        }
        this.prefix = botPrefix;
        return client;
    }

    /**
     * Set's up a MySQL dataSource so that you can run {@link SQLController#runSqlTask(SQLTask)}
     * @param user MySQL User
     * @param password MySQL user password
     * @param serverName Server name
     * @param dbName Database name
     */
    public void setupMySQL(String user, String password, String serverName, String dbName){
        MysqlDataSource dataSource = SQLController.getDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(dbName);
        dataSource.setServerName(serverName);
        dataSource.setPort(3306);
        dataSource.setPortNumber(3306);
    }

    /**
     * This is called when the ReadyEvent is fired, call the {@link #registerCommand(Command)} method in this method.
     */
    public abstract void run();

    public JDA getClient(){
        return this.client;
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

    public String getJBAVersion(){
        if(version == null){
            Properties p = new Properties();
            try {
                p.load(getClass().getClassLoader().getResourceAsStream("version.properties"));
            } catch (IOException e) {
                LOGGER.error("There was an error trying to load the version!", e);
                return null;
            }
            version = p.getProperty("version");
        }
        return version;
    }
}
