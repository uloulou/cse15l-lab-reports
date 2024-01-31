# Lab Report 2 - Servers and SSH Keys (WEEK 3)

## Part 1

For each of the two screenshots, describe:

Which methods in your code are called?
What are the relevant arguments to those methods, and the values of any relevant fields of the class?
How do the values of any relevant fields of the class change from this specific request? If no values got changed, explain why.
By values, we mean specific Strings, ints, URIs, and so on. "abc" is a value, 456 is a value, new URI("http://...") is a value, and so on.)

```
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
        String text = "";
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
            String message = userparameters + ": " + str + "\n";
            listmessages.add(message);
            for (int i = 0; i < listmessages.size(); i++) {
                String lines = listmessages.get(i);
                text += lines;
            }
            System.out.println(text);

            return text;
        } 
        else {
            return "Invalid Input! Please make sure s and user is filled.";
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
```

![Image](Images/text1.png)

![Image](Images/Text2.png)

## Part 2

The absolute path to the private key for your SSH key for logging into ieng6 (on your computer, an EdStem workspace, or on the home directory of the lab computer)

Working Directory when running the command:`.ssh`

command line : `ls ~\.ssh/id_rsa`

***The command lines are inside the red rectangle.***

![Image](Images/Private.png)

The absolute path to the public key for your SSH key for logging into ieng6 (this is the one you copied to your account on ieng6, so it should be a path on ieng6's file system)

Working Directory when running the command:`.ssh`

command line : `ls ~\.ssh/id_rsa.pub`

***The command lines are inside the red rectangle.***

![Image](Images/Public.png)

A terminal interaction where you log into your ieng6 account without being asked for a password.

Working Directory when running the command:`.ssh`

command line : `ssh ulou@ieng6-201.ucsd.edu`

The screenshot shows it did not require me to enter my password when I log into my ieng6 account.

***The command lines are inside the red rectangle.***

![Image](Images/login.png)

## Part 3
As someone with a weak fundation in coding, the knowledge about urls, servers, and local machines are all brand new for me. I learnt how to link to a server and build a server that could work in week 2 and week 3. I also learnt hwo to implement different functions/methods such as add and search. I also learn what components make the website link. In addition, I learn what `scp` and `mkdir` is. Last thing I learnt is to set up ssh key for easier access to the server which saves a lot of time.
