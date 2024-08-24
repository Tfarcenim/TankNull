package tfar.tanknull;

import java.text.DecimalFormat;

public class Utils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");

    public static String formatLargeNumber(long number) {
        if (number >= 1000000000) return decimalFormat.format(number / 1000000000f) + "b";
        if (number >= 1000000) return decimalFormat.format(number / 1000000f) + "m";
        if (number >= 1000) return decimalFormat.format(number / 1000f) + "k";

        return Float.toString(number).replaceAll("\\.?0*$", "");
    }

    public static <E extends Enum<E>> E cycle(E e) {
        E[] values = (E[]) e.getClass().getEnumConstants();
        if (e.ordinal() == values.length - 1) {
            return values[0];
        }
        return values[e.ordinal()+1];
    }
}
