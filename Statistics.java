import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Statistics {

    File file;

    Statistics(File file)
    {
        this.file = file;
    }

    void update(List<String[]> lines, int track) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        int i = 1;
        for(String[] s : lines){
            if(i == track){
                writer.print(s[0]);
                writer.print(" ");
                writer.print(Integer.parseInt(s[1])+1);
                writer.println();
            }else{
                writer.print(s[0]);
                writer.print(" ");
                writer.print(s[1]);
                writer.println();
            }
            i++;

        }
        writer.close();
    }

    void update(List<String[]> lines, String name) throws IOException {
        PrintWriter writer = new PrintWriter(file);

        for(String[] s : lines){
            if(s[0].compareTo(name)==0){
                writer.print(s[0]);
                writer.print(" ");
                writer.print(Integer.parseInt(s[1])+1);
                writer.println();
            }else{
                writer.print(s[0]);
                writer.print(" ");
                writer.print(s[1]);
                writer.println();
            }

        }
        writer.close();
    }

    List<String[]> getInfo() throws IOException {

        BufferedReader br;
        List<String[]> res = new ArrayList<>();
        br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        while(line!=null){
            String[] word = line.split(" ");
            res.add(word);
            line = br.readLine();
        }
        br.close();
        return res;
    }

    boolean getUsername(String username) throws IOException {
        BufferedReader br;

        br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        while(line!=null){
            String[] word = line.split(" ");
            if(word[0].compareTo(username)==0)
                return true;
            line = br.readLine();
        }

        br.close();
        return false;
    }

    List<String> max() throws IOException {
        BufferedReader br;
        List<String> res = new ArrayList<>();

        br = new BufferedReader(new FileReader(file));
        String line = br.readLine();

        String name=null;
        String nblisten=null;
        int nb = -1;
        while(line!=null){
            String[] word = line.split(" ");
            int rep = Integer.parseInt(word[1]);
            if( rep > nb) {
                nb = rep;
                name = word[0];
                nblisten = word[1];
            }
            line = br.readLine();
        }

        res.add(name);
        res.add(nblisten);

        br.close();
        return res;

    }
}
