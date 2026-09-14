package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static gitlet.Repository.CWD;
import static gitlet.Repository.stage_DIR;
import static gitlet.Utils.join;

public class Blob implements Serializable {
    /**
     * This class is to work as below:
     * 1. store the hash of the relative file
     * 2. serializable for better creating the commit
     */
    /**
     * @param fHash The file's hash
     * @param file The file
     */
    private String fHash;
    private File file;

    private Blob(File f) {
        this.file = f;
        this.fHash = Utils.sha1(f);
    }

    /**
     * Saves the blob to a file.
     * Store the file's names in the stage
     * help add method works
     *
     * @param name the file to be staged
     *             stageFile store the 'name' content and the name itself is the hash
     *             filename stores all the stage files' name by using newfilename and oldfilename
     */
    public static void saveToStage(String name) throws IOException {
        File f = join(CWD, name);
        Blob b = new Blob(f);
        File stageFile = join(stage_DIR, b.fHash);

        File filename = join(stage_DIR, "files.txt");
        String newfilename;

        if (filename.exists()) {
            String oldfilename = Utils.readContentsAsString(filename);
            Set<String> names = Arrays.stream(oldfilename.split("\n")).collect(Collectors.toSet());
            if (names.contains(name)) {
                newfilename = oldfilename;
            } else {
                newfilename = oldfilename + name + "\n";
            }
        } else {
            newfilename = name + "\n";
            filename.createNewFile();
        }
        Utils.writeContents(filename, newfilename);

        if (!stageFile.exists()) {
            String text = Utils.readContentsAsString(b.file);
            Utils.writeObject(stageFile, text);
        }
    }

    /**
     * reset the stage after commiting
     */
    public static void resetStage() {
        File[] files = stage_DIR.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}
