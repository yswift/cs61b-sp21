package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Blob implements Dumpable {
    public static final File BLOB_DIR = Utils.join(Repository.GITLET_DIR, "blobs");

    private String filename;
    private byte[] content;
    private String hash;

    public Blob(String filename, String content) {
        this(filename, content.getBytes(StandardCharsets.UTF_8));
    }

    public Blob(String filename, byte[] content) {
        this.filename = filename;
        this.content = content;
        this.hash = Utils.sha1(filename, content);
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }

    public String getHash() {
        return hash;
    }

    public void dump() {
        System.out.println("filename: " + filename);
//        System.out.println("content: " + getContentAsString());
        System.out.println("hash: " + hash);
    }

    public void save() {
        File blobFile = Utils.join(BLOB_DIR, hash);
        Utils.writeObject(blobFile, this);
    }

    public static Blob load(String hash) {
        File blobFile = Utils.join(BLOB_DIR, hash);
        if (!blobFile.exists()) {
            return null;
        }
        return Utils.readObject(blobFile, Blob.class);
    }
}
