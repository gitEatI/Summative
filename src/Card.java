public class Card {

    private int cardType; // 1 = Element, 2 = SpellType, 3 = Utility

    public Card(int ct) {
        this.cardType = ct;
    }

    public int getCardType() {
        return cardType;
    }
    public void setCardType(int ct) {
        this.cardType = ct;
    }
    public void play(){
    }
}
