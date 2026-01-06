public class ElementCard extends Card {

    private String element;

    public ElementCard(String e) {
        super(1);
        this.element = e;
    }
    public String getElement() {
        return element;
    }
    public void setElementType(String e) {
        this.element = element;
    }
}