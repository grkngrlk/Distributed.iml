package nonCoordinatorSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class serverDistributed implements Runnable {

    //the port on which server will listen for connections
    boolean established = false;
    public static int portNumber;
    private HashSet<String> serverLogs;
    String message = null;

    ServerSocket serverSocket = null;
    Socket clientSocket = null;
    PrintWriter socketOut = null;
    BufferedReader socketIn = null;

    public serverDistributed(Integer portNmbr) {
        portNumber = portNmbr;
        tryConnect();
        System.out.println("Connected to Port: " + portNumber);
        serverLogs = new HashSet<String>();

    }

    public void run() {
        startListening();
    }

    //Checks port for is it available
    public void tryConnect() {

        try {
            //initialize server socket
            serverSocket = new ServerSocket(portNumber);

        } catch (IOException e) { //if this port is busy, an IOException is fired
            System.out.println("Cannot listen on port " + portNumber);
            e.printStackTrace();
            System.exit(0);
        }

    }

    //After checking, establishes connection given port when client is available and starts listening for messages from it
    public void startListening() {


        try {
            //wait for client connections
            System.out.println("Server socket initialized.\nWaiting for a client connection. Listening to Port: " + portNumber + "\n");
            clientSocket = serverSocket.accept();

            //let us see who connected
            String clientName = clientSocket.getInetAddress().getHostName();
            System.out.println(clientName + " established a connection.");
            System.out.println();

            //will use socketOut to send text to the server over the socket
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
            //will use socketIn to receive text from the server over the socket
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Cannot get I/O for the connection.");
            e.printStackTrace();
            System.exit(0);
        }

        //System.out.println("Im here 1");
        //if is it established, this variable will be true for starting infinite loop
        established = true;

        /**
         * Protocol (known to both parties):
         * Server blocks on a message from the client.
         * Client sends a message and blocks on the server's response.
         * Upon receipt of message, server will respond: "You said: " + message
         * This continues for 3 rounds.
         */

        //Scanner input = new Scanner(System.in);

        while (established) {

            //System.out.println("Round (" + (i + 1) + ")");

            System.out.println("Waiting for a message from the client. Port: " + portNumber + "\n");

            try {
                message = socketIn.readLine();
                socketOut.println(myWorker(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //socketOut.println("You said: " + message);
            //System.out.println("Client's message was: \n\t\"" + message + "\"");
            System.out.println();
        }

    }

    public String myWorker(String message) {

        //System.out.println("Raw Text =" + message);

        String[] text = message.split(" ");

        //System.out.println("Split Text =" + message);
        //after every chose -except exit-, every chose return current hash size to coordinator for update.
        switch (text[0].toString()) {

            case "1":
                //System.out.println("Im in check");
                //System.out.println(""+text[0]);
                //System.out.println(""+text[1]);
                //System.out.println(""+text[2]);
                return check(message.substring(2));

            case "2":
                //System.out.println("Im in insert");
                //System.out.println(""+text[0]);
                // System.out.println(""+text[1]);
                //System.out.println(""+text[2]);
                insert(message.substring(2));
                return Integer.toString(serverLogs.size());

            case "3":
                //System.out.println("Im in delete");
                //System.out.println(""+text[0]);
                //System.out.println(""+text[1]);
                //System.out.println(""+text[2]);
                delete(message.substring(2));
                return Integer.toString(serverLogs.size());

            case "4":
                exit();
                return Integer.toString(1);

            default:
                return "Incorrect Message";
        }

    }

    //for checking value if it is in this hashtable
    public String check(String strPar) {
        System.out.println("Im in check: ");
        System.out.println("serverlogs:" + serverLogs);
        if (serverLogs.contains(strPar)) {
            System.out.println("Im in check: my response: 1");
            return "1";
        } else return "0";
    }

    //returns current size of this hash table, it necessary for updating sizes on coordinator side
    public String size() {
        return Integer.toString(serverLogs.size());
    }

    //for inserting new value to the hash table
    public void insert(String strPar) {

        //System.out.println("message:" + strPar);
        serverLogs.add(strPar);
        System.out.println("serverlogs in " + serverDistributed.portNumber + " : " + serverLogs);

    }

    //for removing value from the hash table
    public void delete(String strPar) {
        //System.out.println("message:" + strPar);
        serverLogs.remove(strPar);
        System.out.println("serverlogs in " + serverDistributed.portNumber + " : " + serverLogs);

    }

    //for closing server properly
    public void exit() {

        try {
            socketOut.close();
            socketIn.close();
            clientSocket.close();
            serverSocket.close();
            established = false;
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
