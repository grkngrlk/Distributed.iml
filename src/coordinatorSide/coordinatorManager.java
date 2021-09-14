package coordinatorSide;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import nonCoordinatorSide.serverDistributed;

public class coordinatorManager {

    private static ArrayList<Client> currentClients;
    public static ArrayList<Integer> serverHashSizes;
    //public String messageToSend;
    int hashLocation;
    int nrOfDistributes;

    public coordinatorManager(String fileLocation) {
        currentClients = new ArrayList<Client>();
        //System.out.println("Clients: " + currentClients);

        serverHashSizes = new ArrayList<Integer>();
        //System.out.println("Size of Peer Servers " + serverHashSizes);

        //startClientsManual();
        setupCoordinator(fileLocation);

        System.out.println("Clients: " + currentClients);
        System.out.println("Sizes: " + serverHashSizes);

    }

    public static ArrayList<Client> getCurrentClients() {
        return currentClients;
    }

    //for testing manually without file IO
    public void startClientsManual() {

        Client c = new Client(4444);
        c.isEstablished = true;
        currentClients.add(c);
        serverHashSizes.add(0);


        Client c2 = new Client(4445);
        c2.isEstablished = true;
        currentClients.add(c2);
        serverHashSizes.add(0);

    }

    //this is for file IO and setup coordinator
    public void setupCoordinator(String fileLocationF) {

        ArrayList<String> config = new ArrayList<String>();
        Scanner scanner;
        String line;

        try {
            scanner = new Scanner(new File(fileLocationF));
            while (scanner.hasNextLine()) {
                config.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error in File = " + e);
        }

        System.out.println("File Read = " + config);

        nrOfDistributes = Integer.parseInt(config.get(0));

        System.out.println("Total Servers = " + nrOfDistributes);

        int smallestPort = Integer.parseInt(config.get(1).split(",")[1]);

        int i = 0;

        for (i = 0; i < nrOfDistributes - 1; i++) {
            int port = Integer.parseInt(config.get(1 + i).split(",")[1]);

            Client c = new Client(port);

            if (port < smallestPort) smallestPort = port;

            /*if (c.isEstablished == true) {

            }*/
            currentClients.add(c);
            serverHashSizes.add(0);
        }

        serverDistributed coordinatorServer = new serverDistributed(smallestPort - 1);
        System.out.println("Smallest Port" + (smallestPort - 1));
        Thread thread = new Thread(coordinatorServer);
        thread.start();

        Client coordinatorClient = new Client(smallestPort - 1);

        currentClients.add(coordinatorClient);
        serverHashSizes.add(0);

        System.out.println("currentClients: " + currentClients + "sizes: " + serverHashSizes);

        //read inputs file and then send add request for each input string
        ArrayList<String> input = new ArrayList<String>();

        try {
            scanner = new Scanner(new File(config.get(i + 1)));
            while (scanner.hasNextLine()) {
                input.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error in File = " + e);
        }

        System.out.println("File Read = " + input);

        for (i = 0; i < input.size(); i++) {
            int a = insert(input.get(i));
            System.out.println("inserted to " + a);
        }
    }

    //searchs for data passed by argument, if it finds it returns hash port
    //otherwise returns -1
    public int findWhere(String message) {

        WorkerThread[] workerThreads = new WorkerThread[currentClients.size()];

        System.out.println("Available Clients = " + currentClients + " Sizes = " + serverHashSizes);

        for (int i = 0; i < currentClients.size(); i++) {

            workerThreads[i] = new WorkerThread(currentClients.get(i), "1 " + message);
            workerThreads[i].run();

        }

        for (int i = 0; i < currentClients.size(); i++) {

            //System.out.println("findWhere response: " + workerThreads[i].serverResponse);

            if (workerThreads[i].serverResponse == 1) {
                hashLocation = i;
                //System.out.println("findWhere hashLocation: " + hashLocation);
                break;
            } else hashLocation = -1;

        }

        return hashLocation;

    }

    //It checks for parameter string and inserts if it is suitable
    public int insert(String message) {

        int hashLocation = findWhere(message);
        //System.out.println("hash location " + hashLocation);

        if (hashLocation == -1) {

            hashLocation = serverHashSizes.indexOf(Collections.min(serverHashSizes));

            //System.out.println("hashlocation in insert: " + hashLocation);
            WorkerThread worker = new WorkerThread(currentClients.get(hashLocation), "2 " + message);
            Thread thread = new Thread(worker);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("\n\nvalue before crash "+ worker.serverResponse);

            serverHashSizes.set(hashLocation, worker.serverResponse);

            System.out.println("Hash size: " + worker.serverResponse + " on Port: " + currentClients.get(hashLocation).portNumber);

            return currentClients.get(hashLocation).portNumber;

        } else return -1;

    }

    //It checks for parameter string and delete, if it is in one of the hash
    public int delete(String message) {

        int hashLocation = findWhere(message);
        //System.out.println("hash location " + hashLocation);

        if (hashLocation != -1) {

            //System.out.println("hashlocation in insert: " + hashLocation);
            WorkerThread worker = new WorkerThread(currentClients.get(hashLocation), "3 " + message);
            Thread thread = new Thread(worker);
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //System.out.println("\n\nvalue before crash "+ worker.serverResponse);

            serverHashSizes.set(hashLocation, worker.serverResponse);

            System.out.println("Hash size " + worker.serverResponse + " on Port: " + currentClients.get(hashLocation).portNumber);

            return currentClients.get(hashLocation).portNumber;

        } else return -1;
    }

    public void exitAll() {

        for (int i = 0; i < currentClients.size(); i++) {
            //Closing Servers
            WorkerThread worker = new WorkerThread(currentClients.get(i), "4 a");
            Thread thread = new Thread(worker);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //Closing Clients
        for (int i = 0; i < currentClients.size()-1; i++) {
            currentClients.get(i).exit();
        }
    }

}
