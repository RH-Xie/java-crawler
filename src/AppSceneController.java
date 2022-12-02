import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AppSceneController {
    private ExecutorService pool = Executors.newFixedThreadPool(20);
    private long cooldown = 0;

    @FXML
    private ImageView closeBtn;

    @FXML
    private ChoiceBox<String> dateChooseBox;

    @FXML
    private Label startBtn;

    @FXML
    private Label openCSVBtn;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label tipLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    void onCloseClick(MouseEvent event) {
      System.exit(0);
    }

    @FXML
    void onTipClick(MouseEvent event) {
      onCrawlClick(event);
    }

    @FXML
    void onOpenCSV(MouseEvent event) {
      Utils.openFile("./data.csv");
    }

    @FXML
    void onCrawlClick(MouseEvent event) {
      if(this.cooldown > System.currentTimeMillis() - 5000) {
        return;
      }
      cooldown = System.currentTimeMillis();
      this.tipLabel.setText("正在爬取，请稍后...");
      this.progressBar.setVisible(true);
      CrawlTask task = new CrawlTask();
      pool.submit(task);
      Runnable renderTask = () -> {
        while(!task.isCrawling()) {
          try {
            Thread.sleep(30);
            if(progressBar.getProgress() < 0.93) {
              progressBar.setProgress(progressBar.getProgress() + 0.010);
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        this.progressBar.setVisible(false);
        this.tipLabel.setVisible(false);
        this.openCSVBtn.setVisible(true);
        System.out.println("完成！");
        // 更新界面
        Platform.runLater(() -> {
          HashMap<Integer, String> result = task.getResult();
          dateChooseBox.getItems().clear();
          for(Integer key : result.keySet()) {
            String item = key / 100 + "月" + key % 100 + "日";
            dateChooseBox.getItems().add(item);
          }
          gridPane.setAlignment(Pos.CENTER);
          System.out.println("111");
          // this.gridPane = new GridPane();
          // dateChooseBox.setItems(FXCollections.observableArrayList(result.keySet().toArray(new String[0])));
          dateChooseBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(javafx.beans.value.ObservableValue<? extends String> observable, String oldValue, String newValue) {
              gridPane.getChildren().clear();
              String[] date = newValue.split("月|日");
              int key = Integer.parseInt(date[0]) * 100 + Integer.parseInt(date[1]);
              String value = result.get(key);
              String[] games = value.split(",");
              for(int i = 1; i < games.length; i++) {
                TextFlow textFlow = new TextFlow();
                String[] game = games[i].split("vs");
                String group = game[0].split("\\|")[0];
                Label groupLabel = new Label(group);
                groupLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, FontPosture.REGULAR, 12));
                gridPane.add(groupLabel, 0, i - 1);

                String team1 = game[0].split("\\|")[1].trim();
                System.out.println(team1);
                Text info = new Text(team1.substring(0, team1.length() - 2)); // 主队
                info.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, FontPosture.REGULAR, 14));
                game[0] = game[0].trim();
                Text score = new Text(team1.substring(team1.length() - 2)); // 比分
                score.setFill(Color.RED);
                score.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, FontPosture.REGULAR, 20));
                textFlow.getChildren().addAll(info, score);
                gridPane.add(textFlow, 1, i - 1);
                Utils.getNodeByRowColumnIndex(i - 1, 1, gridPane).setStyle("-fx-alignment: center-right;");

                String team2 = game[1].trim();
                System.out.println(team2);
                info = new Text(team2.substring(2)); // 客队
                info.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, FontPosture.REGULAR, 14));

                score = new Text(team2.substring(0, 2)); // 比分
                score.setFill(Color.RED);
                score.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, FontPosture.REGULAR, 20));
                textFlow = new TextFlow();
                textFlow.getChildren().addAll(score, info);
                gridPane.add(textFlow, 2, i - 1);
                Utils.getNodeByRowColumnIndex(i - 1, 2, gridPane).setStyle("-fx-alignment: center-left;");
              }
            }
          });
          dateChooseBox.setVisible(true);
          gridPane.setVisible(true);
          
          dateChooseBox.getSelectionModel().selectFirst();
        });
      };
      pool.submit(renderTask);
    }

    class CrawlTask implements Callable<Void> {
      private boolean isCrawling = false;
      private HashMap<Integer, String> result = new HashMap<>();

      @Override
      public Void call() throws Exception {
        WorldCupCrawler crawler = new WorldCupCrawler("crawl", true);
        try {
          crawler.start(1);
        } catch (Exception e) {
          e.printStackTrace();
        }
        Utils.csvWriter("./data.csv", crawler.getContent());
        this.result = crawler.getContent();
        isCrawling = true;
        return null;
      }

      public boolean isCrawling() {
        return isCrawling;
      }

      public HashMap<Integer, String> getResult() {
        return result;
      }
    }
}
