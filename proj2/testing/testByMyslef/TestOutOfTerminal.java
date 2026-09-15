package testing.testByMyslef;

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
}
