public class ElementCard extends Card {

    private Element element;

    public ElementCard(Element e) {
        super(1);
        this.element = e;
    }
    public Element getElement() {
        return element;
    }
    public void setElementType(Element e) {
        this.element = e;
    }
}