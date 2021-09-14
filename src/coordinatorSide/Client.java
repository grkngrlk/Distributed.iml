package coordinatorSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    //server's name or IP number
    public static String serverName = "localhost";
    //the port of the server to connect to
    public int portNumber;
    Socket clientSocket = null;
    PrintWriter socketOut = null;
    BufferedReader socketIn = null;
    public boolean isEstablished;

    public Client(Integer port) {
        this.portNumber = port;
        connectToServer();
    }

    public void connectToServer() {

        try {
            //create socket and connect to the server
            clientSocket = new Socket(serverName, portNumber);
            //will use socketOut to send text to the server over the socket
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
            //will use socketIn to receive text from the server over the socket
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) { //if serverName cannot be resolved to an address
            System.out.println("Who is " + serverName + "?");
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Cannot get I/O for the connection.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Protocol (known to both parties):
     * Server blocks on a message from the client.
     * Client sends a message and blocks on the server's response.
     * Upon receipt of message, server will respond: "You said: " + message
     * This continues for 3 rounds.
     */

    public String communicateToServer(String message) {

        socketOut.println(message);
        System.out.println("Message sent, waiting for the server's response. Im in client, my port = " + portNumber);
        String response = null;
        try {
            response = socketIn.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return ("An error occurred in Client: " + e);
        }
        return (response);
    }

    public void exit() {

        try {
            socketOut.close();
            socketIn.close();
            clientSocket.close();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
