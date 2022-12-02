import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.TreeMap;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Utils {
  public static HashMap<Integer, String> csvReader(String path) {
    HashMap<Integer, String> map = new HashMap<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(path));
      String line = "";
      while ((line = br.readLine()) != null) {
        String[] values = line.split(",");
        System.out.println(values[0] + " " + values[1]);
        String[] date = values[0].substring(0, 5).split("-");
        int key = Integer.parseInt(date[0] + date[1]);
        map.put(key, line);
      }
      br.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return map;
  }

  public static boolean csvWriter(String path, HashMap<Integer, String> content) {
    if(!Path.of(path).toFile().exists()) {
      try {
        Files.createDirectories(Path.of(path).getParent());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    if(!path.endsWith(".csv")) {
      System.out.println("The file is not a csv file.");
      return false;
    }
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(path));
      for(String line : content.values()) {
        bw.write(line);
        bw.newLine();
      }
      System.out.println("Write successfully.");
      bw.close();
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static HashMap<Integer, String> sortByKey(HashMap<Integer, String> messageList) {
    TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>(messageList);
    HashMap<Integer, String> result = new HashMap<Integer, String>();
    System.out.println("sortByKey");
    for (Integer key : treeMap.keySet()) {
      result.put(key, treeMap.get(key));
    }
    return result;
  }

  public static Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
    Node result = null;
    ObservableList<Node> childrens = gridPane.getChildren();
  
    for (Node node : childrens) {
      if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
        result = node;
        break;
      }
    }
    return result;
  }
}
