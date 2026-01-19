import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Gui extends JFrame implements KeyListener {
    boolean paused = true;
    JLayeredPane layers;
    //Layer panels
    JPanel placementPanel;
    JPanel cardPanel;

    //Card placement hitboxes
    JLabel combinePlacebox;
    JLabel spellPlacebox;
    JLabel elementPlacebox;
    //For checking if a placebox is available
    JLabel combinePlaced;
    JLabel elementPlaced;
    JLabel spellPlaced;
    //ArrayLists
    ArrayList<Card> deck = new ArrayList<>();
    ArrayList<JLabel> hand = new ArrayList<>();

    //Current card
    JLabel currentLabel;
    Rectangle collision;
    int collisionType;//1 or 2
    //Element arrays
    Element[] fireElements = {Element.FIRE, Element.BLAZE, Element.SCORCH,Element.PLASMA,Element.STEAM,Element.LAVA,Element.GLASS,Element.METAL,Element.BLOOD};
    Element[] waterElements = {Element.WATER,Element.STEAM, Element.ICE, Element.STORM,Element.WOOD,Element.TSUNAMI,Element.SLIME,Element.GLACIER,Element.BLOOD};
    Element[] earthElements = {Element.EARTH,Element.WOOD, Element.LAVA, Element.SAND,Element.MAGNET,Element.MOUNTAIN,Element.GLASS,Element.METAL,Element.GLACIER};
    Element[] lightingElements = {Element.LIGHTNING,Element.VOLT, Element.MAGNET, Element.STORM,Element.PLASMA,Element.QUICK,Element.SLIME,Element.METAL,Element.BLOOD};
    Element[] windElements = {Element.WIND,Element.GUST, Element.SCORCH, Element.ICE,Element.SAND,Element.QUICK,Element.GLASS,Element.SLIME,Element.GLACIER};
    //Element types
    Element[] physicalElements = {Element.FIRE,Element.WATER,Element.MOUNTAIN,Element.TSUNAMI, Element.QUICK, Element.WOOD, Element.ICE, Element.SAND, Element.LAVA, Element.GLASS, Element.SLIME,Element.METAL,Element.GLACIER};
    Element[] specialElements ={Element.FIRE,Element.WIND,Element.LIGHTNING,Element.BLAZE,Element.GUST,Element.VOLT,Element.STEAM,Element.SCORCH, Element.STORM,Element.PLASMA,Element.BLOOD,Element.MAGNET};
    //Element combinations
    Element[] singleElements = {Element.FIRE,Element.WATER,Element.EARTH,Element.LIGHTNING,Element.WIND};
    Element[] tripleElements = {Element.GLACIER,Element.BLOOD,Element.GLASS,Element.SLIME,Element.METAL};
    ElementCombinations elementCombinations = new ElementCombinations();
    //Buy stage
    JPanel buyPanel;
    int difficulty;
    int playerHealth;
    int defense;
    Element buff;
    int buffDuration;

    boolean playerTurn;
    JButton attackButton;
    Enemy currentEnemy;

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

        combinePlacebox = new JLabel("Combine", SwingConstants.CENTER);
        spellPlacebox = new JLabel("Spell", SwingConstants.CENTER);
        elementPlacebox = new JLabel("Element", SwingConstants.CENTER);

        for (JLabel i : new JLabel[]{combinePlacebox, spellPlacebox, elementPlacebox}) {
            i.setPreferredSize(boxSize);
            i.setMaximumSize(boxSize);
            i.setOpaque(true);
            i.setBackground(Color.BLACK);
            i.setForeground(Color.WHITE);
        }
        //Add attackButton
        attackButton = new JButton("ATTACK");
        attackButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        attackButton.setForeground(Color.WHITE);
        attackButton.setBackground(Color.BLACK);

        attackButton.setFocusPainted(false);
        attackButton.setBorderPainted(false);
        attackButton.setContentAreaFilled(true);
        attackButton.setOpaque(true);
        Dimension attackButtonSize = new Dimension(200, 100);
        attackButton.setPreferredSize(attackButtonSize);
        attackButton.setMaximumSize(attackButtonSize);

        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Checks if it is the players turn to move and if both card slots are filled
                if(playerTurn&&elementPlaced!=null&&spellPlaced!=null)
                {
                    String elementString = getTypeString(elementPlaced);
                    Element element = Element.valueOf(elementString.trim().toUpperCase());
                    String spellString = getTypeString(spellPlaced);
                    Spell spell = Spell.valueOf(spellString.trim().toUpperCase());
                    //Stop combined elements while using a buff spell
                    if(spell==Spell.BuffSpell)
                    {
                        if(Arrays.asList(singleElements).contains(element))
                        {
                            //Start player turn
                            playerAttack();
                        }
                    }
                    else
                    {
                        //Start player turn
                        playerAttack();
                    }
                }
            }
        });
        //Center horizontally using glue and struts
        horizontalWrapper.add(Box.createHorizontalGlue());
        horizontalWrapper.add(combinePlacebox);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(elementPlacebox);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(spellPlacebox);
        horizontalWrapper.add(Box.createHorizontalStrut(150));
        horizontalWrapper.add(attackButton);
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
        //Buy panel
        buyPanel = new JPanel();
        layers.add(buyPanel,JLayeredPane.POPUP_LAYER);
        buyPanel.setOpaque(false);
        buyPanel.setBounds(0,0,layers.getWidth(),layers.getHeight());
        buyPanel.setLayout(new BoxLayout(buyPanel, BoxLayout.X_AXIS));
        setVisible(true);

        //Set timer to check collisions
        Timer timer = new Timer(16, e -> update()); // ~60 FPS
        timer.start();

        Card fire = new ElementCard(Element.FIRE);
        Card water = new ElementCard(Element.WATER);
        Card earth = new ElementCard(Element.EARTH);
        Card wind = new ElementCard(Element.WIND);
        Card lighting = new ElementCard(Element.LIGHTNING);
        deck.add(fire);
        deck.add(water);
        deck.add(earth);
        deck.add(wind);
        deck.add(lighting);

        setVisible(true);

        difficulty = 1;
        playerHealth=100;
        defense=0;
        buff=null;

        startBattle();
    }
    public void startBattle()
    {
        currentEnemy = createRandomEnemy(difficulty);
        drawHand();
        playerTurn = true;
    }
    public void enemyAttack()
    {
        //Always protects from damage
        playerTurn=false;
        if(defense>0)
        {
            defense-=currentEnemy.damage();
        }
        else {
            playerHealth-= currentEnemy.damage();
        }
        checkResult();
    }
    public void playerAttack()
    {
        playerTurn=true;
        int damage_ = 10;
        int defense_ = 10;
        int combineLevel;
        Element buff_=null;
        if(buffDuration>0) {
            buffDuration-=1;
        }
        else {
            buffDuration=0;
        }

        String elementString = getTypeString(elementPlaced);
        Element element = Element.valueOf(elementString.trim().toUpperCase());
        String spellString = getTypeString(spellPlaced);
        Spell spell = Spell.valueOf(spellString.trim().toUpperCase());
        //Combination level
        if(Arrays.asList(tripleElements).contains(element))
        {
            combineLevel=3;
        }
        else if(!Arrays.asList(singleElements).contains(element))
        {
            combineLevel=2;
        }
        else
        {
            combineLevel=1;
        }
        //Spell type and calculations
        if(spell == Spell.PhysicalSpell||spell == Spell.ElementalSpell)
        {
            //combination damage_
            if(combineLevel==2)
            {
                damage_ *=2;
            }
            if(combineLevel==3)
            {
                damage_ *=5;
            }
            //Spell matching element
            if(spell == Spell.PhysicalSpell&&Arrays.asList(physicalElements).contains(element)) {
                damage_ *=2;
            }
            else if(Arrays.asList(specialElements).contains(element)){
                damage_ *=2;
            }
            //Buffs
            if(buffDuration>0)
            {
                switch (buff_) {
                    case FIRE -> {
                        if(Arrays.asList(fireElements).contains(element)) {
                            damage_ *=2;
                        }
                    }
                    case WATER -> {
                        if(Arrays.asList(waterElements).contains(element)) {
                            damage_ *=2;
                        }
                    }
                    case EARTH -> {
                        if(Arrays.asList(earthElements).contains(element)) {
                            damage_ *=2;
                        }
                    }
                    case WIND -> {
                        if(Arrays.asList(windElements).contains(element)) {
                            damage_ *=2;
                        }
                    }
                    case LIGHTNING -> {
                        if(Arrays.asList(lightingElements).contains(element)) {
                            damage_ *=2;
                        }
                    }
                    default->{

                    }
                }
            }
            //Enemy weaknesses
            if(currentEnemy.getSpellWeakness()==spell)
            {
                damage_ *=2;
            }
            switch (currentEnemy.getElementalWeakness()) {
                case FIRE -> {
                    if (Arrays.asList(fireElements).contains(element)) {
                        damage_ *=3;
                    }
                }
                case WATER -> {
                    if (Arrays.asList(waterElements).contains(element)) {
                        damage_ *=3;
                    }
                }
                case EARTH -> {
                    if (Arrays.asList(earthElements).contains(element)) {
                        damage_ *=3;
                    }
                }
                case WIND -> {
                    if (Arrays.asList(windElements).contains(element)) {
                        damage_ *=3;
                    }
                }
                case LIGHTNING -> {
                    if (Arrays.asList(lightingElements).contains(element)) {
                        damage_ *=3;
                    }
                }
            }

        }
        else if(spell == Spell.BlockSpell)
        {
            //Combination bonus
            if(combineLevel==2)
            {
                defense_*=2;
            }
            if(combineLevel==3)
            {
                defense_*=5;
            }
            if(Arrays.asList(physicalElements).contains(element))
            {
                defense_*=3;
            }
        }
        else//buff
        {
            buffDuration = 2;
            switch (element) {
                case FIRE -> {
                    buff_ =Element.FIRE;
                }
                case WATER -> {
                    buff_ =Element.WATER;
                }
                case EARTH -> {
                    buff_ =Element.EARTH;
                }
                case WIND -> {
                    buff_ =Element.WIND;
                }
                case LIGHTNING -> {
                    buff_ =Element.LIGHTNING;
                }
                default->{
                    buff_=null;
                }
            }
        }
        defense += defense_;
        buff=buff_;

        checkResult();
    }
    public void checkResult()
    {
        if(currentEnemy.gethealth()<=0)
        {
            buyStage();
        }
        else if(playerHealth<=0)
        {
            //Highscore
        }
        else
        {
            if(playerTurn=true)
            {
                enemyAttack();
            }
            else
            {
                playerAttack();
            }

        }

    }
    public void buyStage()
    {

    }



    public Enemy createRandomEnemy(int difficulty)
    {
        int randomNum = (int)(Math.random() * 5) + 1;
        Element elementalWeakness;
        switch (randomNum) {
            case 1 -> elementalWeakness = Element.FIRE;
            case 2 -> elementalWeakness = Element.WATER;
            case 3 -> elementalWeakness = Element.EARTH;
            case 4 -> elementalWeakness = Element.WIND;
            case 5 -> elementalWeakness = Element.LIGHTNING;
            default -> elementalWeakness = null;
        }

        Spell spellWeakness;
        if (Math.random() < 0.5) {
            spellWeakness = Spell.PhysicalSpell;
        } else {
            spellWeakness = Spell.ElementalSpell;
        }
        int health = (int)(Math.random()*difficulty);
        int damage = (int)(Math.random()*difficulty);

        return new Enemy(elementalWeakness, spellWeakness, health, damage);
    }

    public void update() {
        if (currentLabel == null) {
            return;
        }
        Rectangle cl = SwingUtilities.convertRectangle(
                currentLabel.getParent(),
                currentLabel.getBounds(),
                layers
        );

        Rectangle pb1 = SwingUtilities.convertRectangle(
                combinePlacebox.getParent(),
                combinePlacebox.getBounds(),
                layers
        );
        Rectangle pb2 = SwingUtilities.convertRectangle(
                elementPlacebox.getParent(),
                elementPlacebox.getBounds(),
                layers
        );
        Rectangle pb3 = SwingUtilities.convertRectangle(
                spellPlacebox.getParent(),
                spellPlacebox.getBounds(),
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
                    if(combinePlaced ==card)
                    {
                        combinePlaced =null;
                    }
                    else if(elementPlaced ==card)
                    {
                        elementPlaced =null;
                    }
                    else if(spellPlaced ==card)
                    {
                        spellPlaced =null;
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
                    char CardType = card.getText().charAt(0);
                    Point combinePBoxLocation = SwingUtilities.convertPoint(combinePlacebox.getParent(), combinePlacebox.getLocation(), layers);
                    Point elementPBoxLocation = SwingUtilities.convertPoint(elementPlacebox.getParent(), elementPlacebox.getLocation(), layers);
                    Point spellPBoxLocation = SwingUtilities.convertPoint(spellPlacebox.getParent(), spellPlacebox.getLocation(), layers);
                    if (collisionType==1&&CardType=='1') {
                        if(combinePlaced!=null)
                        {
                            String element1 = getTypeString(combinePlaced);
                            String element2 = getTypeString(card);
                            Element e1 = Element.valueOf(element1.trim().toUpperCase());
                            Element e2 = Element.valueOf(element2.trim().toUpperCase());
                            Element combinedElement = elementCombinations.combine(e1, e2);

                            if(combinedElement!=null)
                            {
                                cardPanel.remove(combinePlaced);
                                cardPanel.revalidate();
                                cardPanel.repaint();

                                //Set new type in string by converting the two strings into elements and then getting the combined element
                                card.setText("1"+combinedElement.name());
                                combinePlaced=card;
                                card.setLocation(combinePBoxLocation);
                            }
                            else {
                                hand.add(card);
                                refreshHand();
                            }
                        }
                        else
                        {
                            combinePlaced = card;
                            card.setLocation(combinePBoxLocation);
                        }
                    }
                    else if(collisionType==2&&CardType=='1'&& elementPlaced ==null) {
                        elementPlaced =card;
                        card.setLocation(elementPBoxLocation);
                    }
                    else if(collisionType==3&&CardType=='2'&& spellPlaced ==null) {
                        spellPlaced =card;
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
    public String getTypeString(JLabel card)
    {
        if(card==null) return null;
        return card.getText().substring(1);
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