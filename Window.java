import javax.swing.* ;

class Window extends JFrame {
    Window(int width, int height){
        setSize(width,height);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
