import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public static void main(String[] test) {

        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("Demarrage du serveur de karaoke...");
            System.out.print("Veuillez choisir un numero de port : ");
            int port = Integer.parseInt(sc.nextLine());

            ServerSocket socket = new ServerSocket(port);
            ServerContainer server = new ServerContainer(socket, new ArrayList<>());

            while (true)
            {
                System.out.println("En attente d'eventuels clients...");
                Socket clientSocket = server.serverSocket.accept();

                System.out.println("Un client vient de se connecter. \n");
                ClientTreatment traitement = new ClientTreatment(clientSocket, server);
                traitement.start();
                System.out.println("Fin traitement client");
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
