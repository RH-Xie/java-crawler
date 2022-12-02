import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;

/**
 * Crawling news from github news
 *
 * @author hu
 */
public class DemoAnnotatedAutoNewsCrawler extends BreadthCrawler {
  private HashMap<Integer, String> content = new HashMap<Integer, String>();
  /**
   * @param crawlPath crawlPath is the path of the directory which maintains
   *                  information of this crawler
   * @param autoParse if autoParse is true,BreadthCrawler will auto extract
   *                  links which match regex rules from pag
   */
  public DemoAnnotatedAutoNewsCrawler(String crawlPath, boolean autoParse) {
    super(crawlPath, autoParse);
    /* start pages */
    int start_day = 21;
    int start_month = 11;
    // int end_day = 18;
    // int end_month = 12;
    int day = start_day;
    int month = start_month;
    int today = 2;
    int this_month = 12;

    while(month <= this_month || day <= today) {
      String url = "https://tiyu.baidu.com/match/%E4%B8%96%E7%95%8C%E6%9D%AF/date_time/2022-"+ month + "-"+ day +"/tab/%E8%B5%9B%E7%A8%8B/from/baidu_aladdin";
      this.addSeed(url);
      day++;
      if(day > 31) {
        day = 1;
        month++;
      }
    }    /* fetch url like "https://blog.github.com/2018-07-13-graphql-for-octokit/" */
    this.addRegex("https://tiyu.baidu.com/match/%E4%B8%96%E7%95%8C%E6%9D%AF/date_time/.*");
    /* do not fetch jpg|png|gif */
    // this.addRegex("-.*\\.(jpg|png|gif).*");
    /* do not fetch url contains # */
    // this.addRegex("-.*#.*");

    setThreads(50);
    getConf().setTopN(100);
    // enable resumable mode
    // setResumable(true);
  }

  @MatchUrl(urlRegex = "https://tiyu.baidu.com/match/%E4%B8%96%E7%95%8C%E6%9D%AF/date_time/.*")
  public void visitNews(Page page, CrawlDatums next) {
    /* extract title and content of news by css selector */
    Element today_games = page.select("div.wa-match-schedule-list-wrapper").first();
    Elements divGroups = today_games.select("div.wa-match-schedule-list-item");
    Element date = today_games.select("div.date").first();
    System.out.println();
    String newLine = date.text();
    System.out.println("==========" + date.text() + " 赛程 ==========");
    for (Element divGroup : divGroups) {
      String groupClass = divGroup.select("p.c-line-clamp").text();
      Elements divTeams = divGroup.select("div.team-row");
      System.out.println();
      System.out.println(groupClass);
      newLine += ", " + groupClass;
      String teamName = divTeams.get(0).select("div.team-row-name > span.c-line-clamp1").text();
      String teamScore = divTeams.get(0).select("div.team-row-score > span").text();
      String display = teamName + " " + teamScore;
      teamName = divTeams.get(1).select("div.team-row-name > span.c-line-clamp1").text();
      teamScore = divTeams.get(1).select("div.team-row-score > span").text();
      display += " vs " + teamScore + " " + teamName;
      newLine += "|" + display;
      System.out.println(display);
    }
    String[] stringKey = date.text().substring(0, 5).split("-");
    int key = Integer.parseInt(stringKey[0] + stringKey[1]);
    content.put(key ,newLine);
  }

  @Override
  public void visit(Page page, CrawlDatums next) {
    System.out.println("visit pages that don't match any annotation rules: " + page.url());
  }

  public HashMap<Integer, String> getContent() {
    content = Utils.sortByKey(content);
    return content;
  }

  public static void main(String[] args) throws Exception {
    DemoAnnotatedAutoNewsCrawler crawler = new DemoAnnotatedAutoNewsCrawler("crawl", true);
    /* start crawl with depth of 4 */
    crawler.start(1);
    Utils.csvWriter("./data.csv", crawler.getContent());
  }
}
