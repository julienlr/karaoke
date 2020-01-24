import java.io.PrintWriter;
import java.io.Serializable;
import java.util.*;

class Song implements Serializable {
    int id;
    String title;
    List<Voice> voices;
    Voice currentVoice;
    int tempo = -1;
    int velocity = -50;

    Song(int id, String title, List<Voice> voices) {
        this.id = id;
        this.title = title;
        this.voices = voices;
    }

    String displaySingers()
    {
        StringBuilder singers = new StringBuilder();

        for ( int cpt = 0; cpt < voices.size(); cpt++)
        {
            if (cpt == 0)
            {
                singers.append(voices.get(cpt).name);
            }
            else
            {
                singers.append(" & ").append(voices.get(cpt).name);
            }

        }

        return singers.toString();
    }

    void sendSingers(PrintWriter output) {

        output.println("\nLes chanteurs sont : ");

        for (Voice v : voices)
        {
            output.println(v.id + " : " + v.name);
            output.flush();

        }
    }

    Voice getVoice(int id)
    {
        for(Voice v : voices)
        {
            if (v.id == id)
            {
                return v;
            }
        }

        return null;
    }

    String updateVoice(String message){
        try{
            if (message.contains("V1"))
            {
                this.currentVoice = this.getVoice(1);
                message = message.split("&")[1];
            }
            else if (message.contains("V2"))
            {
                this.currentVoice = this.getVoice(2);
                message = message.split("&")[1];
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
        return message;

    }

    void updateParameters()
    {
        setTempo(-1);
        setVelocity(-50);
        for(Voice v : voices)
        {
            v.setEnable(true);
        }
    }

    int getTempo() {
        return tempo;
    }

    void setTempo(int tempo) {
        this.tempo = tempo;
    }

    int getVelocity() {
        return velocity;
    }

    void setVelocity(int velocity) {
        this.velocity = velocity;
    }
}
