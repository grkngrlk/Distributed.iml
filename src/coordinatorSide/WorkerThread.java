package coordinatorSide;
public class WorkerThread implements Runnable {
    public Client clientSocket;
    public String Message;
    public int serverResponse;

    public WorkerThread(Client s, String message) {
        clientSocket = s;
        Message = message;
    }
    public void run() {
        serverResponse = Integer.parseInt(clientSocket.communicateToServer(Message));
    }
}
