

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {

    public static void main(String[] args) {

            try {

                Scanner sc = new Scanner(System.in);
                System.out.print("Veuillez entrer l'adresse IP du serveur : ");
                String address = sc.nextLine();
                System.out.print("Veuillez entrer le numero de port du serveur : ");
                int port = Integer.parseInt(sc.nextLine());

                Socket clientSocket = new Socket(address, port);
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream());
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                Thread send = new Thread(new Runnable() {
                    String msg;
                    @Override
                    public void run() {
                        while(true){
                            msg = sc.nextLine();
                            output.println(msg);
                            output.flush();
                        }
                    }
                });
                send.start();


                Thread receive = new Thread(new Runnable() {
                    String msg;
                    Midi playTrack;
                    Song receivedSong = null;
                    int tempo;
                    int velocity;

                    @Override
                    public void run() {
                        try {

                            msg = input.readLine();

                            while(msg != null){
                                System.out.println(msg);

                                if (msg.compareTo("Lancement du morceau : ") == 0){

                                    try {
                                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                                        Object objectReceived = in.readObject();
                                        File currentFile = (File) objectReceived;
                                        playTrack = new Midi(currentFile, new Window(1000, 600), receivedSong);

                                        Scanner t = new Scanner(System.in);

                                        playTrack.start();
                                        while(t.next().compareTo("stop")!=0 && playTrack.sequencer.isRunning()){
                                            Thread.sleep(1);
                                        }
                                        playTrack.stop();
                                        
                                    }
                                    catch (Exception ex)
                                    {
                                        System.out.println(ex.getMessage());
                                    }

                                }
                                else if (msg.compareTo("Entrez le tempo (>0) : ") == 0) {
                                    tempo = Integer.parseInt(input.readLine());
                                }
                                else if (msg.compareTo("Entrez la hauteur (entre -30 et 30) : ") == 0) {
                                    velocity = Integer.parseInt(input.readLine());
                                }
                                else if (msg.compareTo("Envoi du morceau : ") == 0)
                                {
                                    System.out.println("Reception des informations du morceau");
                                    try{
                                        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                                        Object objectReceived = in.readObject();
                                        receivedSong = (Song) objectReceived;
                                    }
                                    catch (Exception ex)
                                    {
                                        System.out.println(ex.getMessage());
                                    }

                                }
                                msg = input.readLine();
                            }

                            System.out.println("Traitement termine...");
                            output.close();
                            clientSocket.close();
                            System.out.println("Deconnexion...");
                            System.exit(0);

                        }catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    }
                });
                receive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

}

