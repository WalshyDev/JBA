package com.walshydev.jba.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public interface Command {

    void onCommand(User sender, MessageChannel channel, Message message, String[] args, Member member);

    String getCommand();

    String getDescription();

    default String[] getAliases(){
        return new String[]{};
    }

    default boolean deleteMessage(){
        return false;
    }
}
