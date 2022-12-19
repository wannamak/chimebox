package chimebox.midi;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MidiFileDatabase {
  private List<File> chimeFiles = new ArrayList<>();
  private List<List<Integer>> possibleTranspositions = new ArrayList<>();

  public MidiFileDatabase() {
    chimeFiles.add(new File("./music/westminster.mid"));
    possibleTranspositions.add(ImmutableList.of(0, /*-8, -6, -5, */ -4, -3, -1));

    chimeFiles.add(new File("./music/whittington.mid"));
    possibleTranspositions.add(ImmutableList.of(-3, 0, 2));

    chimeFiles.add(new File("./music/soissons.mid"));
    possibleTranspositions.add(ImmutableList.of(0, -1, 2 /*, 4, 6, 7, 8, 9 */));

    chimeFiles.add(new File("./music/st-michaels.mid"));
    possibleTranspositions.add(ImmutableList.of(-6 /* -3, -1 */));

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
