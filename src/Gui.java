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
    JPanel flowPanel;
    int difficulty;
    int coins;
    int playerHealth;
    int defense;
    Element buff;
    int buffDuration;

    boolean playerTurn;
    JButton attackButton;
    Enemy currentEnemy;

    JPanel statsPanel;
    JLabel statsLabel;
    public Gui() {
        //Set frame
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        addKeyListener(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        setLayout(null);

        gd.setFullScreenWindow(null);
        dispose();
        setUndecorated(true); // Remove title bar (hide X)
        setVisible(true);
        gd.setFullScreenWindow(this); // Fullscreen
        paused=false;

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
                if(playerTurn && elementPlaced != null && spellPlaced != null)
                {
                    String elementString = getTypeString(elementPlaced);
                    Element element = Element.valueOf(elementString.trim().toUpperCase()); // ADD toUpperCase()

                    String spellString = getTypeString(spellPlaced);
                    Spell spell = Spell.valueOf(spellString.trim()); // DON'T add toUpperCase() - Spell is MixedCase

                    //Stop combined elements while using a buff spell
                    if(spell == Spell.BuffSpell)
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

//Stats panel
        statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        statsLabel = new JLabel("Stats");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Increase font size
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setBackground(Color.BLACK);
        statsLabel.setOpaque(true);

// Add vertical spacing with glue
        statsPanel.add(Box.createVerticalGlue()); // Pushes content to top
        statsPanel.add(Box.createVerticalStrut(20)); // Space from top
        statsPanel.add(statsLabel);
        statsPanel.add(Box.createVerticalGlue()); // Pushes content to top

        placementPanel.add(statsPanel, BorderLayout.WEST);

        //Flow panel
        flowPanel = new JPanel();
        layers.add(flowPanel, JLayeredPane.POPUP_LAYER);
        flowPanel.setOpaque(false);
        flowPanel.setBounds(0, 0, layers.getWidth(), layers.getHeight());
        flowPanel.setLayout(new FlowLayout());
        flowPanel.setBackground(Color.white);
        flowPanel.setVisible(false); // Hide it initially

        //Set timer to check collisions
        Timer timer = new Timer(16, e -> update()); // ~60 FPS
        timer.start();

        Card wind = new ElementCard(Element.WIND);
        Card earth = new ElementCard(Element.EARTH);
        Card lighting = new ElementCard(Element.LIGHTNING);
        Card water = new ElementCard(Element.WATER);
        Card fire = new ElementCard(Element.FIRE);

        Card physicalSpell = new SpellCard(Spell.PhysicalSpell);
        Card elementalSpell = new SpellCard(Spell.ElementalSpell);
        Card buffSpell = new SpellCard(Spell.BuffSpell);
        Card blockSpell = new SpellCard(Spell.BlockSpell);
        deck.add(earth);
        deck.add(lighting);
        deck.add(wind);
        deck.add(water);
        deck.add(fire);
        deck.add(physicalSpell);
        deck.add(elementalSpell);
        deck.add(buffSpell);
        deck.add(blockSpell);

        setVisible(true);

        difficulty = 1;
        playerHealth=100;
        defense=0;
        buff=null;
        drawHand();
        startBattle();
    }
    public void startBattle()
    {
        flowPanel.setVisible(false);
        currentEnemy = createRandomEnemy(difficulty);
        rewriteStats();
        playerTurn = true;

        this.requestFocusInWindow();
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
        drawHand();
        playerTurn = true;
        int damage_ = 1;
        int defense_ = 10;
        int combineLevel;
        Element buff_ = null;

        if(buffDuration > 0) {
            buffDuration -= 1;
        }
        else {
            buffDuration = 0;
            buff = null; // Clear buff when it expires
        }

        String elementString = getTypeString(elementPlaced);
        Element element = Element.valueOf(elementString.trim().toUpperCase()); // ADD toUpperCase()

        String spellString = getTypeString(spellPlaced);
        Spell spell = Spell.valueOf(spellString.trim()); // DON'T add toUpperCase()

        //Combination level
        if(Arrays.asList(tripleElements).contains(element))
        {
            combineLevel = 3;
        }
        else if(!Arrays.asList(singleElements).contains(element))
        {
            combineLevel = 2;
        }
        else
        {
            combineLevel = 1;
        }

        //Spell type and calculations
        if(spell == Spell.PhysicalSpell || spell == Spell.ElementalSpell)
        {
            //combination damage_
            if(combineLevel == 2)
            {
                damage_ *= 2;
            }
            if(combineLevel == 3)
            {
                damage_ *= 5;
            }
            //Spell matching element
            if(spell == Spell.PhysicalSpell && Arrays.asList(physicalElements).contains(element)) {
                damage_ *= 2;
            }
            else if(Arrays.asList(specialElements).contains(element)){
                damage_ *= 2;
            }
            //Buffs
            if(buffDuration > 0 && buff != null) // ADD null check
            {
                switch (buff) { // CHANGE from buff_ to buff
                    case FIRE -> {
                        if(Arrays.asList(fireElements).contains(element)) {
                            damage_ *= 2;
                        }
                    }
                    case WATER -> {
                        if(Arrays.asList(waterElements).contains(element)) {
                            damage_ *= 2;
                        }
                    }
                    case EARTH -> {
                        if(Arrays.asList(earthElements).contains(element)) {
                            damage_ *= 2;
                        }
                    }
                    case WIND -> {
                        if(Arrays.asList(windElements).contains(element)) {
                            damage_ *= 2;
                        }
                    }
                    case LIGHTNING -> {
                        if(Arrays.asList(lightingElements).contains(element)) {
                            damage_ *= 2;
                        }
                    }
                }
            }
            //Enemy weaknesses
            if(currentEnemy.getSpellWeakness() == spell)
            {
                damage_ *= 2;
            }
            switch (currentEnemy.getElementalWeakness()) {
                case FIRE -> {
                    if (Arrays.asList(fireElements).contains(element)) {
                        damage_ *= 3;
                    }
                }
                case WATER -> {
                    if (Arrays.asList(waterElements).contains(element)) {
                        damage_ *= 3;
                    }
                }
                case EARTH -> {
                    if (Arrays.asList(earthElements).contains(element)) {
                        damage_ *= 3;
                    }
                }
                case WIND -> {
                    if (Arrays.asList(windElements).contains(element)) {
                        damage_ *= 3;
                    }
                }
                case LIGHTNING -> {
                    if (Arrays.asList(lightingElements).contains(element)) {
                        damage_ *= 3;
                    }
                }
            }

            cardPanel.remove(spellPlaced);
            cardPanel.remove(elementPlaced);
            cardPanel.revalidate();
            cardPanel.repaint();
            elementPlaced = null;
            spellPlaced = null;
            currentEnemy.dealDamage(damage_);
        }
        else if(spell == Spell.BlockSpell)
        {
            //Combination bonus
            if(combineLevel == 2)
            {
                defense_ *= 2;
            }
            if(combineLevel == 3)
            {
                defense_ *= 5;
            }
            if(Arrays.asList(physicalElements).contains(element))
            {
                defense_ *= 3;
            }
            cardPanel.remove(spellPlaced);
            cardPanel.remove(elementPlaced);
            cardPanel.revalidate();
            cardPanel.repaint();
            elementPlaced = null;
            spellPlaced = null;
        }
        else //buff
        {
            buffDuration = 2;
            switch (element) {
                case FIRE -> buff_ = Element.FIRE;
                case WATER -> buff_ = Element.WATER;
                case EARTH -> buff_ = Element.EARTH;
                case WIND -> buff_ = Element.WIND;
                case LIGHTNING -> buff_ = Element.LIGHTNING;
                default -> buff_ = null;
            }
            cardPanel.remove(spellPlaced);
            cardPanel.remove(elementPlaced);
            cardPanel.revalidate();
            cardPanel.repaint();
            elementPlaced = null;
            spellPlaced = null;
        }
        defense += defense_;
        buff = buff_;
        rewriteStats();
        checkResult();
    }
    public void checkResult()
    {
        if(playerHealth<=0)
        {
            flowPanel.setOpaque(true);
            flowPanel.setVisible(true);
            flowPanel.setBackground(Color.BLACK);
            JLabel endText = new JLabel("The End");
            endText.setForeground(Color.WHITE);
            endText.setFont(new Font("Segoe UI", Font.BOLD, 48));
            flowPanel.add(endText);
        }
        else if(currentEnemy.gethealth()<=0)
        {
            coins+=1;
            difficulty++;
            buyStage();
        }
        else
        {
            if(playerTurn)
            {
                enemyAttack();
            }
            else
            {
                playerAttack();
            }

        }
        this.requestFocusInWindow();
        rewriteStats();
    }
    public void rewriteStats()
    {
        if(currentEnemy!=null)
        {
            statsLabel.setText(
                    "<html>" +
                            "Player Health: " + playerHealth + "<br>" +
                            "Defense: " + defense + "<br>" +
                            "Buff: " + (buff != null ? buff.name() : "None") + "<br>" +
                            "Buff Duration: " + buffDuration + "<br>" +
                            "Enemy Health: " + currentEnemy.gethealth() + "<br>" +
                            "Enemy Damage: " + currentEnemy.damage() + "<br>" +
                            "Element Weakness: " + currentEnemy.getElementalWeakness().name() + "<br>" +
                            "Spell Weakness: " + currentEnemy.getSpellWeakness().name() + "<br>" +
                            "Coins: " + coins + "<br>" +
                            "Difficulty: " + difficulty +
                            "</html>"
            );
        }
    }
    public void buyStage()
    {
        flowPanel.removeAll();
        flowPanel.setVisible(true);
        flowPanel.setOpaque(true);
        JButton card1 = new JButton();
        JButton card2 = new JButton();
        JButton card3 = new JButton();

        // Create cards - effectively final
        final Card card1type = createCardFromRandom((int)(Math.random() * 9) + 1);
        final Card card2type = createCardFromRandom((int)(Math.random() * 9) + 1);
        final Card card3type = createCardFromRandom((int)(Math.random() * 9) + 1);

        // Set button text to show card type
        if (card1type instanceof ElementCard) {
            card1.setText("Element: " + ((ElementCard) card1type).getElement());
        } else if (card1type instanceof SpellCard) {
            card1.setText("Spell: " + ((SpellCard) card1type).getSpellType());
        }
        if (card2type instanceof ElementCard) {
            card2.setText("Element: " + ((ElementCard) card2type).getElement());
        } else if (card2type instanceof SpellCard) {
            card2.setText("Spell: " + ((SpellCard) card2type).getSpellType());
        }
        if (card3type instanceof ElementCard) {
            card3.setText("Element: " + ((ElementCard) card3type).getElement());
        } else if (card3type instanceof SpellCard) {
            card3.setText("Spell: " + ((SpellCard) card3type).getSpellType());
        }
        applyCardImageJButton(card1);
        applyCardImageJButton(card2);
        applyCardImageJButton(card3);
        // Configure buttons
        for (JButton i : new JButton[]{card1, card2, card3}) {
            i.setFont(new Font("Segoe UI", Font.BOLD, 16));
            i.setForeground(Color.WHITE);
            i.setBackground(Color.BLACK);
            i.setFocusPainted(false);
            i.setBorderPainted(false);
            i.setContentAreaFilled(true);
            i.setOpaque(true);

            Dimension cardSize = new Dimension(200, 300);
            i.setPreferredSize(cardSize);
            i.setMaximumSize(cardSize);

            i.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(coins > 3) {
                        if(i == card1) {
                            deck.add(card1type);
                        }
                        else if(i == card2) {
                            deck.add(card2type);
                        }
                        else {
                            deck.add(card3type);
                        }
                        coins -= 3;
                    }
                    startBattle();
                }
            });
        }
        // Add buttons to panel
        flowPanel.add(card1);
        flowPanel.add(card2);
        flowPanel.add(card3);
    }

    // Helper method to create cards based on random number
    private Card createCardFromRandom(int randomNum) {
        return switch (randomNum) {
            case 1 -> new ElementCard(Element.FIRE);
            case 2 -> new ElementCard(Element.WATER);
            case 3 -> new ElementCard(Element.EARTH);
            case 4 -> new ElementCard(Element.WIND);
            case 5 -> new ElementCard(Element.LIGHTNING);
            case 6 -> new SpellCard(Spell.PhysicalSpell);
            case 7 -> new SpellCard(Spell.ElementalSpell);
            case 8 -> new SpellCard(Spell.BlockSpell);
            case 9 -> new SpellCard(Spell.BuffSpell);
            default -> null;
        };
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
        int health = (int)(Math.random()*9*difficulty);
        int damage = (int)(Math.random()*9*difficulty);

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
        // Remove all old card labels from the cardPanel before clearing
        for(JLabel oldCard : hand) {
            cardPanel.remove(oldCard);
        }

        ArrayList<Card> deckCopy = new ArrayList<>(deck);
        hand.clear();

        for(int i = 0; i<5 && !deckCopy.isEmpty();i++)
        {
            Random randomNum = new Random();
            int rn = randomNum.nextInt(deckCopy.size());
            createCard(deckCopy.get(rn));
            deckCopy.remove(rn);
        }

        cardPanel.revalidate();
        cardPanel.repaint();
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
        applyCardImage(card);


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
                    if (collisionType == 1 && CardType == '1') {
                        if(combinePlaced != null)
                        {
                            String element1 = getTypeString(combinePlaced);
                            String element2 = getTypeString(card);
                            Element e1 = Element.valueOf(element1.trim().toUpperCase()); // ADD toUpperCase()
                            Element e2 = Element.valueOf(element2.trim().toUpperCase()); // ADD toUpperCase()
                            Element combinedElement = elementCombinations.combine(e1, e2);

                            if(combinedElement != null)
                            {
                                cardPanel.remove(combinePlaced);
                                cardPanel.revalidate();
                                cardPanel.repaint();

                                //Set new type in string by converting the two strings into elements and then getting the combined element
                                card.setText("1" + combinedElement.name());
                                combinePlaced = card;
                                card.setLocation(combinePBoxLocation);
                                applyCardImage(card);
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
    public void applyCardImage(JLabel cardLabel) {
        String cardText = cardLabel.getText();
        if (cardText == null || cardText.isEmpty()) {
            return;
        }

        String typeString = getTypeString(cardLabel);
        if (typeString == null) {
            return;
        }

        String imagePath = null;

        // Determine image path based on card type
        char cardType = cardText.charAt(0);

        if (cardType == '1') { // Element Card
            try {
                Element element = Element.valueOf(typeString.trim().toUpperCase());
                imagePath = switch (element) {
                    case FIRE -> "/cards/fire.png";
                    case WATER -> "/cards/water.png";
                    case EARTH -> "/cards/earth.png";
                    case WIND -> "/cards/wind.png";
                    case LIGHTNING -> "/cards/lightning.png";
                    case BLAZE -> "/cards/blaze.png";
                    case STEAM -> "/cards/steam.png";
                    case ICE -> "/cards/ice.png";
                    case SCORCH -> "/cards/scorch.png";
                    case STORM -> "/cards/storm.png";
                    case PLASMA -> "/cards/plasma.png";
                    case LAVA -> "/cards/lava.png";
                    case WOOD -> "/cards/wood.png";
                    case SAND -> "/cards/sand.png";
                    case MAGNET -> "/cards/magnet.png";
                    case VOLT -> "/cards/volt.png";
                    case GUST -> "/cards/gust.png";
                    case TSUNAMI -> "/cards/tsunami.png";
                    case MOUNTAIN -> "/cards/mountain.png";
                    case QUICK -> "/cards/quick.png";
                    case GLACIER -> "/cards/glacier.png";
                    case BLOOD -> "/cards/blood.png";
                    case GLASS -> "/cards/glass.png";
                    case SLIME -> "/cards/slime.png";
                    case METAL -> "/cards/metal.png";
                };
            } catch (Exception e) {
                System.out.println("Error parsing element: " + e.getMessage());
                return;
            }
        } else if (cardType == '2') { // Spell Card
            try {
                Spell spell = Spell.valueOf(typeString.trim());
                imagePath = switch (spell) {
                    case PhysicalSpell -> "/cards/physical_spell.png";
                    case ElementalSpell -> "/cards/elemental_spell.png";
                    case BlockSpell -> "/cards/block_spell.png";
                    case BuffSpell -> "/cards/buff_spell.png";
                };
            } catch (Exception e) {
                System.out.println("Error parsing spell: " + e.getMessage());
                return;
            }
        }

        // Load and apply the image
        if (imagePath != null) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));

                // Scale image to fit 200x300 bounds
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        200, 300, Image.SCALE_SMOOTH
                );

                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                cardLabel.setIcon(scaledIcon);

            } catch (Exception e) {
                System.out.println("Error loading image: " + imagePath + " - " + e.getMessage());
            }
        }
    }
    public void applyCardImageJButton(JButton cardButton) {
        String cardText = cardButton.getText();
        if (cardText == null || cardText.isEmpty()) {
            return;
        }

        String typeString = cardButton.getText().substring(1);
        if (typeString == null) {
            return;
        }

        String imagePath = null;

        // Determine image path based on card type
        char cardType = cardText.charAt(0);

        if (cardType == '1') { // Element Card
            try {
                Element element = Element.valueOf(typeString.trim().toUpperCase());
                imagePath = switch (element) {
                    case FIRE -> "/cards/fire.png";
                    case WATER -> "/cards/water.png";
                    case EARTH -> "/cards/earth.png";
                    case WIND -> "/cards/wind.png";
                    case LIGHTNING -> "/cards/lightning.png";
                    case BLAZE -> "/cards/blaze.png";
                    case STEAM -> "/cards/steam.png";
                    case ICE -> "/cards/ice.png";
                    case SCORCH -> "/cards/scorch.png";
                    case STORM -> "/cards/storm.png";
                    case PLASMA -> "/cards/plasma.png";
                    case LAVA -> "/cards/lava.png";
                    case WOOD -> "/cards/wood.png";
                    case SAND -> "/cards/sand.png";
                    case MAGNET -> "/cards/magnet.png";
                    case VOLT -> "/cards/volt.png";
                    case GUST -> "/cards/gust.png";
                    case TSUNAMI -> "/cards/tsunami.png";
                    case MOUNTAIN -> "/cards/mountain.png";
                    case QUICK -> "/cards/quick.png";
                    case GLACIER -> "/cards/glacier.png";
                    case BLOOD -> "/cards/blood.png";
                    case GLASS -> "/cards/glass.png";
                    case SLIME -> "/cards/slime.png";
                    case METAL -> "/cards/metal.png";
                };
            } catch (Exception e) {
                System.out.println("Error parsing element: " + e.getMessage());
                return;
            }
        } else if (cardType == '2') { // Spell Card
            try {
                Spell spell = Spell.valueOf(typeString.trim());
                imagePath = switch (spell) {
                    case PhysicalSpell -> "/cards/physical_spell.png";
                    case ElementalSpell -> "/cards/elemental_spell.png";
                    case BlockSpell -> "/cards/block_spell.png";
                    case BuffSpell -> "/cards/buff_spell.png";
                };
            } catch (Exception e) {
                System.out.println("Error parsing spell: " + e.getMessage());
                return;
            }
        }

        // Load and apply the image
        if (imagePath != null) {
            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(imagePath));

                // Scale image to fit 200x300 bounds
                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        200, 300, Image.SCALE_SMOOTH
                );

                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                cardButton.setIcon(scaledIcon);

            } catch (Exception e) {
                System.out.println("Error loading image: " + imagePath + " - " + e.getMessage());
            }
        }
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
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

            if (paused) {
                flowPanel.removeAll();
                flowPanel.setVisible(false);
                flowPanel.setOpaque(true);
                // Hide X - go back to fullscreen borderless
                paused = false;
                gd.setFullScreenWindow(null);
                dispose();
                setUndecorated(true); // Remove title bar (hide X)
                setVisible(true);
                gd.setFullScreenWindow(this); // Fullscreen
            }
            else {
                // Show X - exit fullscreen and show title bar
                paused = true;
                gd.setFullScreenWindow(null);
                dispose();
                setUndecorated(false); // Add title bar (show X)
                setVisible(true);
                setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize
                //Go through the players deck
                flowPanel.removeAll();
                flowPanel.setVisible(true);
                flowPanel.setOpaque(true);
                for(Card card : deck)
                {
                    JLabel cardlabel = new JLabel();
                    if (card instanceof ElementCard) {
                        cardlabel.setText("1" + ((ElementCard) card).getElement());
                    } else if (card instanceof SpellCard) {
                        cardlabel.setText("2" + ((SpellCard) card).getSpellType());
                    }
                    applyCardImage(cardlabel);
                    Dimension boxSize = new Dimension(200, 300);
                    cardlabel.setPreferredSize(boxSize);
                    cardlabel.setMaximumSize(boxSize);
                    cardlabel.setOpaque(true);
                    cardlabel.setBackground(Color.BLACK);
                    cardlabel.setForeground(Color.WHITE);
                    flowPanel.add(cardlabel);
                }
            }

            this.requestFocusInWindow();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

}