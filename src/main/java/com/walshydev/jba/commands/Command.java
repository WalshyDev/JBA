package com.walshydev.jba.commands;

import net.dv8tion.jda.core.entities.*;

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
