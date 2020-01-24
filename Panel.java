import javax.swing.*;
import java.awt.*;

class Panel extends JPanel {

    int width;
    int length;
    int space;
    String lyrics;
    Voice voice;

    Panel(int width, int length, int space, String lyrics, Voice voice) {
        this.width = width;
        this.length = length;
        this.space = space;
        this.lyrics = lyrics;
        this.voice = voice;
    }

    @Override
    public void paintComponent(Graphics g){
        Font font = new Font("Courier", Font.BOLD, 20);

        if (voice != null)
        {
            if (voice.isEnable())
            {
                g.setColor(voice.color);
                drawCenteredString(g, font);
            }
        }
        else
        {
            g.setColor(Color.red);
            drawCenteredString(g, font);
        }
    }

    void drawCenteredString(Graphics g, Font font) {
        length += space;
        FontMetrics metrics = g.getFontMetrics(font);
        int x = (width - metrics.stringWidth(lyrics))/ 2;
        g.setFont(font);
        g.drawString(lyrics, x, space);
    }

}


