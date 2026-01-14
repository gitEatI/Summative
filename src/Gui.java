import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Gui extends JFrame implements KeyListener {
    boolean paused = true;
    JLayeredPane layers;
    //Layer panels
    JPanel placementPanel;
    JPanel cardPanel;

    //Card placement hitboxes
    JLabel elementPlacebox;
    JLabel spellPlacebox;
    JLabel elementPlacebox2;

    //ArrayLists
    ArrayList<Card> deck;
    ArrayList<Card> hand;

    //Current card
    JLabel currentLabel;
    Rectangle collision;

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
        layers.add(cardPanel,JLayeredPane.MODAL_LAYER);

        //Set placement layer
        placementPanel =  new JPanel();
        placementPanel.setOpaque(false);
        placementPanel.setBounds(0,0,layers.getWidth(),layers.getHeight());
        placementPanel.setLayout(new BorderLayout());
        layers.add(placementPanel,JLayeredPane.PALETTE_LAYER);

        //Card placement hitboxes

        //Align the hitboxes on the right side
        JPanel horizontalWrapper = new JPanel();
        horizontalWrapper.setOpaque(false);
        horizontalWrapper.setLayout(new BoxLayout(horizontalWrapper, BoxLayout.X_AXIS));

        // Create all the placeboxes
        Dimension boxSize = new Dimension(200, 300);

        elementPlacebox = new JLabel("Element", SwingConstants.CENTER);
        spellPlacebox = new JLabel("Spell", SwingConstants.CENTER);
        elementPlacebox2 = new JLabel("Element", SwingConstants.CENTER);

        for (JLabel box : new JLabel[]{elementPlacebox, spellPlacebox, elementPlacebox2}) {
            box.setPreferredSize(boxSize);
            box.setMaximumSize(boxSize);
            box.setOpaque(true);
            box.setBackground(Color.BLACK);
            box.setForeground(Color.WHITE);
        }

        //Center horizontally using glue and struts
        horizontalWrapper.add(Box.createHorizontalGlue());
        horizontalWrapper.add(elementPlacebox);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(elementPlacebox2);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(spellPlacebox);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(Box.createHorizontalGlue());

        //Center on the vertically
        JPanel verticalWrapper = new JPanel();
        verticalWrapper.setOpaque(false);
        verticalWrapper.setLayout(new BoxLayout(verticalWrapper, BoxLayout.Y_AXIS));
        verticalWrapper.add(Box.createVerticalGlue());
        verticalWrapper.add(horizontalWrapper);
        verticalWrapper.add(Box.createVerticalGlue());

        placementPanel.add(verticalWrapper, BorderLayout.EAST);
        //Set timer to check collisions
        Timer timer = new Timer(16, e -> update()); // ~60 FPS
        timer.start();

        Card fire = new ElementCard(Element.FIRE);
        createCard(fire);
        setVisible(true);
    }
    private void update() {
        if (currentLabel == null) {
            return;
        }
        Rectangle cl = SwingUtilities.convertRectangle(
                currentLabel.getParent(),
                currentLabel.getBounds(),
                layers
        );

        Rectangle pb1 = SwingUtilities.convertRectangle(
                elementPlacebox.getParent(),
                elementPlacebox.getBounds(),
                layers
        );
        Rectangle pb2 = SwingUtilities.convertRectangle(
                spellPlacebox.getParent(),
                spellPlacebox.getBounds(),
                layers
        );
        Rectangle pb3 = SwingUtilities.convertRectangle(
                elementPlacebox2.getParent(),
                elementPlacebox2.getBounds(),
                layers
        );
        if (cl.intersects(pb1)) {
            collision =pb1;
        }
        else if(cl.intersects(pb2))
        {
            collision =pb2;
        }
        else if(cl.intersects(pb3))
        {
            collision =pb3;
        }
        repaint();

    }


    public void createCard(Card c)
    {
        //Label and settings
        JLabel card = new JLabel();
        card.setBounds(0,0,200,300);
        card.setBackground(Color.BLACK);
        card.setForeground(Color.WHITE);
        card.setOpaque(true);
        cardPanel.add(card);
        card.setVisible(true);
        this.repaint();
        //Drag variables
        boolean dragable = true;
        //Dragging
        Point clickOffset = new Point();
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentLabel=card;
                clickOffset.x = e.getX();
                clickOffset.y = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if(dragable==true) {
                    int x = card.getX() + e.getX() - clickOffset.x;
                    int y = card.getY() + e.getY() - clickOffset.y;
                    card.setLocation(x, y);
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(
                        card,
                        e.getPoint(),
                        layers
                );

                if (collision != null && collision.contains(p)) {

                }
                else {

                }
            }
        };
        card.addMouseListener(dragAdapter);
        card.addMouseMotionListener(dragAdapter);
        //Card information
        if (c instanceof ElementCard ec) {
            card.setText(ec.getElement().name());
            card.setHorizontalAlignment(SwingConstants.CENTER);
            card.setVerticalAlignment(SwingConstants.CENTER);
        }
        else if(c.cardType==2){

        }
        else if(c.cardType==3) {

        }

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
                pauseScreen.setBackground(new Color(213, 25, 25, 150));
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