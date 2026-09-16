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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /**
     * The Blobs directory.
     */
    public static final File blob_DIR = join(GITLET_DIR, "blobs");
    /**
     * The Stage directory.
     */
    public static final File stage_DIR = join(GITLET_DIR, "stage");
    /**
     * The Commits directory.
     */
    public static final File Commit_DIR = join(GITLET_DIR, "commits");
    /**
     * The Head to point current commit
     */
    public static String currHead;
    /**
     * The Branches File.
     */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /**
     * The Reference directory.
     */
    public static final File ref = join(GITLET_DIR, "ref");
    /**
     * The removal directory
     */
    public static final File removal = join(GITLET_DIR, "rm");

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
            removal.mkdir();
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
        } else {
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
     * Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit,
     * stage it for removal and remove the file from the working directory
     * if the user has not already done so
     * (do not remove it unless it is tracked in the current commit).
     */
    public static void rm(String name) throws IOException {
        File CWDf = Utils.join(CWD, name);
        File fs = Utils.join(stage_DIR, "files");
        if (fs.exists()) {
            TreeMap<String, String> m = Utils.readObject(fs, java.util.TreeMap.class);
            if (m.containsKey(name)) {
                m.remove(name);
                Utils.writeObject(fs, m);
                String content = Utils.readContentsAsString(CWDf);
                File r = Utils.join(removal, name);
                if (r.exists()) {
                    Utils.writeContents(r, content);
                } else {
                    r.createNewFile();
                    Utils.writeContents(r, content);
                }
                return;
            }
        }
        if (Commit.currCommitFiles_map().containsKey(name)) {
            CWDf.delete();
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

    }

    /**
     * Starting at the current head commit,
     * display information about each commit backwards along the commit tree
     * until the initial commit, following the first parent commit links,
     * ignoring any second parents found in merge commits.
     * For every node in this history, the information it should display is
     * the commit id, the timestamp, and message.
     * e.g.
     * ===
     * commit 3e8bf1d794ca2e9ef8a4007275acf3751c7170ff
     * Date: Thu Nov 9 17:01:33 2017 -0800
     * Another commit message.
     * ===
     * commit e881c9575d180a215d1a636545b8fd9abfb1d2bb
     * Date: Wed Dec 31 16:00:00 1969 -0800
     * initial commit
     */
    public static void log() {
        String p;
        Date d = new Date(0);
        String initTime = d.toString();

        String me;
        String ts; /* timestamp */
        String hash;
        String par;
        String secPar;

        String time = " ";
        Commit hc; /*history commit*/
        File cf = Utils.join(Commit_DIR, currHead);
        Commit cc = Utils.readObject(cf, Commit.class);
        p = cc.returnInfo().get(3);
        File hf = Utils.join(Commit_DIR, p);
        List<String> l;
        while (!time.equals(initTime)) {
            hc = Utils.readObject(hf, Commit.class);
            l = hc.returnInfo();
            ts = l.get(0);
            me = l.get(1);
            hash = l.get(2);
            p = l.get(3);
            if (l.size() > 4) {
                par = l.get(4);
                secPar = l.get(5);
                System.out.printf("===%ncommit: %s%nMerge: %s %s%nDate: %s%n%s%n",
                        hash, par, secPar, ts, me);
            } else {
                System.out.printf("===%ncommit: %s%nDate: %s%n%s%n",
                        hash, ts, me);
            }
            if (p == null) {
                return;
            }
            time = l.get(0);
            hf = Utils.join(Commit_DIR, p);
            hc = Utils.readObject(hf, Commit.class);
            l = hc.returnInfo();
        }
    }

    /**
     * displays information about all commits ever made.
     * The order of the commits does not matter.
     */
    public static void global_log() {
        List<String> l = plainFilenamesIn(Commit_DIR);
        File f;
        Commit c;

        String me;
        String ts; /* timestamp */
        String hash;
        String par;
        String secPar;
        for (String s : l) {
            f = Utils.join(Commit_DIR, s);
            c = Utils.readObject(f, Commit.class);
            l = c.returnInfo();
            ts = l.get(0);
            me = l.get(1);
            hash = l.get(2);
            if (l.size() > 4) {
                par = l.get(4);
                secPar = l.get(5);
                System.out.printf("===%ncommit: %s%nMerge: %s %s%nDate: %s%n%s%n",
                        hash, par, secPar, ts, me);
            } else {
                System.out.printf("===%ncommit: %s%nDate: %s%n%s%n",
                        hash, ts, me);
            }
        }
    }

    /**
     * Prints out the ids of all commits that have the given commit message
     * If there are multiple such commits, it prints the ids out on separate lines
     *
     */
    public static void find(String fm) {
        List<String> l = plainFilenamesIn(Commit_DIR);
        File f;
        Commit c;
        String m;
        List<String> arr_c = new ArrayList<>();

        for (String s : l) {
            f = Utils.join(Commit_DIR, s);
            c = Utils.readObject(f, Commit.class);
            List<String> la = c.returnInfo();
            m = la.get(1);
            if (Objects.equals(fm, m)) {
                arr_c.add(la.get(2));
            }
        }
        if (arr_c.isEmpty()) {
            System.out.println("Found no commit with that message.");
        } else {
            for (String s : arr_c) {
                System.out.println(s);
            }
        }

    }

    /**
     * Displays what branches currently exist,and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     * An example of the exact format it should follow is as follows.
     * === Branches ===
     * *master
     * other-branch
     * <p>
     * === Staged Files ===
     * wug.txt
     * wug2.txt
     * <p>
     * === Removed Files ===
     * goodbye.txt
     * <p>
     * === Modifications Not Staged For Commit ===
     * junk.txt (deleted)
     * wug3.txt (modified)
     * <p>
     * === Untracked Files ===
     * random.stuff
     *
     */
    public static void status() {
        /*  branches  */
        String currb = Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD"));
        List<String> l = Utils.plainFilenamesIn(ref);
        System.out.println("=== Branches ===");
        for (String s : l) {
            if (Objects.equals(s, currb)) {
                System.out.format("*%s%n", s);
            } else {
                System.out.println(s);
            }
        }
        System.out.println("\n");
        /*  staged files  */
        File f = Utils.join(stage_DIR, "files");
        Set<String> sf;
        System.out.println("=== Staged files ===");

        if (!f.exists()) {
            System.out.println("\n");
        } else {
            sf = readObject(f, TreeMap.class).keySet();
            for (String s : sf) {
                System.out.format("%s%n", s);
            }
        }
        System.out.println("\n");

        /*removed files*/
        l = Utils.plainFilenamesIn(removal);
        System.out.println("=== Removed Files ===");
        if (l != null) {
            for (String s : l) {
                System.out.format("%s%n", s);
            }
        }
        System.out.println("\n");

        /* === Modifications Not Staged For Commit === */
        System.out.println("=== Modifications Not Staged For Commit ===");
        File ccf = Utils.join(Commit_DIR, currHead); /*current commit file*/
        Commit cc = Utils.readObject(ccf, Commit.class);
        l = Utils.plainFilenamesIn(CWD);
        TreeMap<String, String> m = Commit.currCommitFiles_map();
        Set<String> cf = m.keySet();
        /*
        Tracked in the current commit, changed in the working directory, but not staged; or
        Staged for addition, but with different contents than in the working directory; or
        Staged for addition, but deleted in the working directory; or
        Not staged for removal, but tracked in the current commit and deleted from the working directory.
        l -> CWD files
        sf -> staged files
        cf -> current commit files
         */
        if (f.exists()) {
            TreeMap<String, String> stage_m = Utils.readObject(f, TreeMap.class);
            sf = readObject(f, TreeMap.class).keySet();
            for (String s : sf) {
                File cwd_f = Utils.join(CWD, s);
                if (!cwd_f.exists()) {
                    System.out.format("%s(deleted)%n", s);
                }
            }
            for (String s : l) {
                String CWDf_hash = Utils.sha1(Utils.readContentsAsString(Utils.join(CWD, s)));
                if ((cf.contains(s) && !sf.contains(s) && !CWDf_hash.equals(m.get(s))) ||
                        (stage_m.containsKey(s) && !CWDf_hash.equals(stage_m.get(s)))) {
                    System.out.format("%s(modified)%n", s);
                }
            }

        } else {
            for (String s : l) {
                String CWDf_hash = Utils.sha1(Utils.readContentsAsString(Utils.join(CWD, s)));
                if (cf.contains(s) && !CWDf_hash.equals(m.get(s))) {
                    System.out.format("%s(modified)%n", s);
                }
            }

            List<String> rml = Utils.plainFilenamesIn(removal);
            for (String s : cf) {
                File cwd_f = Utils.join(CWD, s);
                if (!cwd_f.exists()) {
                    System.out.format("%s(deleted)%n", s);
                }
            }
            if (rml != null) {
                for (String s : rml) {
                    l = Utils.plainFilenamesIn(CWD);
                    if (l != null && cf.contains(s) && !l.contains(s)) {
                        System.out.format("%s(deleted)%n", s);
                    }
                }
            }
        }
        System.out.println("\n");
    /** Untracked Files.
    * Files present in the working directory
    * but neither staged for addition nor tracked.  */
        System.out.println("=== Untracked Files ===");
        l = Utils.plainFilenamesIn(CWD);

        if (l != null) {
            sf = f.exists()
                    ? readObject(f, TreeMap.class).keySet()
                    : Collections.emptySet();

            for (String s : l) {
                if (!cf.contains(s) && !sf.contains(s)) {
                    System.out.format("%s%n", s);
                }
            }
        }
        System.out.println("\n");
    }
}



