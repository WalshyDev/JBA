# JBA
Allows you to easily create a JDA bot with a command system fully built in!

JBA is a wrapper for JDA, it allows you to create a JDA bot with ease (especially for sharding) and it also has an in-built command system so that you can just sit back and make only the command classes and not worry about the users input to get the command or aliases!

# Setup

If you use Maven you will want to put this in your pom.xml
```xml
<repositories>
    <repository>
        <id>JBA-mvn-repo</id>
        <url>https://raw.github.com/WalshyDev/JBA/mvn-repo/</url>
    </repository>
</repositories>
...
<dependencies>
    <dependency>
        <groupId>com.walshydev.jba</groupId>
        <artifactId>JBA</artifactId>
        <version>1.1.5</version>
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
new YourBotClass().init(new JDABuilder(AccountType.BOT).setToken("<token>").setGame(Game.of("Thrones"), "<prefix>");
```

# Using the command system
To use the command system it is very easy, just make a class and implement Command like so, that will then generate the default methods that you can use.

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

# Setting up MySQL
For ease of use, JBA comes built in with a few classes to help you use MySQL as a database system. You can set up the database for usage in the pre-generated `public void run()` method made on implementing the JBA class. Inside that run method, simply enter the code below, replacing each String with your details for your MySQL DB.

```java
setupMySQL("mysql_user", "mysql_pass", "mysql_address", "mysql_dbname");
```

This can be easily configured with the Config system in JBA, which you can see [here.](#config-and-mysql)
You can then query the database like so:

```java
public class Clazz {
    private String id;
    private String message;

    public Clazz(String id, String message) {
        this.id = id;
        this.id = message;
    }
}

public Clazz getClazzById(String id) {
    final Clazz[] c = {null};
    try {
        SQLController.runSqlTask((conn) -> {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM dbname WHERE id = ?");
            statement.setString(1, id);
            ResultSet set = statement.executeQuery();
            if(set.next()) c[0] = new Clazz(id, set.getString("message"));
            else c[0] = new Clazz(id, null);
        });
    } catch (SQLException e) {
        c[0] = new Clazz(id, null);
    }
    return c[0];
}
```

# Config
JBA also comes with a `Config` class to make it easier to make configs.
You create it like this:

```java
Config config = new Config("config");
```

This creates a new file called config.json. You can then write in your config, following the template found in the Example Config area below.
You can check if a value exists using `config.exists(String path)`, and you can recall values from the config using one of the methods below, and a few others.

```java
config.getString("token"); // Returns "botToken"
config.getString("prefix"); // Returns "!~"
config.getString("mysql.user"); // Returns "user"
```
## Config and MySQL
You also can use this with the `setupMySQL()` method mentioned earlier for a more secure and more configurable setup.

```java
setupMySQL(config.getString("mysql.user"), config.getString("mysql.password"), config.getString("mysql.address"), config.getString("mysql.dbname"));
```

## Example Config
```
{
  "token": "botToken",
  "prefix": "!~",
  "mysql": {
    "user": "user",
    "password":"password",
    "address":"127.0.0.1",
    "dbname":"botdatabase"
  }
}
```
