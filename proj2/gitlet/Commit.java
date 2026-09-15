package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Blob.resetStage;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable, Dumpable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private String timestamp;
    /** The parent commit's hash of this Commit. */
    private String parent;
    /** The second parent commit's hash of this Commit. */
    private String secondParent;
    /** The hash of this Commit. */
    private String self_hash;
    /** The map<String, String> of this Commit. */
    private TreeMap<String, String> nameToHash = new TreeMap<String, String>();

    /* TODO: fill in the rest of this class. */

    /**
     *
     * @param info the commit message
     * @param m  the map
     * @param p  the node points to the parent
     */
    public Commit(String info, TreeMap<String, String> m, String p){
        this.message = info;
        this.nameToHash = m;
        this.parent = p;
        this.secondParent = null;
        Date time = new Date();
        this.timestamp = time.toString();

    }

    /**create the same first commit for every gitlet_init
     *
     *
     */
    public static String first_commit() throws IOException {
        Commit first = new Commit("initial commit",new TreeMap<>(), null);
        Date metaTime = new Date(0);
        first.timestamp = metaTime.toString();
        byte[] text = Utils.serialize(first);
        first.self_hash = Utils.sha1(text);
        File fir = Utils.join(Commit_DIR, first.self_hash);
        fir.createNewFile();
        Utils.writeObject(fir, first);
        currHead = first.self_hash;
        File h = Utils.join(ref, "master");
        h.createNewFile();
        Utils.writeContents(h, currHead);
        return first.self_hash;
    }

    /**
     * commiting a new commit in *current* branch
     */
    public static String commitInCurrBran(String me) throws IOException {
        TreeMap<String, String> m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        Commit nc = new Commit(me, m, currHead);
        File currBranch = Utils.join(ref, Utils.readContentsAsString(HEAD));
        byte[] text = Utils.serialize(nc);
        nc.self_hash = Utils.sha1(text);
        currHead = nc.self_hash;
        String[] files = nc.nameToHash.keySet().toArray(new String[0]);
        for (String name : files) {
            File f = Utils.join(stage_DIR, nc.nameToHash.get(name));
            File copy = Utils.join(blob_DIR, nc.nameToHash.get(name));
            Utils.writeContents(copy, Utils.readContentsAsString(f));
        }
        File ncf = Utils.join(Commit_DIR, nc.self_hash);
        Utils.writeObject(ncf, nc);
        ncf.createNewFile();
        Utils.writeContents(currBranch, nc.self_hash);
        resetStage();
        return currHead;
    }

    /**
     * create branch
     */
    public static void branch(String branch) throws IOException {
        File b = Utils.join(ref, branch);
        if (b.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }else {
            b.createNewFile();
            Utils.writeContents(b, branch);
        }
    }

    /**
     * get the current Commit's map
     */
    public static TreeMap<String, String> currCommitFiles_map() {
        Commit c = Utils.readObject(Utils.join(Commit_DIR, currHead), Commit.class);
        return c.nameToHash;
    }


    @Override
    public void dump() {
        System.out.printf("parent: %s%n map: %s%n self_hash: %s%n",
                this.parent,this.nameToHash, this.self_hash);
    }
}
