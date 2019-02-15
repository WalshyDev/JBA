package com.walshydev.jba;

import com.walshydev.jba.commands.PingCommand;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class ExampleBot extends JBA {

    public static void main(String[] args){
        new ExampleBot().init();
    }

    private Config config;

    public void init(){
        // This is the new config system, here you make a JSON file which you can hold a bunch of configurable values.
        // In this case we are using it to store token and prefix.
        // The parameter for Config is the config name (If it doesn't have a .json extension it will be added itself!)
        config = new Config("config");
        // This is your account type so AccountType.BOT or AccountType.CLIENT
        // Then pass your bot/client token
        // Third argument is the command prefix. You can chose to not include this for no command system.
        super.init(AccountType.BOT, config.getString("token"), config.getString("prefix"));
    }

    @Override
    public void run() {
        // The code here is ran onReady

        // Setup a MySQL connection using user root with no password on localhost and use the database exampleBot.
        setupMySQL("root", "", "localhost", "exampleBot");

        // Register a new command
        registerCommand(new PingCommand());

        getClient().getPresence().setPresence(Activity.playing("JBA is cool"), false);

        // Log that the bot has fully started.
        LOGGER.info("Started the example bot successfully! JBA is running v" + getJBAVersion());

        config.set("welcome.messages", new ArrayList<String>());

        config.save();

        System.out.println(config.getStringList("welcome.messages"));
    }
}
