import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

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
    //For checking if a placebox is available
    JLabel elementPlaced1;
    JLabel elementPlaced2;
    JLabel spellPlaced1;
    //ArrayLists
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<JLabel> hand = new ArrayList<>();

    //Current card
    JLabel currentLabel;
    Rectangle collision;
    int collisionType;//1 2 or 3

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

        for (JLabel i : new JLabel[]{elementPlacebox, spellPlacebox, elementPlacebox2}) {
            i.setPreferredSize(boxSize);
            i.setMaximumSize(boxSize);
            i.setOpaque(true);
            i.setBackground(Color.BLACK);
            i.setForeground(Color.WHITE);
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
        Card water = new ElementCard(Element.WATER);
        Card earth = new ElementCard(Element.EARTH);
        deck.add(fire);
        deck.add(water);
        deck.add(earth);
        drawHand();
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
        Rectangle pb3 = SwingUtilities.convertRectangle(
                spellPlacebox.getParent(),
                spellPlacebox.getBounds(),
                layers
        );
        Rectangle pb2 = SwingUtilities.convertRectangle(
                elementPlacebox2.getParent(),
                elementPlacebox2.getBounds(),
                layers
        );
        if (cl.intersects(pb1)) {
            collision = pb1;
            collisionType = 1;
        }
        else if(cl.intersects(pb2))
        {
            collision = pb2;
            collisionType = 2;
        }
        else if(cl.intersects(pb3))
        {
            collision = pb3;
            collisionType = 3;
        }
        repaint();
    }
    public void refreshHand()
    {
        Point p = new Point(0,cardPanel.getHeight()-300);
        for(JLabel i : hand)
        {
            i.setLocation(p);
            p.x+=220;
        }
    }
    public void drawHand()
    {
        ArrayList<Card> deckCopy = new ArrayList<>(deck);
        hand.clear();
        for(int i = 0; i<5 && !deckCopy.isEmpty();i++)
        {
            Random randomNum = new Random();
            int rn = randomNum.nextInt(deckCopy.size());
            createCard(deckCopy.get(rn));
            deckCopy.remove(rn);
        }
        refreshHand();
    }

    public void createCard(Card c)
    {
        //Label and text info

        JLabel card;
        if (c instanceof ElementCard elementCard) {
            card = new JLabel(""+c.getCardType()+elementCard.getElement());
        }
        else  if (c instanceof SpellCard spellCard) {
            card = new JLabel(""+c.getCardType()+spellCard.getSpellType());
        }
        //else if(c.getCardType()==3) {}
        else
        {
            card = new JLabel();
        }

        card.setBounds(0,0,200,300);
        card.setBackground(Color.BLACK);
        card.setForeground(Color.WHITE);
        card.setOpaque(true);
        cardPanel.add(card);
        card.setVisible(true);
        this.repaint();
        //Add to hand
        hand.add(card);
        refreshHand();
        //Dragging
        Point clickOffset = new Point();
        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) { // left click only
                    currentLabel = card;
                    clickOffset.x = e.getX();
                    clickOffset.y = e.getY();
                    hand.remove(card);
                    if(elementPlaced1==card)
                    {
                        elementPlaced1=null;
                    }
                    else if(elementPlaced2==card)
                    {
                        elementPlaced2=null;
                    }
                    else if(spellPlaced1==card)
                    {
                        spellPlaced1=null;
                    }
                    refreshHand();
                }
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentLabel == card && (e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) != 0) {
                    int x = card.getX() + e.getX() - clickOffset.x;
                    int y = card.getY() + e.getY() - clickOffset.y;
                    card.setLocation(x, y);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentLabel != card || !SwingUtilities.isLeftMouseButton(e)) return;
                Point p = SwingUtilities.convertPoint(
                        card,
                        e.getPoint(),
                        layers
                );

                if (collision != null && collision.contains(p)) {
                    //String rest = str.substring(1);
                    char CardType = card.getText().charAt(0);
                    Point elementPBox1Location = SwingUtilities.convertPoint(elementPlacebox.getParent(), elementPlacebox.getLocation(), layers);
                    Point elementPBox2Location = SwingUtilities.convertPoint(elementPlacebox2.getParent(), elementPlacebox2.getLocation(), layers);
                    Point spellPBoxLocation = SwingUtilities.convertPoint(spellPlacebox.getParent(), spellPlacebox.getLocation(), layers);
                    if (collisionType==1&&CardType=='1'&&elementPlaced1==null) {
                        elementPlaced1=card;
                        card.setLocation(elementPBox1Location);
                    }
                    else if(collisionType==2&&CardType=='1'&&elementPlaced2==null) {
                        elementPlaced2=card;
                        card.setLocation(elementPBox2Location);
                    }
                    else if(collisionType==3&&CardType=='2'&&spellPlaced1==null) {
                        spellPlaced1=card;
                        card.setLocation(spellPBoxLocation);
                    }
                    else {
                        //Add back to hand
                        hand.add(card);
                        refreshHand();
                    }
                }
                else {
                    // Add back to hand
                    hand.add(card);
                    refreshHand();
                }

                currentLabel = null;
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