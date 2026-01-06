import java.lang.runtime.SwitchBootstraps;

public class UtilityCard extends Card {

    private int utilityValue;

    public UtilityCard(int uv) {
        super(3);
        this.utilityValue = uv;
    }

    public int getUtilityValue() {
        return utilityValue;
    }

    @Override
    public void play() {

    }
}