package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    private static final GuitarString[] strings = new GuitarString[keyboard.length()];
    static {
        for (int i = 0; i < keyboard.length(); i++) {
            strings[i] = new GuitarString(440 * Math.pow(2, (i - 24) / 12.0));
        }
    }

    public void play() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                int index = keyboard.indexOf(key);
                if (index != -1) {
                    strings[index].pluck();
                }
            }
            double sample = 0;
            for (GuitarString string : strings) {
                sample += string.sample();
            }
            StdAudio.play(sample);

            for (GuitarString string : strings) {
                string.tic();
            }
        }
    }

    public static void main(String[] args) {
        GuitarHero guitarHero = new GuitarHero();
        guitarHero.play();
    }
}
