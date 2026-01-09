import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

public class Gui extends JFrame implements KeyListener{
    boolean paused = true;
    //background
    public Gui() {
        //Set frame
        addKeyListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        setUndecorated(true);
        setLayout(null);

        //Make it full screen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);
        this.setFocusable(true);
        this.requestFocusInWindow();

        //Layered pane
        JLayeredPane layers = new JLayeredPane();
        layers.setBounds(0, 0, getWidth(), getHeight());
        layers.setLayout(null);
        this.add(layers);

        //Set background
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/Grid.png"));
        JLabel background = new JLabel(backgroundImage);
        background.setBounds(0, 0, getWidth(), getHeight());
        layers.add(background,JLayeredPane.DEFAULT_LAYER);

        //createCard();

        setVisible(true);
    }
    public void createCard()
    {
        JLabel card = new JLabel();
        card.setPreferredSize(new Dimension(10, 10));
        card.setLocation(0,0);
        card.setBackground(Color.BLACK);
        card.setOpaque(true);
        this.add(card);
        this.repaint();
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_ESCAPE) {
            if(paused==true)
            {
                //Set a transparent panel
                JPanel pauseScreen = new JPanel();
                this.add(pauseScreen,BorderLayout.CENTER);
                pauseScreen.setOpaque(false);
                pauseScreen.setBackground(new Color(0, 0, 0, 150));
                pauseScreen.setSize(300, 200);
                pauseScreen.setLocation(this.getX(), this.getY());
                pauseScreen.setVisible(true);

                paused=false;
            }
            else
            {

                paused=true;
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

//    @Override
//    public void actionPerformed(ActionEvent e) {
//
//    }

}