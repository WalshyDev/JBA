# JBA
Allows you to easily create a JDA bot with a command system fully built in!

JBA is a wrapper for JDA, it allows you to create a JDA bot with ease (especially for sharding) and it also has an in-built command system so that you can just sit back and make only the command classes and not worry about the users input to get the command or aliases!

# Setup

If you use Maven you will want to put this in your pom.xml
```xml
<repositories>
    <repository>
        <id>JBA-mvn-repo</id>
        <url>https://raw.github.com/bwfcwalshyPluginDev/JBA/mvn-repo/</url>
    </repository>
</repositories>
...
<dependencies>
    <dependency>
        <groupId>com.walshydev.jba</groupId>
        <artifactId>JBA</artifactId>
        <version>1.1.3</version>
    </dependency>
</dependencies>
```

# Creating the JDA object

```java
// To make a new JDA object you will need to be in your JBA class and use the method init

// The AccountType of your bot, BOT or CLIENT
// The token of your bot or client
// Prefix you want to use for the command system (Don't include if you don't want to use the in-built system)
new YourBotClass().init(AccountType.BOT, "<token>", "<prefix>");

// If you want to do something like set the game using the builder you can also pass JDABuilder instead of AccountType and the token like so
new YourBotClass().init(new JDABuilder(AccountType.BOT).setToken("<token>").setGame(Game.of("Thrones"), "<prefix");
```

# Using the command system
To use the command system it is very easy, just make a class and implemenet Command like so, that will then generate the default methods that you can use.

```java
public class PingCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        // Get the time since the message sent by the user was created.
        long pongTime = message.getCreationTime().until(LocalDateTime.now().atOffset(ZoneOffset.UTC), ChronoUnit.MILLIS);
        channel.sendMessage("Pong! `" + pongTime + "ms`").queue();
    }

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Ping pong goes the bot";
    }
}
```
You do not need to set the description, that just makes it easier for help commands.

You can also override the method String[] getAliases() this allows you to set an alias for the command for example `p` instead of `ping`.
