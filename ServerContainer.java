import java.awt.*;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.*;
import java.util.List;


class ServerContainer implements Serializable {
    transient ServerSocket serverSocket;
    List<Song> songs;

    ServerContainer(ServerSocket socket, List<Song> songs)
    {
        this.serverSocket = socket;
        this.songs = songs;
        initContainer();
    }

    void initContainer()
    {
        List<Voice> voices = new ArrayList<>() {{
            add(new Voice("Black Eyed Peas", 1, Color.blue));
        }};
        addSong(new Song(1, "I gotta Feeling",voices));

        voices = new ArrayList<>() {{
            add(new Voice("Manau", 1, Color.blue));
        }};
        addSong(new Song(2, "La belette", voices));

        voices = new ArrayList<>() {{
            add(new Voice("Luis Fonsi", 1, Color.blue));
            add(new Voice("Daddy Yankee", 2, Color.red));
        }};
        addSong(new Song(3, "Despacito", voices));
    }

    void sendPlaylist(PrintWriter output) {

        output.println("\nVoici la playlist : ");

        int cpt = 0;
        for (Song song : songs)
        {
            cpt = song.id;
            output.println(song.id + " : " + song.title + " de " + song.displaySingers());
            output.flush();
        }

        output.println(cpt + 1 + " : Revenir au menu");
        output.flush();
    }

    void addSong(Song song)
    {
        songs.add(song);
    }

    Song getSong(int id) {
        for (Song song : songs) {
            if (song.id == id)
                return song;
        }
        return null;
    }

}
