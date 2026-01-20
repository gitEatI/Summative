public class Enemy {
    private Element elementalWeakness;
    private Spell spellWeakness;
    private int health;
    private int damage;
    public Enemy(Element ew, Spell sw,int h,int d)
    {
        this.elementalWeakness = ew;
        this.spellWeakness = sw;
        this.health = h;
        this.damage = d;
    }
    public Element getElementalWeakness()
    {
        return elementalWeakness;
    }
    public Spell getSpellWeakness()
    {
        return spellWeakness;
    }
    public int gethealth()
    {
        return health;
    }
    public int damage()
    {
        return damage;
    }
    public void dealDamage(int d)
    {
        health-=d;
    }

}
