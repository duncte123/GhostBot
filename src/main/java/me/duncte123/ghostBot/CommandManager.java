package me.duncte123.ghostBot;

import me.duncte123.ghostBot.commands.GoingGhostCommand;
import me.duncte123.ghostBot.objects.Command;
import me.duncte123.ghostBot.variables.Variables;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class CommandManager {

    private final Set<Command> commands = ConcurrentHashMap.newKeySet();

    public CommandManager() {
        this.addCommand(new GoingGhostCommand());
    }

    public Command getCommand(String name) {
        Optional<Command> cmd = commands.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();

        if (cmd.isPresent()) {
            return cmd.get();
        }

        cmd = commands.stream().filter(c -> Arrays.asList(c.getAliases()).contains(name)).findFirst();

        return cmd.orElse(null);
    }

    public boolean addCommand(Command command) {
        if (command.getName().contains(" ")) {
            throw new IllegalArgumentException("Name can't have spaces!");
        }

        //ParallelStream for less execution time
        if (this.commands.stream().anyMatch((cmd) -> cmd.getName().equalsIgnoreCase(command.getName()))) {
            List<String> aliases = Arrays.asList(this.commands.stream().filter((cmd) -> cmd.getName().equalsIgnoreCase(command.getName())).findFirst().get().getAliases());
            for (String alias : command.getAliases()) {
                if (aliases.contains(alias)) {
                    return false;
                }
            }
            return false;
        }
        this.commands.add(command);

        return true;
    }

    public void handleCommand(GuildMessageReceivedEvent event) {
        final String rw = event.getMessage().getContentRaw();
        final String[] split = rw.replaceFirst(Pattern.quote(Variables.PREFIX), "").split("\\s+");
        final String invoke = split[0].toLowerCase();
        final String[] args = Arrays.copyOfRange(split, 1, split.length);

        Command cmd = getCommand(invoke);

        if(cmd != null)
            cmd.execute(invoke, args, event);
    }
}
