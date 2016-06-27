import sx.blah.discord.handle.impl.obj.Channel;
import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Commands {

    String guildConfigFile;
    String CCFile;
    Guild guild;
    Channel channel;
    Message message;
    IUser author;
    String guildID;
    private GuildConfig guildConfig;
    private CustomCommands customCommands;
    FileHandler handler = new FileHandler();
    private boolean isOwner;
    private boolean isAdmin;
    private boolean isMod;
    private boolean isCreator;
    String notAllowed;
    String errorMessage;

    public Commands(Message message) {
        this.message = message;
        guild = (Guild) message.getGuild();
        channel = (Channel) message.getChannel();
        author = message.getAuthor();
        guildID = message.getGuild().getID();
        notAllowed = "I'm Sorry " + author.getName() + " I'm afraid I can't do that.";
        guildConfigFile = "GuildConfigs/" + guildID + "_config.json";
        CCFile = "CommandLists/" + guildID + "_CustomCommands.json";
        errorMessage = "You have Found an error, please Mention this bot or " + guild.getUserByID(Globals.creatorID).getName() + " to let them know of this error";
        if (author.getID().equals(Globals.creatorID)) {
            isCreator = true;
        }
        if (guild.getOwner().equals(author)) {
            isOwner = true;
        }
        for (int i = 0; i < author.getRolesForGuild(guild).size(); i++) {
            IRole role = author.getRolesForGuild(guild).get(i);
            if (role.getPermissions().contains(Permissions.ADMINISTRATOR)) {
                isAdmin = true;
            }
            if (role.getPermissions().contains(Permissions.MANAGE_ROLES)) {
                isMod = true;
            }
        }
    }

    public void setPOGOS(GuildConfig guildConfig,CustomCommands customCommands) {
        this.guildConfig = guildConfig;
        this.customCommands = customCommands;
    }

    public void flushFiles() {
        handler.writetoJson(guildConfigFile, guildConfig);
        handler.writetoJson(CCFile, customCommands);
    }

    public String channelNotInit(String channelType) {
        return "The " + channelType + " Channel has not been set up yet please have an admin perform the command `" + Globals.commandPrefix + channelType + "Here` in the appropriate channel";
    }

    public String wrongChannel(String channelID){
        return "Command must be performed in " + guild.getChannelByID(channelID).toString();
    }

    @AliasAnnotation(alias = {"Hi", "Hello", "Greeting", "Hai", "Hoi"})
    @CommandAnnotation(name = "Hello", description = "Says Hello")
    public String HelloVoxBot() {
        if (isCreator) {
            return "Hello Creator";
        } else if (isOwner) {
            return "Hello Server Owner";
        } else if (isAdmin) {
            return "Hello Admin";
        } else if (isMod) {
            return "Hello Moderator";
        } else {
            return "Hello Normie";
        }
    }

    @CommandAnnotation(name = "SetGeneral", description = "Sets the current Channel as the Server's 'General' Channel")
    public String setGeneral() {
        if (isAdmin || isOwner) {
            guildConfig.setGeneralChannel(channel.getID());
            return "This Channel is now the Server's 'General' Channel.";
        } else {
            return notAllowed;
        }
    }

    @CommandAnnotation(name = "RoleSelectHere", description = "Sets the current Channel as the Server's 'Role Select' Channel")
    public String setRoleSelect() {
        if (isAdmin || isOwner) {
            guildConfig.setRoleSelectChannel(channel.getID());
            return "This Channel is now the Server's 'Role Select' Channel.";
        } else {
            return notAllowed;
        }
    }

    @CommandAnnotation(name = "Help", description = "Lists all of the Commands VoxBot can run")
    public String VoxBotHelp() {
        Method[] methods = this.getClass().getMethods();
        StringBuilder commandList = new StringBuilder();
        commandList.append("Hello My Name is VoxBot\n" +
                "Here are the commands currently at my disposal:");
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class)) {
                CommandAnnotation anno = m.getAnnotation(CommandAnnotation.class);
                commandList.append("\n   " + Globals.commandPrefix + anno.name());
            }
        }
        commandList.append("\nMy Author is Dawn Felstar and I am currently In Development\n" +
                "I am being written using Java with the Discord4J Libraries.\n" +
                "if you have any feedback or issues send a mention at me to let my creator know.");
        return commandList.toString();
    }

    @CommandAnnotation(name = "Info", description = "Gives information about a specific command\nUsage: >Info [Command]")
    public String VoxBotInfo() {
        Method[] methods = this.getClass().getMethods();
        String buildMessage = message.toString();
        if (message.toString().toLowerCase().equals(">info")) {
            return ">Info\nGives information about a specific command\nUsage: >Info [Command]";
        }
        String[] splitMessage = buildMessage.split(" ");
        for (Method m : methods) {
            if (m.isAnnotationPresent(CommandAnnotation.class) && !splitMessage[1].equals("")) {
                CommandAnnotation commandAnno = m.getAnnotation(CommandAnnotation.class);
                String testMessage = splitMessage[1].toLowerCase();
                String testTo = commandAnno.name().toLowerCase();
                if ((testMessage.startsWith(testTo)) && (testMessage.length() == testTo.length())) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(Globals.commandPrefix + commandAnno.name() + "\n" + commandAnno.description() + "\n");
                    if (m.isAnnotationPresent(AliasAnnotation.class)){
                        AliasAnnotation aliasAnno = m.getAnnotation(AliasAnnotation.class);
                        String[] alias = aliasAnno.alias();
                        builder.append("Aliases: ");
                        for (int i = 0; i < alias.length;i++){
                            builder.append(alias[i] + ", ");
                        }
                    }
                    return builder.toString();
                }
            }
        }
        return "That command does not exist.";
    }

    @CommandAnnotation(name = "ListRoles", channel = "RoleSelect", description = "Lists the Available roles that you can choose from")
    public String listRoles() {
        StringBuilder response = new StringBuilder();
        ArrayList<String> roles = guildConfig.getRoles();
        response.append("Here are the Roles you can choose from:\n");
        for (String s : roles) {
            response.append(guild.getRoleByID(s).getName() + ", ");
        }
        return response.toString();
    }

    @CommandAnnotation(name = "AddRole", channel = "RoleSelect", description = "Adds role to selectable roles\nUsage: >AddRole [Role name]")
    public String addRole() {
        if (isAdmin || isOwner || isMod) {
            String[] testMessage = message.toString().split(" ");
            if (message.toString().equalsIgnoreCase(">AddRole") || testMessage[1].equals("")) {
                return "Could not add role because you did not tell me which one you wanted to add. I'm a bot not a wizard.\nUsage >AddRole [role]";
            }
            ArrayList<IRole> roles = (ArrayList) guild.getRoles();
            String roleID;
            for (IRole r : roles) {
                if (r.getName().toLowerCase().equals(testMessage[1].toLowerCase()) && !testMessage[1].equals("")) {
                    roleID = r.getID();
                    guildConfig.addRole(roleID);
                    return "Role added.";
                }
            }
            return "role not found";
        } else {
            return notAllowed;
        }
    }

    @CommandAnnotation(name = "RemoveRole", channel = "RoleSelect", description = "Removes the role from the selectable roles\nUsage: >AddRole [Role name]")
    public String removeRole() {
        if (isAdmin || isOwner || isMod) {
            String[] testMessage = message.toString().split(" ");
            if (message.toString().equalsIgnoreCase(">RemoveRole") || testMessage[1].equals("")) {
                return "ERROR: USER SPECIFIED NOTHING AS A PARAMETER, CANNOT REMOVE NOTHING FROM RACE LIST.\nUsage >AddRole [role]";
            }
            ArrayList<IRole> roles = (ArrayList) guild.getRoles();
            String roleID;
            for (IRole r : roles) {
                if (r.getName().toLowerCase().equals(testMessage[1].toLowerCase()) && !testMessage[1].equals("")) {
                    roleID = r.getName();
                    guildConfig.removeRole(roleID);
                    return "Role removed.";
                }
            }
            return "role not found";
        } else {
            return notAllowed;
        }
    }

    @AliasAnnotation(alias = {"Iam","Role"})
    @CommandAnnotation(name = "Role", channel = "RoleSelect", description = "Gives you a role.\nUsage: >Role [Role name]")
    public String roles() {
        List<IRole> roles = author.getRolesForGuild(guild);
        ArrayList<String> roleList = guildConfig.getRoles();
        List<IRole> guildRoles = guild.getRoles();
        String response = "";
        for (int i = 0; i < roles.size(); i++) {
            for (String r : roleList) {
                if (roles.get(i).equals(guild.getRoleByID(r))) {
                    roles.remove(i);
                }
            }
        }
        boolean roleFound = false;
        String[] newRole = message.toString().split(" ");

        if (newRole[1].equalsIgnoreCase("remove")) {
            roleFound = true;
            response = "Your Roles were removed";
        }
        for (String r : roleList) {
            if (newRole[1].equalsIgnoreCase(guild.getRoleByID(r).getName())) {
                roles.add(guild.getRoleByID(r));
                roleFound = true;
            }
        }
        IRole[] newRoleList;
        newRoleList = roles.stream().toArray(IRole[]::new);
        try {
            if (roleFound) {
                guild.editUserRoles(author, newRoleList);
                if (response.equals("")) {
                    return "Your Roles have been updated";
                }
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(message.toString());
                stringBuilder.delete(0, newRole[0].length());
                response = "You cannot have the Role:" + stringBuilder.toString() + " as that Role does not exist";
            }
            return response;
        } catch (MissingPermissionsException e) {
            e.printStackTrace();
        } catch (DiscordException e) {
            e.printStackTrace();
        } catch (RateLimitException e) {
            e.printStackTrace();
        }
        return "failed to update Roles, if you are an admin VoxBot Cannot change your Role, but you can do it manually, you lazy person.";
    }

    @AliasAnnotation(alias = {"NewCC","CCNew"})
    @CommandAnnotation(name = "NewCC", description = "Creates a custom command\nUsage: >NewCC [CommandName] [Message]")
    public String newCC(){
        String[] splitString = message.toString().split(" ");
        StringBuilder command = new StringBuilder();
        command.append(message.toString());
        command.delete(0, (splitString[0].length() + splitString[1].length() + 2));
        return customCommands.createCommand(author.getID(),splitString[1],command.toString());
    }

    @AliasAnnotation(alias = {"DelCC","CCDel"})
    @CommandAnnotation(name = "DelCC", description = "Removes the Command\nUsage: >DelCC [CommandName]")
    public String delCC(){
        String[] splitString = message.toString().split(" ");
        return customCommands.removeCommand(isMod,author.getID(),splitString[1]);
    }

    @AliasAnnotation(alias = {"CCList","ListCCs"})
    @CommandAnnotation(name = "CCList", description = "Lists the Server's Custom Commands")
    public String listCCs(){
        return customCommands.listCommands();
    }

    @CommandAnnotation(name = "CCTags", description = "Lists all of the tags available to use in a custom command")
    public String tagsCC(){
        return "You can add any of the following tags to a Custom command to \n" +
                "get a special responce:\n" +
                "  #author# - replaces with the senders nickname\n" +
                "  #author!# - replaces with the senders username\n" +
                "  #args# - replaces with any text after the command";
    }

    @CommandAnnotation(name = "LogOut", description = "Only the Bot Creator Can run this command")
    public String logout() {
        if (author.getID().equals(Globals.creatorID)) {
            try {
                message.getClient().logout();
                System.exit(1);
            } catch (RateLimitException e) {
                e.printStackTrace();
            } catch (DiscordException e) {
                e.printStackTrace();
            }
            return "logging out";
        }
        return notAllowed;
    }
}

