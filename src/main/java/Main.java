import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.util.DiscordException;

import java.io.*;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Main {


    public static void main(String[] args) {

        String token;
        // you need to set a token in Token/Token.txt for the bot to run
        try {
            File configDir = new File("Token");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            File file = new File("Token/Token.txt");
            if(!file.exists()){
                file.createNewFile();
            }
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            token = bufferedReader.readLine();
            IDiscordClient client = Client.getClient(token, true);
            client.isBot();
            EventDispatcher dispatcher = client.getDispatcher();
            dispatcher.registerListener(new InterfaceListener());
            dispatcher.registerListener(new AnnotationListener());
        } catch (DiscordException ex) {
            System.out.println(ex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
