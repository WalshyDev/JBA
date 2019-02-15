package com.walshydev.jba;

import com.walshydev.jba.commands.Command;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class JBAListener extends ListenerAdapter {

    private JBA jba = JBA.getInstance();

    private static final ThreadGroup COMMAND_THREADS = new ThreadGroup("Command Threads");
    private static final ExecutorService CACHED_POOL = Executors.newCachedThreadPool(r ->
            new Thread(COMMAND_THREADS, r, "Command Pool-" + COMMAND_THREADS.activeCount()));

    @Override
    public void onReady(ReadyEvent event) {
        jba.run();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith(jba.getPrefix(event.getGuild())) &&
                (jba.getClient().getAccountType() == AccountType.BOT && !event.getAuthor().isBot() ||
                        jba.getClient().getAccountType() == AccountType.CLIENT && event.getMessage()
                .getAuthor().getId().equals(jba.getClient().getSelfUser().getId()))) {
            String message = event.getMessage().getContentRaw();
            String command = message.replaceFirst(Pattern.quote(jba.getPrefix(event.getGuild())), "");
            String[] args = new String[0];
            if (message.contains(" ")) {
                command = command.substring(0, command.indexOf(" "));

                args = message.substring(message.indexOf(" ") + 1).split(" ");
            }
            for (Command cmd : jba.getCommands()) {
                if (cmd.getCommand().equalsIgnoreCase(command)) {
                    execute(cmd, args, event);
                    return;
                } else {
                    for (String alias : cmd.getAliases()) {
                        if (alias.equalsIgnoreCase(command)) {
                            execute(cmd, args, event);
                            return;
                        }
                    }
                }
            }
        }
    }

    private void execute(Command cmd, String[] args, MessageReceivedEvent event) {
        CACHED_POOL.submit(() -> {
            JBA.LOGGER.info(
                    "Dispatching command '" + cmd.getCommand() + "' " + Arrays.toString(args) + " in " + event.getChannel() + "! Sender: " +
                            event.getAuthor().getName() + '#' + event.getAuthor().getDiscriminator());
            try {
                cmd.onCommand(event.getAuthor(), event.getChannel(), event.getMessage(), args, event.getMember());
            } catch (Exception ex) {
                JBA.LOGGER.error("There was an exception while executing the command '" + cmd.getCommand() + "'", ex);
            }
            if (cmd.deleteMessage())
                delete(event.getMessage());
        });
    }

    private void delete(Message message) {
        if (message.getTextChannel().getGuild().getSelfMember()
                .getPermissions(message.getTextChannel()).contains(Permission.MESSAGE_MANAGE))
            message.delete().queue();
    }
}