package gitlet;

import java.io.File;
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
    /** The Staging directory. */
    public static final File staging_DIR = join(GITLET_DIR, "staging")

    /* TODO: fill in the rest of this class. */

    /**
     * initiate the .gitlet folder with a #initial commit
     * if the .gitlet already exists then throw an error
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory");
            System.exit(0);
        } else {
            GITLET_DIR.mkdir();
            staging_DIR.mkdir();
            //TODO: create an initial commit

        }
    }

    /**
     *
     */
    public static void commit(String message) {

    }

    /**
     * Adds a copy of the file as it currently exists to the staging area
     * if the file is same as that in current commit, delete it from staging if it is there
     */
    public static void add(File name) {
        if (!name.exists()) {
            message("File does not exists.");
            System.exit(0);
        }
    }
}
