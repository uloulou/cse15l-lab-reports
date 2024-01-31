import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

class Handler implements URLHandler {
    // The one bit of state on the server: a number that will be manipulated by
    // various requests.
    ArrayList<String> listmessages = new ArrayList<String>();

    public String handleRequest(URI url) {
        String str = null;
        String userparameters = null;
        if (url.getPath().equals("/")) {
            return String.format("Type /add-message?s=<string>&user=<string> on the URL to add string");
        } 
        else if (url.getPath().equals("/add-message")) {
            String[] parameters = url.getQuery().split("&");
            for (int i = 0; i < parameters.length; i++){
                String[] strparameters = parameters[i].split("=");
                if (strparameters[0].equals("s")) {
                    str = strparameters[1];
                }
                else if (strparameters[0].equals("user")) {
                    userparameters = strparameters[1];
                }
            }
        } 
        if (str != null && userparameters != null){
            String message = userparameters + ": " + str + "\n";
            listmessages.add(message);
        }
        else {
            return "Invalid Input! Please make sure s and user is filled.";
        }
        for (int i = 0; i <= listmessages.size(); i++){
            return String.format(listmessages.get(i));
        }
    }
}

class ChatServer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler());
    }
}
