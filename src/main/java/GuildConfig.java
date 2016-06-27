import java.util.ArrayList;

/**
 * Created by Vaerys on 22/05/2016.
 */
public class GuildConfig {

    String roleSelectChannel = "";
    String generalChannel = "";
    ArrayList<String> roles = new ArrayList<String>();
    /*first part of the array should be the User ID.
    the next part should be the server name no spaces allowed.
    then comes the server IP and port
    lastly the server description*/


    public String getRoleSelectChannel() {
        return roleSelectChannel;
    }

    public void setRoleSelectChannel(String roleSelect) {
        this.roleSelectChannel = roleSelect;
    }

    public String getGeneralChannel() {
        return generalChannel;
    }

    public void setGeneralChannel(String generalChannel) {
        this.generalChannel = generalChannel;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    public void removeRole(String role){
        for (int i = 0; i < roles.size();i++){
            if (roles.get(i).toLowerCase().equals(role.toLowerCase())){
                roles.remove(i);
            }
        }
    }

}
