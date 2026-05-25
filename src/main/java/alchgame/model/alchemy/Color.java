package alchgame.model.alchemy;

import java.util.Arrays;

public enum Color {
    RED, GREEN, BLUE, NONE;

    public static Color[] real() {
        return Arrays.stream(values())
                .filter(c -> c != NONE)
                .toArray(Color[]::new);
    }
}
