import java.util.ArrayList;
import java.util.List;

public class Translator {

    public List<String> translate(List<String> content) {
        final List<String> hackCommands = new ArrayList<>();
        final CommandResolver commandResolver = new CommandResolver(hackCommands);

        for(String command : content) {
            command = withoutComment(command);

            if(command.isBlank()) {
                continue;
            }

            var commands = command.split(" ");
            var first = commands[0];
            var second = commands.length > 1 ? commands[1] : null;
            var third = commands.length > 2 ? commands[2] : null;

            //hackCommands.add(first + ":");

            if(first.equals("push")) {
                commandResolver.Push(second, third);
            } else if(first.equals("pop")) {
                commandResolver.Pop(second, third);
            } else if(first.equals("add")) {
                commandResolver.Add();
            } else if(first.equals("sub")) {
                commandResolver.Sub();
            } else if(first.equals("neg")) {
                commandResolver.Neg();
            } else if(first.equals("eq")) {
                commandResolver.Eq();
            } else if(first.equals("gt")) {
                commandResolver.Gt();
            } else if(first.equals("lt")) {
                commandResolver.Lt();
            } else if(first.equals("and")) {
                commandResolver.And();
            } else if(first.equals("or")) {
                commandResolver.Or();
            } else if(first.equals("not")) {
                commandResolver.Not();
            } else {
                // remove later - just for debugging
                hackCommands.add("*** UNCHECKED COMMAND *** (" + command + ")");
            }


            //hackCommands.add(""); // remove later - just for debugging
        }

        return hackCommands;
    }

    private String withoutComment(String command) {
        if(command.contains("//")) {
            return command.split("//")[0].trim();
        }
        return command.trim();
    }
}
