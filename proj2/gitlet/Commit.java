package gitlet;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author yswift
 */
public class Commit implements Dumpable {
    public static final File COMMIT_DIR = Utils.join(Repository.GITLET_DIR, "commits");

    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    private Date timestamp;
    private String parentId;
    private String mergeParentId;
    private String hash;
    // filename -> blobId
    private HashMap<String, String> blobs = new HashMap<>();

    public Commit(String message, String parentId, String mergeParentId, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
        this.parentId = parentId;
        this.mergeParentId = mergeParentId;
        this.hash = Utils.sha1(message, timestamp.toString());
        Commit parent = Commit.load(parentId);
        if (parent != null) {
            blobs.putAll(parent.blobs);
        }
    }

    public Commit(String message, String parentId, String mergeParentId) {
        this(message, parentId, mergeParentId, new Date());
    }

    public Commit(String message, String parentId) {
        this(message, parentId, null, new Date());
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getParentId() {
        return parentId;
    }

    public String getMergeParentId() {
        return mergeParentId;
    }

    public String getHash() {
        return hash;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }

    public void save() {
        File commitFile = Utils.join(COMMIT_DIR, hash);
        Utils.writeObject(commitFile, this);
    }

    public static Commit load(String hash) {
        if (hash == null) {
            return null;
        }
        if (hash.length() < 40) {
            List<String> commitIdList = Utils.plainFilenamesIn(Commit.COMMIT_DIR);
            for (String commitId : commitIdList) {
                if (commitId.startsWith(hash)) {
                    hash = commitId;
                    break;
                }
            }
        }
        File commitFile = Utils.join(COMMIT_DIR, hash);
        if (!commitFile.exists()) {
            return null;
        }
        return Utils.readObject(commitFile, Commit.class);
    }

    public void addBlob(String filename, String blobId) {
        blobs.put(filename, blobId);
    }

    public String getBlob(String filename) {
        return blobs.get(filename);
    }

    public void removeBlob(String filename) {
        blobs.remove(filename);
    }

    @Override
    public void dump() {
//        System.out.printf("Commit %s%n", hash);
//        System.out.printf("%s%n", message);
//        System.out.printf("%s%n", timestamp);
//        System.out.printf("Parent: %s%n", parentId);
        System.out.println(this);
        System.out.println("Blobs:");
        for (String filename : blobs.keySet()) {
            System.out.printf("%s -> %s%n", filename, blobs.get(filename));
        }
    }

    // ===
    // commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
    // Date: Thu Nov 9 17:01:33 2017 -0800
    // Another commit message.
    //
    // ===
    // commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
    // Merge: 4975af1 2c1ead1
    // Date: Sat Nov 11 12:30:00 2017 -0800
    // Merged development into master.
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");
        StringBuilder sb = new StringBuilder();
        sb.append("===").append('\n');
        sb.append("commit ").append(hash).append('\n');
        if (mergeParentId != null) {
            sb.append("Merge: ");
            sb.append(parentId.substring(0, 7)).append(" ");
            sb.append(mergeParentId.substring(0, 7)).append('\n');
        }
        sb.append("Date: ").append(simpleDateFormat.format(timestamp)).append('\n');
        sb.append(message).append('\n');
        return sb.toString();
    }
}
