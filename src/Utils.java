import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class Utils {
  public static boolean csvReader(String path) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      String line = "";
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        System.out.println(values[0] + " " + values[1]);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static boolean csvWriter(String path, String content) {
    if(!Path.of(path).toFile().exists()) {
      System.out.println("Cannot create directory: " + path);
      return false;
    }
    if(!path.endsWith(".csv")) {
      System.out.println("The file is not a csv file.");
      return false;
    }
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(path));
      bw.write(content);
      bw.newLine();
      bw.write("11,12,13,14,15,16,17,18,19,20");
      bw.newLine();
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
