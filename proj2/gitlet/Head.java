package gitlet;

import java.io.File;

public class Head {
    public static final File HEAD_FILE = Utils.join(Repository.GITLET_DIR, "HEAD");

    public static void setBranch(String branchName) {
        Utils.writeContents(HEAD_FILE, branchName);
    }

    public static String getBranch() {
        return Utils.readContentsAsString(HEAD_FILE);
    }
}
