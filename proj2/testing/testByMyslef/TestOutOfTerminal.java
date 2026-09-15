package testing.testByMyslef;

import gitlet.Commit;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.TreeMap;


import static com.google.common.truth.Truth.assertThat;
import static gitlet.Blob.resetStage;
import static gitlet.Blob.saveToStage;
import static gitlet.Commit.branch;
import static gitlet.Commit.commitInCurrBran;
import static gitlet.Repository.*;

public class TestOutOfTerminal {

    @Test
    public void testInit() throws IOException {
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
        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        assertThat(t).isEqualTo(currHead);
        saveToStage("456.txt");
        t = commitInCurrBran("test456");
        assertThat(t).isEqualTo(currHead);
        assertThat(t).isEqualTo(Utils.readContentsAsString(Utils.join(ref, "master")));
    }

    @Test
    public void testBranch() throws IOException {
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

        File test123 = new File("./123.txt");
        Utils.writeContents(test123, "Hello");
        saveToStage("123.txt");
        String t = commitInCurrBran("test123");
        String a = t;
        Commit t123 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        saveToStage("456.txt");
        branch("dev");
        t = commitInCurrBran("test456");

        String cb = Utils.readContentsAsString(HEAD);
        assertThat(cb).isEqualTo("dev");

        String dh = Utils.readContentsAsString(Utils.join(ref, "dev"));
        String mh = Utils.readContentsAsString(Utils.join(ref, "master"));
        assertThat(mh).isEqualTo(a);
        assertThat(dh).isEqualTo(t);

        Commit t456 = Utils.readObject(Utils.join(Commit_DIR, t), Commit.class);

        t123.dump();
        t456.dump();
    }

    @Test
    public void testAdd() throws IOException {
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
}
