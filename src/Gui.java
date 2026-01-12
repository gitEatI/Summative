import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class Gui extends JFrame implements KeyListener {
    boolean paused = true;
    JLayeredPane layers;
    JPanel cardPanel;
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
        layers = new JLayeredPane();
        layers.setBounds(0, 0, getWidth(), getHeight());
        layers.setLayout(null);
        this.add(layers);

        //Set background
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/Grid.png"));
        JLabel background = new JLabel(backgroundImage);
        background.setBounds(0, 0, getWidth(), getHeight());
        layers.add(background,JLayeredPane.DEFAULT_LAYER);

        //Set Card layer
        cardPanel =  new JPanel();
        cardPanel.setOpaque(false);
        cardPanel.setBounds(0,0,layers.getWidth(),layers.getHeight());
        cardPanel.setLayout(null);
        layers.add(cardPanel,JLayeredPane.PALETTE_LAYER);


        createCard();

        setVisible(true);
    }
    public void createCard()
    {
        JLabel card = new JLabel();
        card.setBounds(0,0,200,300);
        card.setBackground(Color.BLACK);
        card.setOpaque(true);
        cardPanel.add(card);
        card.setVisible(true);
        this.repaint();
        //Dragging
        Point clickOffset = new Point();
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickOffset.x = e.getX();
                clickOffset.y = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = card.getX() + e.getX() - clickOffset.x;
                int y = card.getY() + e.getY() - clickOffset.y;
                card.setLocation(x, y);
            }
        };

        card.addMouseListener(dragAdapter);
        card.addMouseMotionListener(dragAdapter);

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

}