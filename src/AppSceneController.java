import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class AppSceneController {

    @FXML
    private ImageView closeBtn;

    @FXML
    private ChoiceBox<?> dateChooseBox;

    @FXML
    private Label startBtn;

    @FXML
    void onCloseClick(MouseEvent event) {
      Platform.exit();
    }

    @FXML
    void onCrawlClick(MouseEvent event) {
      WorldCupCrawler crawler = new WorldCupCrawler("crawl", true);
      /* start crawl with depth of 4 */
      try {
        crawler.start(1);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Utils.csvWriter("./data.csv", crawler.getContent());
    }

}
