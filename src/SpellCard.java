public class SpellCard extends Card {

    private int spellType;

    public SpellCard(int st) {
        super(2);
        this.spellType = st;
    }
    public void setSpellType(int st) {
        this.spellType = st;
    }
    public int getSpellType() {
        return spellType;
    }

}