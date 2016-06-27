import java.util.ArrayList;

/**
 * Created by Vaerys on 25/06/2016.
 */
public class CustomCommands {

    ArrayList<String[]> commands = new ArrayList<String[]>();
    final String[] commandNotFound = {"noUser","404","No Command with that name found."};

    public String createCommand(String userID, String commandName, String response){
        boolean noDuplicate = true;
        for (String[] sA : commands){
            if (sA[1].equalsIgnoreCase(commandName)){
                noDuplicate = false;
            }
        }
        if (noDuplicate) {
            String[] newEntry = new String[3];
            newEntry[0] = userID;
            newEntry[1] = commandName;
            newEntry[2] = response;
            commands.add(newEntry);
            return "Command Added";
        }
        return "A Command with that name already exists, Cannot create command.";
    }

    public String[] getCommand(String commandName){
        for (String[] cA: commands){
            if (cA[1].equalsIgnoreCase(commandName)){
                return cA;
            }
        }
        return commandNotFound;
    }

    public String removeCommand(boolean isMod, String userID, String commandName){
        for (String[] sA : commands){
            if (sA[1].equalsIgnoreCase(commandName)){
                if (sA[0].equalsIgnoreCase("LockedCommand")){
                    return "This command cannot be removed";
                }
                if (isMod || sA[0].equals(userID)){
                    commands.remove(sA);
                    return "Command Removed";
                }
                return "You Do not have Permission to Remove this command";
            }
        }
        return "A command with that name does not exist";
    }

    public String listCommands(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Here is the List of Custom Commands");
        for (String[] sA : commands){
            stringBuilder.append("\n  "+ sA[1]);
        }
        return stringBuilder.toString();
    }
}
