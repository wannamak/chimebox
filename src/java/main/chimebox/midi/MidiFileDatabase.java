package chimebox.midi;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiFileDatabase {
  private List<File> chimeFiles = new ArrayList<>();
  private List<List<Integer>> possibleTranspositions = new ArrayList<>();

  public static final int WESTMINSTER = 0;
  public static final int WHITTINGTON = 1;
  public static final int SOISSONS = 2;
  public static final int ST_MICHAELS = 3;
  public static final int HAPPY_BIRTHDAY = 4;
  public static final int BEECH_SPRING = 5;

  public MidiFileDatabase() {
    chimeFiles.add(new File("./music/westminster.mid"));
    possibleTranspositions.add(ImmutableList.of(0, /*-8, -6, -5, */ -4, -3, -1));

    chimeFiles.add(new File("./music/whittington.mid"));
    possibleTranspositions.add(ImmutableList.of(-3, 0, 2));

    chimeFiles.add(new File("./music/soissons.mid"));
    possibleTranspositions.add(ImmutableList.of(0, -1, 2 /*, 4, 6, 7, 8, 9 */));

    chimeFiles.add(new File("./music/st-michaels.mid"));
    possibleTranspositions.add(ImmutableList.of(-6 /* -3, -1 */));

    chimeFiles.add(new File("./music/birthday.mid"));
    possibleTranspositions.add(ImmutableList.of(-1, 2));

    chimeFiles.add(new File("./music/beech.mid"));
    possibleTranspositions.add(ImmutableList.of(-1, 2));

    for (File chimeFile : chimeFiles) {
      if (!chimeFile.exists()) {
        throw new IllegalStateException("File not found: " + chimeFile.getAbsolutePath());
      }
    }
  }

  public int getFileListSize() {
    return chimeFiles.size();
  }

  public File getFile(int fileIndex) {
    return chimeFiles.get(fileIndex);
  }

  public List<Integer> getPossibleTranspositions(File file) {
    int fileIndex = chimeFiles.indexOf(file);
    return possibleTranspositions.get(fileIndex);
  }
}
