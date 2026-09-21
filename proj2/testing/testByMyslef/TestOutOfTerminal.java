package testing.testByMyslef;

import gitlet.Commit;
import gitlet.Repository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;


import static com.google.common.truth.Truth.assertThat;
import static gitlet.Blob.resetStage;
import static gitlet.Blob.saveToStage;
import static gitlet.Commit.*;
import static gitlet.Repository.*;

public class TestOutOfTerminal {

    private static final Logger log = LoggerFactory.getLogger(TestOutOfTerminal.class);

    public void reInit() throws IOException {
        Path folder = Paths.get(GITLET_DIR.toURI());

        Files.walk(folder)
                .sorted(Comparator.reverseOrder())
                .filter(path -> !path.equals(folder))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        GITLET_DIR.delete();
        assertThat(GITLET_DIR.exists()).isFalse();
        init();
    }

    @Test
    public void testInit() throws IOException {
        reInit();
        assertThat("D:\\IDEA2025\\IDEAProject\\skeleton-sp21\\proj2\\.gitlet").
                isEqualTo(GITLET_DIR.toString());
    }

    @Test
    public void testStage() throws IOException {
        resetStage();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        System.out.println(Utils.sha1(Utils.readContentsAsString(test123)));
        saveToStage("123.txt");
        saveToStage("123.txt");
        TreeMap m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.containsKey("123.txt")).isTrue();
        assertThat(m.get("123.txt")).isEqualTo(Utils.
                sha1(Utils.readContentsAsString(test123)));
        Utils.writeContents(test123, "Bye");
        System.out.println(Utils.sha1(Utils.readContentsAsString(test123)));
        saveToStage("123.txt");
        m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.get("123.txt")).isEqualTo(Utils.
                sha1(Utils.readContentsAsString(test123)));

        saveToStage("456.txt");
        m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.containsKey("456.txt")).isTrue();
    }

    @Test
    public void teseNewFiletxt() throws IOException {
        TreeMap<String, String> m = new TreeMap<>();
        String n1 = "123.txt";
        File f1 = new File(n1);
        String t1 = Utils.readContentsAsString(f1);
        String h1 = Utils.sha1(t1);
        m.put(n1, h1);
        System.out.println(m.toString());
        File save = Utils.join(CWD, "save");
        save.createNewFile();
        Utils.writeObject(save, m);
        TreeMap nm = Utils.readObject(save, TreeMap.class);
        System.out.println(nm.toString());
    }

    @Test
    public void testCommitInCurr() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        assertThat(t).isEqualTo(currHead);
        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        t = commitInCurrBran("test456");
        assertThat(t).isEqualTo(currHead);
        assertThat(t).isEqualTo(Utils.readContentsAsString(Utils.join(ref, "master")));
    }

    @Test
    public void testBranch() throws IOException {
        reInit();

        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        String a = t;
        Commit t123 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        branch("dev");
        Utils.writeContents(HEAD, "dev");
        t = commitInCurrBran("test456");

        String cb = Utils.readContentsAsString(HEAD); /* current branch */
        assertThat(cb).isEqualTo("dev");

        String dh = Utils.readContentsAsString(Utils.join(ref, "dev")); /*  dev hash */
        String mh = Utils.readContentsAsString(Utils.join(ref, "master"));/* master hash*/
        assertThat(mh).isEqualTo(a);
        assertThat(dh).isEqualTo(t);

        Commit t456 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        t123.dump();
        t456.dump();
    }

    @Test
    public void testAdd() throws IOException {
        reInit();

        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        add("123.txt");
        commitInCurrBran("123");

        Utils.writeContents(test123, "Bye");
        add("123.txt");
        TreeMap m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.containsKey("123.txt")).isTrue();

        Utils.writeContents(test123, "Hello");
        add("123.txt");
        m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.containsKey("123.txt")).isFalse();

        Utils.writeContents(test123, "Bye");
        add("123.txt");
        add("456.txt");
        m = Utils.readObject(Utils.join(stage_DIR, "files"),
                java.util.TreeMap.class);
        assertThat(m.containsKey("456.txt")).isTrue();
    }


    @Test
    public void testrm() throws IOException {
        reInit();

        File test123 = new File("123.txt");
        Utils.writeContents(test123, "Hello");
        add("123.txt");
        commitInCurrBran("123");
        Utils.writeContents(test123, "Bye");
        add("123.txt");
        File fs = Utils.join(stage_DIR, "files");
        TreeMap<String, String> m = Utils.readObject(fs, java.util.TreeMap.class);
        assertThat(m.containsKey("123.txt")).isTrue();
        rm("123.txt");
        m = Utils.readObject(fs, java.util.TreeMap.class);
        assertThat(m.containsKey("123.txt")).isFalse();

        rm("123.txt");
        assertThat(test123.exists()).isFalse();

    }

    @Test
    public void testLog() throws IOException {
        reInit();

        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        commitInCurrBran("123.txt");
        Repository.log();

        saveToStage("456.txt");
        commitInCurrBran("456.txt");
        Repository.log();
        Repository.global_log();
    }

    @Test
    public void testFind() throws IOException {
        reInit();

        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        commitInCurrBran("same");

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        commitInCurrBran("same");
        Repository.find("same");
        Repository.find("diff");
    }

    @Test
    public void teststatus() throws IOException {
        reInit();

        Repository.status();
        File test123 = Utils.join(CWD, "123.txt");
        Utils.writeContents(test123, "Hello");
        if (!test123.exists()) {
            test123.createNewFile();
        }
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        Commit t123 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);
        Repository.status();

        System.out.println("test123 changed");
        Utils.writeContents(test123, "Bye");
        Repository.status();
        System.out.println("test123 deleted");
        rm("123.txt");
        Repository.status();

        File t456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(t456, "World");
        saveToStage("456.txt");
        branch("dev");
        Utils.writeContents(HEAD, "dev");
        t = commitInCurrBran("test456");
        Repository.status();

        File test789 = Utils.join(CWD, "789.txt");
        if (!test789.exists()) {
            test789.createNewFile();
        }
        Utils.writeContents(test789, "Zelda");
        saveToStage("789.txt");
        rm("789.txt");
        Repository.status();

        saveToStage("789.txt");
        test789.delete();
        Repository.status();

    }

    @Test
    public void testcheck1() throws IOException {
        reInit();

        File test123 = Utils.join(CWD, "123.txt");
        Utils.writeContents(test123, "Hello");
        if (!test123.exists()) {
            test123.createNewFile();
        }
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");

        test123.delete();
        assertThat(test123.exists()).isFalse();

        check_file("123.txt");
        assertThat(test123.exists()).isTrue();

        Utils.writeContents(test123, "Bye");
        check_file("123.txt");
        String s = Utils.readContentsAsString(test123);
        assertThat(s).isEqualTo("Hello");


    }

    @Test
    public void testcheck2() throws IOException {
        reInit();

        File test123 = Utils.join(CWD, "123.txt");
        Utils.writeContents(test123, "Hello");
        if (!test123.exists()) {
            test123.createNewFile();
        }
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");

        test123.delete();
        assertThat(test123.exists()).isFalse();

        Utils.writeContents(test123, "Bye");
        saveToStage("123.txt");
        commitInCurrBran("test123");

        check_id(t, "123.txt");
        assertThat(test123.exists()).isTrue();

        String s = Utils.readContentsAsString(test123);
        assertThat(s).isEqualTo("Hello");

        check_id("13214", "123");
        check_id(t, "123");



    }

    @Test
    public void testcheck3() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        String a = t;
        Commit t123 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        branch("dev");
        Utils.writeContents(HEAD, "dev");
        t = commitInCurrBran("test456");


        check_branch("master");
        assertThat(Utils.readContentsAsString(Utils.join(GITLET_DIR, "HEAD")))
                .isEqualTo("master");
        assertThat(test456.exists()).isFalse();


    }

    @Test
    public void testrm_branch() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        String a = t;
        Commit t123 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        branch("dev");
        Utils.writeContents(HEAD, "dev");
        t = commitInCurrBran("test456");


        check_branch("master");
        rm_branch("dev");
        assertThat(Utils.plainFilenamesIn(ref).contains("dev")).isFalse();


    }
    @Test
    public void testreset() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test123, "Good");
        Utils.writeContents(test456, "World");
        saveToStage("456.txt");
        saveToStage("123.txt");
        branch("dev");
        commitInCurrBran("456");
        Utils.writeContents(HEAD, "dev");
        File test789 = Utils.join(CWD, "789.txt");
        Utils.writeContents(test789, "Link");

        reset(t);
        assertThat(test789.exists()).isTrue();
        assertThat(test456.exists()).isFalse();
        assertThat(Utils.readContentsAsString(test123).equals("Good")).isFalse();
        assertThat(Utils.readContentsAsString(test123).equals("Hello")).isTrue();
    }

    @Test
    public void testMergeFastForward() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t1 = commitInCurrBran("initial master");

        branch("dev");
        check_branch("dev");

        File test456 = Utils.join(CWD, "456.txt");
        Utils.writeContents(test456, "Dev World");
        saveToStage("456.txt");
        saveToStage("123.txt");
        String t2 = commitInCurrBran("dev commit");

        check_branch("master");
        assertThat(test456.exists()).isFalse();

        merge("dev");
        assertThat(test456.exists()).isTrue();
        assertThat(Utils.readContentsAsString(test456)).isEqualTo("Dev World");
        assertThat(currHead).isEqualTo(t2);
    }

    @Test
    public void testMergeAncestor() throws IOException {
        reInit();
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        commitInCurrBran("commit 1");

        branch("dev");

        Utils.writeContents(test123, "Hello World");
        saveToStage("123.txt");
        String masterHead = commitInCurrBran("commit 2");

        merge("dev");
        assertThat(currHead).isEqualTo(masterHead);
        assertThat(Utils.readContentsAsString(test123)).isEqualTo("Hello World");
    }

    @Test
    public void testMergeNormalThreeWay() throws IOException {
        reInit();
        // Split point setup
        File f1 = new File("./f1.txt");
        File f2 = new File("./f2.txt");
        File f3 = new File("./f3.txt");
        Utils.writeContents(f1, "f1 init\n");
        Utils.writeContents(f2, "f2 init\n");
        Utils.writeContents(f3, "f3 init\n");
        saveToStage("f1.txt");
        saveToStage("f2.txt");
        saveToStage("f3.txt");
        String splitId = commitInCurrBran("split commit");

        branch("dev");

        // Master branch modifications
        Utils.writeContents(f1, "f1 modified in master\n");
        File f4 = new File("./f4.txt");
        Utils.writeContents(f4, "f4 master only\n");
        saveToStage("f1.txt");
        saveToStage("f2.txt");
        saveToStage("f3.txt");
        saveToStage("f4.txt");
        String masterCommit = commitInCurrBran("master changes");

        // Dev branch modifications
        check_branch("dev");
        Utils.writeContents(f2, "f2 modified in dev\n");
        File f5 = new File("./f5.txt");
        Utils.writeContents(f5, "f5 dev only\n");
        saveToStage("f1.txt");
        saveToStage("f2.txt");
        saveToStage("f5.txt");
        // Remove f3 from dev
        rm("f3.txt");
        String devCommit = commitInCurrBran("dev changes");

        // Switch back to master and merge dev
        check_branch("master");
        merge("dev");

        // Rule 1: f2 modified in given, not in curr -> taken from dev
        assertThat(Utils.readContentsAsString(f2)).isEqualTo("f2 modified in dev\n");
        // Rule 2: f1 modified in curr, not in given -> kept from master
        assertThat(Utils.readContentsAsString(f1)).isEqualTo("f1 modified in master\n");
        // Rule 4: f4 present only in master -> kept
        assertThat(Utils.readContentsAsString(f4)).isEqualTo("f4 master only\n");
        // Rule 5: f5 present only in dev -> added
        assertThat(Utils.readContentsAsString(f5)).isEqualTo("f5 dev only\n");
        // Rule 6: f3 absent in dev, unmodified in master -> deleted
        assertThat(f3.exists()).isFalse();

        // Verify merge commit parents
        List<String> parents = Commit.return_parents(currHead);
        assertThat(parents.size()).isEqualTo(2);
        assertThat(parents.get(0)).isEqualTo(masterCommit);
        assertThat(parents.get(1)).isEqualTo(devCommit);
    }

    @Test
    public void testMergeConflict() throws IOException {
        reInit();
        File f1 = new File("./f1.txt");
        Utils.writeContents(f1, "base content\n");
        saveToStage("f1.txt");
        commitInCurrBran("base commit");

        branch("dev");

        Utils.writeContents(f1, "master content\n");
        saveToStage("f1.txt");
        String masterCommit = commitInCurrBran("master modify");

        check_branch("dev");
        Utils.writeContents(f1, "dev content\n");
        saveToStage("f1.txt");
        String devCommit = commitInCurrBran("dev modify");

        check_branch("master");
        merge("dev");

        String expected = "<<<<<<< HEAD\nmaster content\n=======\ndev content\n>>>>>>>\n";
        assertThat(Utils.readContentsAsString(f1)).isEqualTo(expected);

        List<String> parents = Commit.return_parents(currHead);
        assertThat(parents.size()).isEqualTo(2);
        assertThat(parents.get(0)).isEqualTo(masterCommit);
        assertThat(parents.get(1)).isEqualTo(devCommit);
    }

    @Test
    public void testRemoteOperations() throws IOException {
        reInit();
        File remoteDir = new File(CWD, "remote_repo");
        if (remoteDir.exists()) {
            Files.walk(remoteDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        File remoteGitlet = new File(remoteDir, ".gitlet");
        remoteGitlet.mkdirs();
        new File(remoteGitlet, "commits").mkdirs();
        new File(remoteGitlet, "blobs").mkdirs();
        new File(remoteGitlet, "ref").mkdirs();

        add_remote("origin", remoteGitlet.getPath());
        File rf = new File(remotes_DIR, "origin");
        assertThat(rf.exists()).isTrue();

        rm_remote("origin");
        assertThat(rf.exists()).isFalse();

        if (remoteDir.exists()) {
            Files.walk(remoteDir.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
