package coordinatorSide;

import java.util.Scanner;

public class coordinator {

    public static String fileLocation;

    public static void main(String[] args) {

        boolean isOn = false;
        String choice;
        String textToSend;

        fileLocation = args[0];
        coordinatorManager start = new coordinatorManager(fileLocation);

        isOn = true;
        Scanner userChoose = new Scanner(System.in);
        System.out.println("\nPlease Make a Choice");

        isOn = true;

        while (isOn) {
            int returnIndex;
            System.out.println("1. Query a string s.\n" +
                    "2. Insert a new string s.\n" +
                    "3. Delete an existing string s.\n" +
                    "4. Exit");

            choice = userChoose.nextLine();

            switch (choice) {

                case "1":
                    System.out.println("Please Enter for Search\n");

                    textToSend = userChoose.nextLine();
                    //System.out.println("okunan: " + textToSend);

                    returnIndex = start.findWhere(textToSend);
                    //System.out.println("Gelen Yanıt = " + returnIndex);

                    if (returnIndex != -1)
                        System.out.println("" + textToSend + " Found on " + start.getCurrentClients().get(returnIndex).portNumber + "\n");

                    else System.out.println("\nLog Not Found");
                    break;

                case "2":
                    System.out.println("Please Enter for Insert\n");

                    textToSend = userChoose.nextLine();
                    //System.out.println("okunan " + textToSend);

                    returnIndex = start.insert(textToSend);
                    //System.out.println("Gelen Yanıt = " + returnIndex);

                    if (returnIndex == -1) System.out.println("\nLog Already Exist");

                    else System.out.println("\n" + textToSend + " Inserted to End Server, Port:" + returnIndex);
                    break;

                case "3":
                    System.out.println("Please Enter for Delete\n");

                    textToSend = userChoose.nextLine();
                    returnIndex = start.delete(textToSend);
                    if (returnIndex == -1) System.out.println("\nLog Not Found");

                    else System.out.println("\n" + textToSend + " Deleted From End Server, Port:" + returnIndex);
                    break;

                case "4":
                    System.out.println("Please Enter for Delete\n");

                    start.exitAll();
                    System.exit(0);
            }

        }


    }

}
