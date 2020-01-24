import java.io.*;
import java.net.Socket;
import java.util.List;

class ClientTreatment {
    Socket socketClient;
    ServerContainer container;
    PrintWriter output;
    BufferedReader input;

    ClientTreatment(Socket socketClient, ServerContainer container) {
        this.socketClient = socketClient;
        this.container = container;
    }

    // Copie du fichier d'entrée vers fichier de sortie
    protected void copy(File file, File copy) throws IOException {

        FileInputStream in = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(copy);
        int i;

        while ((i = in.read())!=-1)
        {
            out.write(i);
        }
        in.close();
        out.close();
    }

    String sendQuestion(String[] msg) throws IOException {
        for (String sentence : msg){
            output.println(sentence);
        }
        output.flush();
        return input.readLine();
    }

    void start() throws IOException {

        output = new PrintWriter(socketClient.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));

        output.println("Bienvenue sur le serveur de karaoke !!!");

        String path_input = "src/musicInput/";
        String path_output = "src/musicOutput/";

        Statistics user = new Statistics(new File("src/statistics/user.txt"));
        Statistics bestTrack = new Statistics(new File("src/statistics/track.txt"));
        String username = sendQuestion(new String[]{"Entrez votre nom utilisateur : "});

        while (!user.getUsername(username)) {
            username = sendQuestion(new String[]{"Erreur. Entrez votre nom utilisateur : "});
        }

        boolean connexion = true;
        while (connexion) {

            String[] menu = {"\nMenu", "1 : Consulter la playlist", "2 : Consulter les statistiques", "3 : Se deconnecter\n", "Choix : "};

            String choice = sendQuestion(menu);

            System.out.println("Le client a choisi l'option :" + choice);

            switch (choice) {

                case "1":
                    System.out.println("Envoi de la playlist au client...");
                    container.sendPlaylist(output);

                    String trackId = sendQuestion(new String[]{"Choix du morceau : "});

                    // Retour au menu
                    if (container.getSong(Integer.parseInt(trackId)) == null)
                    {
                        break;
                    }

                    Song currentSong = container.getSong(Integer.parseInt(trackId));
                    String track_name = currentSong.title;
                    System.out.println("Le client a choisi le morceau " + track_name);

                    // Desactivation des voix
                    output.println("Ce morceau contient " + currentSong.voices.size() + " voix.");
                    if (currentSong.voices.size() > 1)
                    {
                        String changeVoice = sendQuestion(new String[]{"Voulez-vous desactiver une des voix ? (oui ou non)"});

                        if (changeVoice.compareTo("oui") == 0)
                        {
                            currentSong.sendSingers(output);
                            String voiceAnswer = sendQuestion(new String[]{"\nQuelle voix souhaitez-vous desactiver ?"});
                            try{
                                currentSong.getVoice(Integer.parseInt(voiceAnswer)).setEnable(false);
                            }
                            catch(Exception ex)
                            {
                                ex.printStackTrace();
                            }
                        }
                    }

                    String tempoAnswer = sendQuestion(new String[]{"Voulez-vous changer le tempo ? (oui ou non) "});

                    if (tempoAnswer.compareTo("oui") == 0) {
                        try{
                            String tempo = sendQuestion(new String[]{"Entrez le tempo (>0) : "});
                            currentSong.setTempo(Integer.parseInt(tempo));
                            output.println(tempo);
                            output.flush();
                        }
                        catch (Exception ex)
                        {
                            System.out.println(ex.getMessage());
                        }

                    }

                    String velocityAnswer = sendQuestion(new String[]{"Voulez-vous changer la hauteur du morceau ? (oui ou non) "});

                    if (velocityAnswer.compareTo("oui") == 0) {
                        try{
                            String velocity = sendQuestion(new String[]{"Entrez la hauteur (entre -30 et 30) : "});
                            currentSong.setVelocity(Integer.parseInt(velocity));
                            output.println(velocity);
                            output.flush();
                        }
                        catch (Exception ex)
                        {
                            System.out.println(ex.getMessage());
                        }

                    }



                    output.println("Envoi du morceau : ");
                    output.flush();

                    // Envoi des infos concernant le morceau demandé au client
                    ObjectOutputStream out = new ObjectOutputStream(socketClient.getOutputStream());
                    try {

                        out.writeObject(currentSong);
                        out.flush();
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                    finally {
                        if (socketClient.isClosed())
                        {
                            out.close();
                        }
                    }

                    output.println("Tapez 'stop' pour arreter le morceau a tout moment\n");
                    output.flush();

                    output.println("Lancement du morceau : ");
                    output.flush();

                    List<String[]> infoTrack = bestTrack.getInfo();
                    List<String[]> infoUser = user.getInfo();
                    bestTrack.update(infoTrack, currentSong.id);
                    user.update(infoUser,username);

                    File fileRequested = new File(path_input + track_name + ".mid");
                    File copy = new File(path_output + track_name + "_Copie" + ".mid");
                    copy(fileRequested, copy);
                    ObjectOutputStream outFile = new ObjectOutputStream(socketClient.getOutputStream());

                    // Envoi du fichier midi
                    try {
                        outFile.writeObject(fileRequested);
                        outFile.flush();
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                    finally {
                        if (socketClient.isClosed())
                        {
                            outFile.close();
                        }
                    }
                    System.out.println("Morceau envoye");
                    break;

                case "2":
                    System.out.println("Envoi des statistiques au client...");

                    user = new Statistics(new File("src/statistics/user.txt"));
                    bestTrack = new Statistics(new File("src/statistics/track.txt"));
                    List<String> user_info = user.max();
                    List<String> track_info = bestTrack.max();

                    output.println(user_info.get(0) + " est l'utilisateur qui a le plus d'écoutes avec " + user_info.get(1) + " ecoute(s).");
                    output.println("Le morceau " + container.getSong(Integer.parseInt(track_info.get(0))).title + " est le morceau qui a le plus d'écoutes avec " + track_info.get(1) + " ecoute(s).");
                    break;

                case "3":
                    System.out.println("Le client veut se deconnecter");
                    output.flush();
                    connexion = false;
                    break;

                default:
                    //System.out.println("Option inexistante");
                    //output.println("Option inexistante");
                    //output.flush();
                    break;
            }
        }
        output.close();
        socketClient.close();
        System.out.println("Deconnexion du client");
        System.out.println("Traitement client termine !");
    }
}