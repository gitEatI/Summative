import java.util.HashMap;
public class ElementCombinations {

    private HashMap<String, Element> twoCombos;
    private HashMap<String, Element> threeCombos;

    public ElementCombinations() {
        twoCombos = new HashMap<>();
        threeCombos = new HashMap<>();
        setupCombos();
    }

    private void setupCombos() {

        // Two-element combos
        twoCombos.put("FIRE+WATER", Element.STEAM);
        twoCombos.put("WIND+EARTH", Element.SAND);
        twoCombos.put("METAL+WIND", Element.VOLT);

        // Three-element combos
        threeCombos.put("METAL+VOLT+WIND", Element.LIGHTNING);
    }

    public Element combineTwo(Element a, Element b) {

        String key1 = a.name() + "+" + b.name();
        String key2 = b.name() + "+" + a.name();

        if (twoCombos.containsKey(key1))
            return twoCombos.get(key1);

        if (twoCombos.containsKey(key2))
            return twoCombos.get(key2);

        return null;
    }

    public Element combineThree(Element a, Element b, Element c) {

        String key = a.name() + "+" + b.name() + "+" + c.name();
        return threeCombos.get(key);
    }
}

