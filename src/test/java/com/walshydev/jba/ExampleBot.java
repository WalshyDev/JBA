package com.walshydev.jba;

import com.walshydev.jba.commands.PingCommand;
import net.dv8tion.jda.core.AccountType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ExampleBot extends JBA {

    public static void main(String[] args){
        new ExampleBot().init();
    }

    private File config;
    private Properties properties;

    public void init(){
        config = new File("config.prop");
        properties = new Properties();
        try {
            if(!config.exists()){
                config.createNewFile();
                properties.setProperty("token", "Not Set");
                properties.store(new FileOutputStream(config), null);
            }else{
                properties.load(new FileInputStream(config));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // This is your account type so AccountType.BOT or AccountType.CLIENT
        // Then pass your bot/client token
        // Third argument is the command prefix. You can chose to not include this for no command system.
        super.init(AccountType.BOT, properties.getProperty("token"), "!~");
    }

    @Override
    public void run() {
        // The code here is ran onReady

        // Setup a MySQL connection using user root with no password on localhost and use the database exampleBot.
        setupMySQL("root", "", "localhost", "exampleBot");

        // Register a new command
        registerCommand(new PingCommand());

        // Log that the bot has fully started.
        LOGGER.info("Started the example bot successfully!");
    }
}
