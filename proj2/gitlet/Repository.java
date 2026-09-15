package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Blob.saveToStage;
import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *  all the commands are implemented in this part
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The Blobs directory. */
    public static final File blob_DIR = join(GITLET_DIR, "blobs");
    /** The Stage directory. */
    public static final File stage_DIR = join(GITLET_DIR, "stage");
    /** The Commits directory. */
    public static final File Commit_DIR = join(GITLET_DIR, "commits");
    /** The Head to point current commit*/
    public static String currHead;
    /** The Branches File. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The Reference directory. */
    public static final File ref = join(GITLET_DIR, "ref");

    /* TODO: fill in the rest of this class. */

    /**
     * initiate the .gitlet folder with a #initial commit
     * if the .gitlet already exists then throw an error
     */
    public static void init() throws IOException {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        } else {
            GITLET_DIR.mkdir();
            blob_DIR.mkdir();
            Commit_DIR.mkdir();
            stage_DIR.mkdir();
            HEAD.createNewFile();
            ref.mkdir();
            currHead = Commit.first_commit();
            Utils.writeContents(HEAD, "master");
            //TODO: create an initial commit

        }
    }

    /**
     *
     */
    public static void commit(String message) {
        File fs = Utils.join(stage_DIR, "files");
        if (!fs.exists()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else {

        }
    }

    /**
     * Adds a copy of the file as it currently exists to the stage area
     * if the file is same as that in current commit, delete it from stage if it is there
     */
    public static void add(String name) throws IOException {
        /**
         * 1.if the file do not exist in the workzone, print error
         * 2.if the file's previous version in the stage, replace it with current version
         * 3.if the file is identical to that in the current commit, do not stage it to be added,
         *     and remove it from the staging area if it is already there
         */
        if (name.isEmpty()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File f = Utils.join(CWD, name);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }else {
            Commit c = Utils.readObject(Utils.join(Commit_DIR, currHead), Commit.class);
            String hash = Utils.sha1(Utils.readContentsAsString(f));
            TreeMap<String, String> m = Commit.currCommitFiles_map();
            if (Utils.join(stage_DIR, "files").exists() && m.containsKey(name) && m.get(name).equals(hash)) {
                File copyInStage = Utils.join(stage_DIR, "files");
                TreeMap<String, String> text = Utils.readObject(copyInStage, java.util.TreeMap.class);
                text.remove(name);
                Utils.writeObject(copyInStage, text);
                return;
            }
            saveToStage(name);
        }
    }
    /**
     *  Unstage the file if it is currently staged for addition.
     *  If the file is tracked in the current commit,
     *      stage it for removal and remove the file from the working directory
     *      if the user has not already done so
     *          (do not remove it unless it is tracked in the current commit).
     */
    public static void rm(String name) {
        File CWDf = Utils.join(CWD, name);
        File fs = Utils.join(stage_DIR, "files");
        if (fs.exists()) {
            TreeMap<String, String> m = Utils.readObject(fs, java.util.TreeMap.class);
            if (m.containsKey(name)) {
                m.remove(name);
                Utils.writeObject(fs, m);
                return;
            }
        }
        if (Commit.currCommitFiles_map().containsKey(name)) {
            CWDf.delete();
        }else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }


}
