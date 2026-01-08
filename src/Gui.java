import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {

    public Gui() {

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        setUndecorated(true);

        // Make it full screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);
    }
}