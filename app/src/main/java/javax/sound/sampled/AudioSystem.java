package javax.sound.sampled;

@SuppressWarnings("unused")
public class AudioSystem {

    private static final Mixer.Info[] infos = {new Mixer.Info()};

    public static Mixer.Info[] getMixerInfo() {
        return infos;
    }
}
