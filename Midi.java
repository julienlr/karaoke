import java.io.File;
import javax.sound.midi.*;
import javax.sound.midi.Track;


class Midi {

    private Sequence sequence;
    Sequencer sequencer;
    Window window;
    String sentence;
    Song receivedSong;

    Midi(File file, Window window, Song song) throws Throwable {
        sequence = MidiSystem.getSequence(file);
        this.window = window;
        this.receivedSong = song;
    }


    private void init() throws Throwable {
        if (sequencer == null) {
            sequencer = MidiSystem.getSequencer();
            window.setTitle(receivedSong.title + " de " + receivedSong.displaySingers());
        }
    }

    public void start() throws Throwable {
        init();
        sequencer.open();
        sequencer.setSequence(sequence);
        listener(sequencer);
        if(receivedSong.velocity!=-50){
            changeVelocity();
        }
        sequencer.start();
    }

    public void stop() {
        sequencer.close();
        receivedSong.updateParameters();
        window.setVisible(false);
    }

    void changeVelocity() throws InvalidMidiDataException {
        for(Track track : sequence.getTracks()) {
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                if(event.getMessage() instanceof ShortMessage) {
                    ShortMessage msg = (ShortMessage)event.getMessage();
                    //si le shortMessage correspond à la hauteur
                    if(msg.getCommand() == 0x80 || msg.getCommand() == 0x90) {
                        msg.setMessage(msg.getCommand(), msg.getChannel(), msg.getData1() + receivedSong.velocity, msg.getData2());
                    }
                }
            }
        }
        sequencer.setSequence(sequence);
    }

    protected void displayLyrics(MetaMessage message)
    {
        Panel panel;

        try{
            // Extrait les données des évènements midi
            String msg = decodeMessage(message);
            if (message.getType() == 5)
            {
                // Remet à jour la fenetre
                window.repaint();
                window.revalidate();
                // Remise à jour de la voix et de la phrase courante
                String m = receivedSong.updateVoice(msg);
                setSentence(m);
                // Récupération du nouveau panel
                panel = new Panel(1000, 70, 130, sentence, receivedSong.currentVoice);
                panel.setVisible(true);
                window.setContentPane(panel);
                window.setVisible(true);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    protected void setSentence(String message){
        // Récupère le caractère de fin de phrase
        if (message.contains(new String(new char[]{13})))
        {
            sentence = "";
        }
        else {
            sentence = sentence + message;
        }
    }

    private void listener(final Sequencer sequencer){
        sequencer.addMetaEventListener(new MetaEventListener() {

            public void meta(MetaMessage message) {

                displayLyrics(message);

                // Fin du morceau
                if (message.getType() == 47) {
                    stop();
                }
            }
        });
    }

    // Permet d'associer les messages du fichier à leur type correspondant et de renvoyer leur contenu
    public String decodeMessage(MetaMessage message) {

        byte[] data = message.getData();
        String msg = null;

        switch (message.getType()) {
            case 1:
                String text = new String(data);
                msg = "Texte : " + text;
                break;

            case 2:
                String cp = new String(data);
                msg = "Copyright : " + cp;
                break;

            case 3:
                String trackName = new String(data);
                msg = "Nom du morceau : " + trackName;
                break;

            case 4:
                String instrument = new String(data);
                msg = "Nom de l'instrument : " + instrument;
                break;

            case 5:
                msg = new String(data);
                break;

            case 0x51:
                if(receivedSong.tempo > 0){
                    sequencer.setTempoInBPM(receivedSong.tempo);
                }
                break;

            case 0x2F:
                msg = "Fin du Morceau";
                break;
        }
        return msg;
    }
}