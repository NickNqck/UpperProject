package fr.nicknqck.upper.arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaCompletor implements TabCompleter {

    private final ArenaCommand arenaCommand;

    public ArenaCompletor(ArenaCommand arenaCommand) {
        this.arenaCommand = arenaCommand;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (strings.length == 1) {
                final List<String> list = new ArrayList<>();
                list.add("join");
                if (arenaCommand.getUuidWinner() != null){
                    if (arenaCommand.getUuidWinner().equals(((Player) commandSender).getUniqueId())){
                        list.add("reward");
                    }
                }
                if (commandSender.isOp()) {
                    list.add("list");
                }
                if (!strings[0].isEmpty()) {
                    for (String string : list) {
                        if (string.startsWith(strings[0])) {
                            return new ArrayList<>(Collections.singletonList(string));
                        }
                    }
                }
                return list;

            }
        }
        return Collections.emptyList();
    }
}
