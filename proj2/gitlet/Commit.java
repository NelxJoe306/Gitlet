package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
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
    private final TreeMap<String, String> nameToHash = new TreeMap<String, String>();
    /* TODO: fill in the rest of this class. */

    public Commit(String massage, String name){
        File f = Utils.join(CWD, name);
        String hash = Utils.sha1(f);
        File copy = Utils.join(stage_DIR, hash);
        if (f.exists()) {
            nameToHash.put(name, hash);
        } else {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        this.message = massage;

    }


}
