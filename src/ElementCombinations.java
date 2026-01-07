import java.util.Arrays;
import java.util.HashMap;
public class ElementCombinations {

    private HashMap<String, Element> combos;
    //private HashMap<String, Element> threeCombos;

    public ElementCombinations() {
        combos = new HashMap<>();
        //threeCombos = new HashMap<>();
        setCombos();
    }

    private void setCombos() {
        //Self
        combos.put("FIRE+FIRE", Element.BLAZE);
        combos.put("WIND+WIND", Element.GUST);
        combos.put("LIGHTNING+LIGHTNING", Element.VOLT);
        combos.put("EARTH+EARTH", Element.MOUNTAIN);
        combos.put("WATER+WATER", Element.TSUNAMI);
        //Fire
        combos.put("FIRE+WIND", Element.SCORCH);
        combos.put("FIRE+LIGHTNING", Element.PLASMA);
        combos.put("EARTH+FIRE", Element.LAVA);
        combos.put("FIRE+WATER", Element.STEAM);
        //Wind without fire
        combos.put("LIGHTNING+WIND", Element.QUICK);
        combos.put("EARTH+WIND", Element.SAND);
        combos.put("WATER+WIND", Element.ICE);
        //Lighting without fire,wind
        combos.put("EARTH+LIGHTNING", Element.MAGNET);
        combos.put("LIGHTNING+WATER", Element.STORM);
        //Earth without fire,wind,lighting
        combos.put("EARTH+WATER", Element.WOOD);

        //Advanced
        combos.put("FIRE+SAND", Element.GLASS);
        combos.put("QUICK+WATER", Element.SLIME);
        combos.put("EARTH+PLASMA", Element.METAL);
        combos.put("EARTH+ICE", Element.GLACIER);
        combos.put("PLASMA+WATER", Element.BLOOD);

        //Advanced+Advanced for player satisfaction(blaze counts as fire) (maybe)
//        combos.put("BLAZE+SAND", Element.GLASS);
//        combos.put("QUICK+TSUNAMI", Element.SLIME);
//        combos.put("MOUNTAIN+PLASMA", Element.METAL);
//        combos.put("ICE+MOUNTAIN", Element.GLACIER);
//        combos.put("PLASMA+TSUNAMI", Element.BLOOD);
    }

    public Element combine(Element a, Element b) {
        String[] names = {a.name(), b.name()};

        Arrays.sort(names);

        String key = names[0] + "+" + names[1];
        return combos.get(key);
    }

//    public Element combineThree(Element a, Element b, Element c) {
//        String[] names = {a.name(), b.name(), c.name()};
//
//        Arrays.sort(names);
//
//        String key = names[0] + "+" + names[1] + "+" + names[2];
//        return threeCombos.get(key);
//    }
}

