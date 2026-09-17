package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Blob.resetStage;
import static gitlet.Blob.saveToStage;
import static gitlet.Commit.currCommitFiles_map;
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
            writeContents(HEAD, "master");
            //TODO: create an initial commit

        }
    }

    /**
     *
     */
    public static void commit(String message) {
        File fs = join(stage_DIR, "files");
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
        File f = join(CWD, name);
        if (!f.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else {
            Commit c = readObject(join(Commit_DIR, currHead), Commit.class);
            String hash = sha1(readContentsAsString(f));
            TreeMap<String, String> m = currCommitFiles_map();
            if (join(stage_DIR, "files").exists() && m.containsKey(name) && m.get(name).equals(hash)) {
                File copyInStage = join(stage_DIR, "files");
                TreeMap<String, String> text = readObject(copyInStage, TreeMap.class);
                text.remove(name);
                writeObject(copyInStage, text);
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
        File CWDf = join(CWD, name);
        File fs = join(stage_DIR, "files");
        if (fs.exists()) {
            TreeMap<String, String> m = readObject(fs, TreeMap.class);
            if (m.containsKey(name)) {
                m.remove(name);
                writeObject(fs, m);
                String content = readContentsAsString(CWDf);
                File r = join(removal, name);
                if (r.exists()) {
                    writeContents(r, content);
                } else {
                    r.createNewFile();
                    writeContents(r, content);
                }
                return;
            }
        }
        if (currCommitFiles_map().containsKey(name)) {
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
        File cf = join(Commit_DIR, currHead);
        Commit cc = readObject(cf, Commit.class);
        p = cc.returnInfo().get(3);
        File hf = join(Commit_DIR, p);
        List<String> l;
        while (!time.equals(initTime)) {
            hc = readObject(hf, Commit.class);
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
            hf = join(Commit_DIR, p);
            hc = readObject(hf, Commit.class);
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
            f = join(Commit_DIR, s);
            c = readObject(f, Commit.class);
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
            f = join(Commit_DIR, s);
            c = readObject(f, Commit.class);
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
        String currb = readContentsAsString(join(GITLET_DIR, "HEAD"));
        List<String> l = plainFilenamesIn(ref);
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
        File f = join(stage_DIR, "files");
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
        l = plainFilenamesIn(removal);
        System.out.println("=== Removed Files ===");
        if (l != null) {
            for (String s : l) {
                System.out.format("%s%n", s);
            }
        }
        System.out.println("\n");

        /* === Modifications Not Staged For Commit === */
        System.out.println("=== Modifications Not Staged For Commit ===");
        File ccf = join(Commit_DIR, currHead); /*current commit file*/
        Commit cc = readObject(ccf, Commit.class);
        l = plainFilenamesIn(CWD);
        TreeMap<String, String> m = currCommitFiles_map();
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
            TreeMap<String, String> stage_m = readObject(f, TreeMap.class);
            sf = readObject(f, TreeMap.class).keySet();
            for (String s : sf) {
                File cwd_f = join(CWD, s);
                if (!cwd_f.exists()) {
                    System.out.format("%s(deleted)%n", s);
                }
            }
            for (String s : l) {
                String CWDf_hash = sha1(readContentsAsString(join(CWD, s)));
                if ((cf.contains(s) && !sf.contains(s) && !CWDf_hash.equals(m.get(s))) ||
                        (stage_m.containsKey(s) && !CWDf_hash.equals(stage_m.get(s)))) {
                    System.out.format("%s(modified)%n", s);
                }
            }

        } else {
            for (String s : l) {
                String CWDf_hash = sha1(readContentsAsString(join(CWD, s)));
                if (cf.contains(s) && !CWDf_hash.equals(m.get(s))) {
                    System.out.format("%s(modified)%n", s);
                }
            }

            List<String> rml = plainFilenamesIn(removal);
            for (String s : cf) {
                File cwd_f = join(CWD, s);
                if (!cwd_f.exists()) {
                    System.out.format("%s(deleted)%n", s);
                }
            }
            if (rml != null) {
                for (String s : rml) {
                    l = plainFilenamesIn(CWD);
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
        l = plainFilenamesIn(CWD);

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

    /**
     * checkout -- [file name]
     * Takes the version of the file as it exists in the head commit and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     *
     */
    public static void check_file(String name) throws IOException {
        TreeMap<String,String> curr_com_m = currCommitFiles_map();
        if (!curr_com_m.containsKey(name)) {
            System.out.println("File does not exist in that commit.");
        }else {
            File cwd_f = join(CWD, name);
            String hash = curr_com_m.get(name);
            String text = readContentsAsString(join(blob_DIR, hash));
            writeContents(cwd_f, text);
            if (!cwd_f.exists()) {
                cwd_f.createNewFile();
            }
        }
    }
    /**
     *  checks the commit with the given id, and puts it in the working directory,
     *  overwriting the version of the file that’s already there if there is one.
     *  The new version of the file is not staged.
     */
    public static void check_id (String id, String name) throws IOException {
        String full_id = findCommitId(id);
        File aim_com = join(Commit_DIR, full_id);
        if (!aim_com.exists()) {
            System.out.println("No commit with that id exists.");
        }else {
            String p = currHead;
            currHead = full_id;
            check_file(name);
            currHead = p;
        }
    }

    /**
     * find the id by using characters fewer than 40
     *
     */
    private static String findCommitId(String id) {
        List<String> commits_name = plainFilenamesIn(Commit_DIR);
        String result = "A";
        if (commits_name == null) {
            return result;
        }
        int i = 0;
        for (String s : commits_name) {
            if (s.startsWith(id)) {
                result = s;
            }
        }
        return result;
    }

    /**
     *  The commit at the head of the given branch, and puts them in the working directory,
     *      overwriting the versions of the files that are already there if they exist.
     *  At the end of this command,
     *      the given branch will now be considered the current branch (HEAD)
     *  Files that are tracked in the current branch
     *      but are not present in the checked-out branch are deleted.
     *  The staging area is cleared, unless the checked-out branch is the current branch.
     */
    public static void check_branch(String name) throws IOException {
        /*check if is untracked*/
        List<String> l = plainFilenamesIn(CWD);
        File f = Utils.join(stage_DIR, "files");
        Set<String> sf;
        Map<String, String> cf = currCommitFiles_map();

        if (l != null) {
            sf = f.exists()
                    ? readObject(f, TreeMap.class).keySet()
                    : Collections.emptySet();

            for (String s : l) {
                if (!cf.containsKey(s) && !sf.contains(s)) {
                    System.out.println("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");

                }
            }
        }

        /*check if the branch exists*/
        List<String> branches = Utils.plainFilenamesIn(ref);
        if (!branches.contains(name)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        /*check if is the current branch*/
        File head_file = Utils.join(GITLET_DIR, "HEAD");
        if (readContentsAsString(head_file).equals(name)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        File rev_bran = Utils.join(ref, name);
        currHead = Utils.readContentsAsString(rev_bran);
        Utils.writeContents(Utils.join(GITLET_DIR, "HEAD"), name);
        TreeMap<String, String> cm = currCommitFiles_map();
        for (String s : cm.keySet()) {
            File copy = Utils.join(CWD, s);
            String content = Utils.readContentsAsString(Utils.join(blob_DIR, cm.get(s)));
            Utils.writeContents(copy, content);
            if (!copy.exists()) {
                copy.createNewFile();
            }
        }
        for (String s : cf.keySet()) {
            if (!cm.containsKey(s)) {
                Utils.join(CWD, s).delete();
            }
        }
        resetStage();
    }

    /**
     * Reset the current branch to the given commit.
     * The working directory is updated to match the given commit,
     * and the staging area is cleared.
     */
    public static void reset(String id) {

        String full_id = findCommitId(id);

        if (full_id == null) {
            System.out.println("No commit with that id exists.");
            return;
        }

        File aim_com = join(Commit_DIR, full_id);

        if (!aim_com.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Map<String, String> cf = currCommitFiles_map();

        String oldHead = currHead;
        currHead = full_id;
        Map<String, String> targetFiles = currCommitFiles_map();
        currHead = oldHead;

        List<String> files = plainFilenamesIn(CWD);

        if (files != null) {

            File stageFile = Utils.join(stage_DIR, "files");

            Set<String> stagedFiles = stageFile.exists()
                    ? readObject(stageFile, TreeMap.class).keySet()
                    : Collections.emptySet();

            for (String fileName : files) {

                if (!cf.containsKey(fileName)
                        && !stagedFiles.contains(fileName)
                        && targetFiles.containsKey(fileName)) {

                    System.out.println(
                            "There is an untracked file in the way; "
                                    + "delete it, or add and commit it first."
                    );

                    return;
                }
            }
        }

        for (String fileName : cf.keySet()) {

            if (!targetFiles.containsKey(fileName)) {
                Utils.join(CWD, fileName).delete();
            }
        }

        for (String fileName : targetFiles.keySet()) {

            File file = Utils.join(CWD, fileName);

            String content = Utils.readContentsAsString(
                    Utils.join(blob_DIR, targetFiles.get(fileName))
            );

            Utils.writeContents(file, content);
        }

        currHead = full_id;

        String currentBranch = Utils.readContentsAsString(
                Utils.join(GITLET_DIR, "HEAD")
        );

        Utils.writeContents(
                Utils.join(ref, currentBranch),
                full_id
        );

        resetStage();
    }


}



