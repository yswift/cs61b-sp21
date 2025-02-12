package gitlet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author yswift
 */
public class Repository {
    /**
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = Utils.join(CWD, ".gitlet");

    // init command
    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in "
                    + "the current directory.");
            return;
        }
        GITLET_DIR.mkdir();
        Commit.COMMIT_DIR.mkdir();
        Blob.BLOB_DIR.mkdir();
        Branch.BRANCH_DIR.mkdir();

        // create a staging area
        Staging staging = new Staging();
        staging.save();

        // create a initial commit
        Commit initialCommit = new Commit("initial commit", null, null, new Date(0));
        initialCommit.save();
        Branch.setCommitId("master", initialCommit.getHash());
        Head.setBranch("master");
    }

    // add command
    public static void add(String filename) {
        File file = Utils.join(CWD, filename);
        if (!file.exists()) {
            Utils.exitWithError("File does not exist.");
            return;
        }
        Blob blob = new Blob(filename, Utils.readContentsAsString(file));

        Staging staging = Staging.load();
        // If the current working version of the file is identical to the version
        // in the current commit, do not stage it to be added
        Commit headCommit = Commit.load(Branch.getCommitId(Head.getBranch()));
        if (headCommit != null && headCommit.getBlobs().containsKey(filename)) {
            Blob headBlob = Blob.load(headCommit.getBlobs().get(filename));
            if (headBlob != null && headBlob.getHash().equals(blob.getHash())) {
                staging.getRemoval().remove(filename);
                staging.save();
                return;
            }
        }

        blob.save();
        staging.add(filename, blob.getHash());
        staging.save();
    }

    // commit command
    public static void commit(String message) {
        String currentCommitId = Branch.getCommitId(Head.getBranch());
        commit(message, currentCommitId, null);
    }

    private static void commit(String message, String currentCommitId, String mergedCommitId) {
        if (message.isEmpty()) {
            Utils.exitWithError("Please enter a commit message.");
            return;
        }
        Staging staging = Staging.load();
        if (staging.getAddition().isEmpty() && staging.getRemoval().isEmpty()) {
            Utils.exitWithError("No changes added to the commit.");
            return;
        }

        Commit commit = new Commit(message, currentCommitId, mergedCommitId);
        for (Map.Entry<String, String> entry : staging.getAddition().entrySet()) {
            commit.addBlob(entry.getKey(), entry.getValue());
        }
        for (String filename : staging.getRemoval()) {
            commit.removeBlob(filename);
        }
        commit.save();

        Branch.setCommitId(Head.getBranch(), commit.getHash());
        staging.clear();
        staging.save();
    }

    // rm command
    public static void rm(String filename) {
        Staging staging = Staging.load();
        if (staging.getAddition().containsKey(filename)) {
            staging.getAddition().remove(filename);
            staging.save();
            return;
        }

        Commit currentCommit = Commit.load(Branch.getCommitId(Head.getBranch()));
        if (currentCommit.getBlobs().containsKey(filename)) {
            staging.getRemoval().add(filename);
            Utils.join(CWD, filename).delete();
            staging.save();
            return;
        }

        Utils.exitWithError("No reason to remove the file.");
    }

    // log command
    public static void log() {
        for (String commitId = Branch.getCommitId(Head.getBranch()); commitId != null;) {
            Commit commit = Commit.load(commitId);
            System.out.println(commit);
            commitId = commit.getParentId();
        }
    }

    public static void globalLog() {
        for (String commitId : Utils.plainFilenamesIn(Commit.COMMIT_DIR)) {
            Commit commit = Commit.load(commitId);
            System.out.println(commit);
        }
    }

    // find command
    public static void find(String message) {
        boolean found = false;
        for (String commitId : Utils.plainFilenamesIn(Commit.COMMIT_DIR)) {
            Commit commit = Commit.load(commitId);
            if (commit.getMessage().contains(message)) {
                System.out.println(commit.getHash());
                found = true;
            }
        }
        if (!found) {
            Utils.exitWithError("Found no commit with that message.");
        }
    }

    // status command
    // === Branches ===
    // *master
    // other-branch
    //
    // === Staged Files ===
    // wug.txt
    // wug2.txt
    //
    // === Removed Files ===
    // goodbye.txt
    //
    // === Modifications Not Staged For Commit ===
    // junk.txt (deleted)
    // wug3.txt (modified)
    //
    // === Untracked Files ===
    // random.stuff
    public static void status() {
        System.out.println("=== Branches ===");
        for (String branchName : Utils.plainFilenamesIn(Branch.BRANCH_DIR)) {
            if (branchName.equals(Head.getBranch())) {
                System.out.println("*" + branchName);
            } else {
                System.out.println(branchName);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Staging staging = Staging.load();
        printCollectionString(staging.getAddition().keySet());

        System.out.println("=== Removed Files ===");
        printCollectionString(staging.getRemoval());

        Commit currentCommit = Commit.load(Branch.getCommitId(Head.getBranch()));
        List<String> cwdFileNames = Utils.plainFilenamesIn(CWD);

        System.out.println("=== Modifications Not Staged For Commit ===");
        List<String> modificationsNotStagedForCommit = getModificationsNotStagedForCommit(
                staging, currentCommit, cwdFileNames);
        printCollectionString(modificationsNotStagedForCommit);

        System.out.println("=== Untracked Files ===");
        List<String> untrackedFiles = getUntrackedFiles(staging, currentCommit, cwdFileNames);
        printCollectionString(untrackedFiles);
    }

    private static void printCollectionString(Collection<String> collection) {
        for (String item : collection) {
            System.out.println(item);
        }
        System.out.println();
    }

    // === Modifications Not Staged For Commit ===
    // junk.txt (deleted)
    // wug3.txt (modified)
    private static List<String> getModificationsNotStagedForCommit(Staging staging,
                                                                   Commit currentCommit,
                                                                   List<String> cwdFileNames) {
        List<String> result = new ArrayList<>();
        for (String fileName : cwdFileNames) {
            File file = Utils.join(CWD, fileName);
            Blob blob = new Blob(fileName, Utils.readContents(file));
            // case1: Tracked in the current commit, changed in the working directory, but not
            // staged; or
            boolean tracked = currentCommit.getBlobs().containsKey(fileName);
            boolean changed = !blob.getHash().equals(currentCommit.getBlobs().get(fileName));
            boolean staged = staging.getAddition().containsKey(fileName);
            if (tracked && changed && !staged) {
                result.add(fileName + " (modified)");
                continue;
            }
            // case2: Staged for addition, but with different contents than in the working
            // directory; or
            changed = !blob.getHash().equals(staging.getAddition().get(fileName));
            if (staged && changed) {
                result.add(fileName + " (modified)");
            }
        }
        // case3: Staged for addition, but deleted in the working directory; or
        for (String fileName : staging.getAddition().keySet()) {
            if (!cwdFileNames.contains(fileName)) {
                result.add(fileName + " (deleted)");
            }
        }
        // case4: Not staged for removal, but tracked in the current commit and deleted from the
        // working directory.
        for (String fileName : currentCommit.getBlobs().keySet()) {
            boolean stagedForRemoval = staging.getRemoval().contains(fileName);
            boolean cwdContains = cwdFileNames.contains(fileName);
            if (!stagedForRemoval && !cwdContains) {
                result.add(fileName + " (deleted)");
            }
        }
        Collections.sort(result);
        return result;
    }

    // === Untracked Files ===
    // random.stuff
    private static List<String> getUntrackedFiles(Staging staging, Commit currentCommit,
                                                  List<String> cwdFileNames) {
        List<String> result = new ArrayList<>();
        for (String fileName : cwdFileNames) {
            boolean tracked = currentCommit.getBlobs().containsKey(fileName);
            boolean staged = staging.getAddition().containsKey(fileName);
            // untracked files
            if (!staged && !tracked) {
                result.add(fileName);
            }
        }
        Collections.sort(result);
        return result;
    }

    public static void checkout(String[] args) {
        if (args.length == 2) {
            checkoutBranch(args[1]);
        } else if (args.length == 3 && args[1].equals("--")) {
            checkoutFile(Branch.getCommitId(Head.getBranch()), args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            checkoutFile(args[1], args[3]);
        } else {
            Utils.exitWithError("Incorrect operands.");
        }
    }

    private static void checkoutBranch(String branchName) {
        if (!Utils.plainFilenamesIn(Branch.BRANCH_DIR).contains(branchName)) {
            Utils.exitWithError("No such branch exists.");
            return;
        }
        if (branchName.equals(Head.getBranch())) {
            Utils.exitWithError("No need to checkout the current branch.");
            return;
        }
        checkoutCommit(Branch.getCommitId(branchName));
        Head.setBranch(branchName);
    }

    private static void checkoutCommit(String commitId) {
        Commit currentCommit = Commit.load(Branch.getCommitId(Head.getBranch()));
        Commit branchCommit = Commit.load(commitId);
        // check for untracked files
        List<String> cwdFileNames = Utils.plainFilenamesIn(CWD);
        Staging stagingArea = Staging.load();
        List<String> untrackedFiles = getUntrackedFiles(stagingArea, currentCommit, cwdFileNames);
        Set<String> fileNames = branchCommit.getBlobs().keySet();
        for (String untrackedFileName : untrackedFiles) {
            if (fileNames.contains(untrackedFileName)) {
                Utils.exitWithError("There is an untracked file in the way; "
                        + "delete it or add it first.");
            }
        }
        stagingArea.clear();
        stagingArea.save();

        for (String filename : currentCommit.getBlobs().keySet()) {
            File file = Utils.join(CWD, filename);
            file.delete();
        }
        for (String filename : fileNames) {
            Blob blob = Blob.load(branchCommit.getBlobs().get(filename));
            File file = Utils.join(CWD, filename);
            Utils.writeContents(file, blob.getContent());
        }
    }

    private static void checkoutFile(String commitId, String filename) {
        Commit commit = Commit.load(commitId);
        if (commit == null) {
            Utils.exitWithError("No commit with that id exists.");
            return;
        }
        if (!commit.getBlobs().containsKey(filename)) {
            Utils.exitWithError("File does not exist in that commit.");
            return;
        }
        Blob blob = Blob.load(commit.getBlobs().get(filename));
        File file = Utils.join(CWD, filename);
        Utils.writeContents(file, blob.getContent());
    }

    public static void branch(String branchName) {
        if (Utils.plainFilenamesIn(Branch.BRANCH_DIR).contains(branchName)) {
            Utils.exitWithError("A branch with that name already exists.");
            return;
        }
        Branch.setCommitId(branchName, Branch.getCommitId(Head.getBranch()));
    }

    public static void rmBranch(String branchName) {
        if (branchName.equals(Head.getBranch())) {
            Utils.exitWithError("Cannot remove the current branch.");
            return;
        }
        File branchFile = Utils.join(Branch.BRANCH_DIR, branchName);
        if (!branchFile.exists()) {
            Utils.exitWithError("A branch with that name does not exist.");
            return;
        }
        branchFile.delete();
    }

    public static void reset(String commitId) {
        Commit commit = Commit.load(commitId);
        if (commit == null) {
            Utils.exitWithError("No commit with that id exists.");
            return;
        }
        checkoutCommit(commitId);
        Branch.setCommitId(Head.getBranch(), commitId);
    }

    public static void merge(String branchName) {
        Staging staging = Staging.load();
        if (!staging.getAddition().isEmpty() || !staging.getRemoval().isEmpty()) {
            Utils.exitWithError("You have uncommitted changes.");
            return;
        }
        if (!Utils.plainFilenamesIn(Branch.BRANCH_DIR).contains(branchName)) {
            Utils.exitWithError("A branch with that name does not exist.");
            return;
        }
        if (branchName.equals(Head.getBranch())) {
            Utils.exitWithError("Cannot merge a branch with itself.");
            return;
        }
        Commit currentCommit = Commit.load(Branch.getCommitId(Head.getBranch()));
        Commit givenCommit = Commit.load(Branch.getCommitId(branchName));
        Commit splitPoint = findSplitPoint(currentCommit, givenCommit);
        if (splitPoint.getHash().equals(givenCommit.getHash())) {
            Utils.exitWithError("Given branch is an ancestor of the current branch.");
            return;
        }
        if (splitPoint.getHash().equals(currentCommit.getHash())) {
            checkoutCommit(givenCommit.getHash());
            Branch.setCommitId(Head.getBranch(), givenCommit.getHash());
            Utils.exitWithError("Current branch fast-forwarded.");
            return;
        }
        List<String> cwdFileNames = Utils.plainFilenamesIn(CWD);
        List<String> untrackedFiles = getUntrackedFiles(staging, currentCommit, cwdFileNames);
        for (String untrackedFileName : untrackedFiles) {
            if (givenCommit.getBlobs().containsKey(untrackedFileName)) {
                Utils.exitWithError("There is an untracked file in the way; "
                        + "delete it or add it first.");
            }
        }
        boolean conflict = processMerge(staging, splitPoint, currentCommit, givenCommit);

        staging.save();
        commit(String.format("Merged %s into %s.", branchName, Head.getBranch()),
                currentCommit.getHash(), givenCommit.getHash());
        if (conflict) {
            Utils.exitWithError("Encountered a merge conflict.");
        }
    }

    private static Commit findSplitPoint(Commit currentCommit, Commit givenCommit) {
        Set<String> currentAncestors = new HashSet<>();
        for (String commitId = currentCommit.getHash(); commitId != null;) {
            currentAncestors.add(commitId);
            Commit commit = Commit.load(commitId);
            commitId = commit.getParentId();
        }
        for (String commitId = givenCommit.getHash(); commitId != null;) {
            if (currentAncestors.contains(commitId)) {
                return Commit.load(commitId);
            }
            Commit commit = Commit.load(commitId);
            commitId = commit.getParentId();
        }
        return null;
    }

    private static boolean processMerge(Staging stagingArea, Commit splitPointCommit,
                                        Commit currentCommit, Commit mergedCommit) {
        boolean conflict = false;
        HashMap<String, String> splitBlobs = splitPointCommit.getBlobs();
        HashMap<String, String> currentBlobs = currentCommit.getBlobs();
        HashMap<String, String> mergedBlobs = mergedCommit.getBlobs();
        for (String fileName : mergedBlobs.keySet()) {
            // modified in the given branch since the split point
            String mergedBlobId = mergedBlobs.get(fileName);
            String splitBlobId = splitBlobs.get(fileName);
            String currentBlobId = currentBlobs.get(fileName);
            // case1: Any files that have been modified in the given branch since the split
            // point, but not modified in the current branch since the split point should be
            // changed to their versions in the given branch
            if (splitBlobId != null && !mergedBlobId.equals(splitBlobId)) {
                if (splitBlobId.equals(currentBlobId)) {
                    checkoutFile(mergedCommit.getHash(), fileName);
                    stagingArea.getAddition().put(fileName, mergedBlobId);
                    continue;
                }
            }
            // case3: keep same

            // case5: Any files that were not present at the split point and are present only in
            // the given branch should be checked out and staged.
            if (splitBlobId == null && currentBlobId == null) {
                checkoutFile(mergedCommit.getHash(), fileName);
                stagingArea.getAddition().put(fileName, mergedBlobId);
                continue;
            }
            // case7: keep same

            // case8: or the contents of one are changed and the other file is deleted,
            if (splitBlobId != null && !mergedBlobId.equals(splitBlobId) && currentBlobId == null) {
                conflict = true;
                processConflict(stagingArea, fileName, currentBlobId, mergedBlobId);
            }
        }

        for (String fileName : currentBlobs.keySet()) {
            String currentBlobId = currentBlobs.get(fileName);
            String splitBlobId = splitBlobs.get(fileName);
            String mergedBlobId = mergedBlobs.get(fileName);
            // case2: keep same
            // case4: keep same
            // case6: Any files present at the split point, unmodified in the current branch, and
            // absent in the given branch should be removed (and untracked).
            if (currentBlobId.equals(splitBlobId)) {
                if (mergedBlobId == null) {
                    Utils.join(CWD, fileName).delete();
                    stagingArea.getRemoval().add(fileName);
                    continue;
                }
            }
            // case8: the contents of both are changed and different from other
            if (splitBlobId != null && mergedBlobId != null) {
                if (!currentBlobId.equals(splitBlobId) && !mergedBlobId.equals(splitBlobId)) {
                    if (!currentBlobId.equals(mergedBlobId)) {
                        conflict = true;
                        processConflict(stagingArea, fileName, currentBlobId, mergedBlobId);
                    }
                }
            }
            // case8: or the contents of one are changed and the other file is deleted,
            if (splitBlobId != null && !currentBlobId.equals(splitBlobId) && mergedBlobId == null) {
                conflict = true;
                processConflict(stagingArea, fileName, currentBlobId, mergedBlobId);
            }
            // case8: or the file was absent at the split point and has different contents in the
            // given and current branches.
            if (splitBlobId == null && currentBlobId != null && mergedBlobId != null) {
                if (!currentBlobId.equals(mergedBlobId)) {
                    conflict = true;
                    processConflict(stagingArea, fileName, currentBlobId, mergedBlobId);
                }
            }
        }
        return conflict;
    }

    private static void processConflict(Staging stagingArea, String fileName,
                                        String currentBlobId, String mergedBlobId) {
        String newContents = conflictFileContents(currentBlobId, mergedBlobId);
        Blob newBlob = new Blob(fileName, newContents.getBytes(StandardCharsets.UTF_8));
        newBlob.save();
        File file = Utils.join(CWD, fileName);
        Utils.writeContents(file, newContents);
        stagingArea.getAddition().put(fileName, newBlob.getHash());
    }

    private static String conflictFileContents(String currentBlobId, String mergedBlobId) {
        String currentContents;
        String mergedContents;
        if (currentBlobId == null) {
            currentContents = "";
        } else {
            currentContents = Utils.readContentsAsString(Utils.join(Blob.BLOB_DIR, currentBlobId));
        }
        if (mergedBlobId == null) {
            mergedContents = "";
        } else {
            mergedContents = Utils.readContentsAsString(Utils.join(Blob.BLOB_DIR, mergedBlobId));
        }
        return "<<<<<<< HEAD\n" + currentContents + "=======\n" + mergedContents + ">>>>>>>\n";
    }
}
