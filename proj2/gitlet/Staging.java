package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class Staging implements Dumpable {
    public static final File STAGING_FILE = Utils.join(Repository.GITLET_DIR, "staging");

    // filename -> blobId
    private final HashMap<String, String> addition = new HashMap<>();
    private final HashSet<String> removal = new HashSet<>();

    public void add(String filename, String blobId) {
        removal.remove(filename);
        addition.put(filename, blobId);
    }

    public HashMap<String, String> getAddition() {
        return addition;
    }

    public HashSet<String> getRemoval() {
        return removal;
    }

    public void clear() {
        addition.clear();
        removal.clear();
    }

    public void save() {
        Utils.writeObject(STAGING_FILE, this);
    }

    public static Staging load() {
        if (!STAGING_FILE.exists()) {
            return null;
        }
        return Utils.readObject(STAGING_FILE, Staging.class);
    }

    public void dump() {
        System.out.println("addition: " + addition);
        System.out.println("removal: " + removal);
    }
}
