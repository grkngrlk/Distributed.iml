import coordinatorSide.Client;
import nonCoordinatorSide.serverDistributed;
import java.net.ServerSocket;


public class nonCoordinator {

    public static int portNumber;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        portNumber = Integer.parseInt(args[0]);
        //server class creator
        serverDistributed startServer = new serverDistributed(portNumber);
        new Thread(startServer).start();
    }
}