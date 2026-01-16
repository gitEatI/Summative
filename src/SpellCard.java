public class SpellCard extends Card {

    private Spell spellType;

    public SpellCard(Spell st) {
        super(2);
        this.spellType = st;
    }
    public void setSpellType(Spell st) {
        this.spellType = st;
    }
    public Spell getSpellType() {
        return spellType;
    }

}