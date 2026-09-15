package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.LinkedList;
import java.util.TreeMap;

import static gitlet.Repository.CWD;
import static gitlet.Repository.stage_DIR;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
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
    private String parent_hash;
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
        Date time = new Date();
        this.timestamp = time.toString();

        this.parent_hash = p;


        this.self_hash = Utils.sha1(this);

    }

    /**create the same first commit for every gitlet_init
     *
     *
     */
    public String first_commit() {
        Commit first = new Commit("initial commit",new TreeMap<>(), null);
        Date metaTime = new Date(0);
        first.timestamp = metaTime.toString();
        return first.self_hash;
    }

}
