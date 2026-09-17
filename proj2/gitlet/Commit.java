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
    private final String parent;
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
        File fs = Utils.join(stage_DIR, "files");
        if (fs.exists()) {
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
            resetStage();
            return currHead;
        }else {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        return me;
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
     * Deletes the branch with the given name.
     * This only means to delete the pointer associated with the branch;
     * it does not mean to delete all commits that were created under the branch,
     * or anything like that.
     */
    public static void rm_branch(String name) {
        /* if the branch exists*/
        List<String> branches = Utils.plainFilenamesIn(ref);
        if (!branches.contains(name)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        /* if is the current branch */
        String curr_bran = Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD"));
        if (curr_bran.equals(name)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }

        File bran_rev = Utils.join(ref, name);
        bran_rev.delete();
    }

    /**
     * get the current Commit's map
     */
    public static TreeMap<String, String> currCommitFiles_map() {
        Commit c = Utils.readObject(Utils.join(Commit_DIR, currHead), Commit.class);
        return c.nameToHash;
    }

    /**
     * @return a list of information for log
     * 1 date
     * 2 message
     * 3 self_hash
     * 4 parent
     * 5 6 if merged, then add a “Merge:” consist of the first seven digits of the first
     *          and second parents’ commit ids
     */
    public List<String> returnInfo() {
        List<String> l = new ArrayList<>();
        l.add(this.timestamp);
        l.add(this.message);
        l.add(this.self_hash);
        l.add(this.parent);
        if (this.secondParent != null) {
            l.add(this.parent.substring(0, 7));
            l.add(this.secondParent.substring(0, 7));
        }

        return l;
    }


    @Override
    public void dump() {
        System.out.printf("parent: %s%n map: %s%n self_hash: %s%n",
                this.parent,this.nameToHash, this.self_hash);
    }
}
