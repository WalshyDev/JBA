package com.walshydev.jba.commands;

import com.walshydev.jba.ExampleBot;
import com.walshydev.jba.SQLController;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class PingCommand implements Command {

    @Override
    public void onCommand(User sender, TextChannel channel, Message message, String[] args, Member member) {
        // Get the time since the message sent by the user was created.
        long pongTime = message.getCreationTime().until(ZonedDateTime.now(), ChronoUnit.MILLIS);
        channel.sendMessage("Pong! `" + pongTime + "ms`").queue();
        try {
            SQLController.runSqlTask(conn -> {
                PreparedStatement statement = conn.prepareStatement("INSERT INTO pings (time_pinged, response_time) VALUES (?, ?)");
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.setLong(2, pongTime);
                statement.execute();
            });
        } catch (SQLException e) {
            ExampleBot.LOGGER.error("There was an error inserting into the MySQL table.", e);
        }
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
