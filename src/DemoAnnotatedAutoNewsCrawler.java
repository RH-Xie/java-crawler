
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.rocks.BreadthCrawler;

/**
 * Crawling news from github news
 *
 * @author hu
 */
public class DemoAnnotatedAutoNewsCrawler extends BreadthCrawler {
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
    int end_day = 18;
    int end_month = 12;
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
    System.out.println("==========" + date.text() + " 赛程 ==========");
    for (Element divGroup : divGroups) {
      String groupClass = divGroup.select("p.c-line-clamp").text();
      Elements divTeams = divGroup.select("div.team-row");
      System.out.println();
      System.out.println(groupClass);
      String teamName = divTeams.get(0).select("div.team-row-name > span.c-line-clamp1").text();
      String teamScore = divTeams.get(0).select("div.team-row-score > span").text();
      String display = teamName + " " + teamScore;
      teamName = divTeams.get(1).select("div.team-row-name > span.c-line-clamp1").text();
      teamScore = divTeams.get(1).select("div.team-row-score > span").text();
      display += " vs " + teamScore + " " + teamName;
      // System.out.println(groupClass + " " + teamName + " " + teamScore);
      // System.out.println("输出：");
      // System.out.println(divGroup);
      System.out.println(display);
    }
    // while(day < end_day || month < end_month) {
      // try {
      //   trustAllHosts();
      //   URL url = new URL("https://tiyu.baidu.com/api/match/世界杯/live/date/2022-11-30/direction/forward?from=self");
      //   HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
      //   // conn.setHostnameVerifier(DO_NOT_VERIFY);
      //   conn.setRequestMethod("GET");
      //   conn.setRequestProperty("user-agent", "	Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:107.0) Gecko/20100101 Firefox/107.0");
			//   conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
      //   conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
      //   conn.setRequestProperty("accept-language", "zh-CN,zh;q=0.9");
      //   conn.setRequestProperty("Connection", "keep-alive");
      //   conn.setRequestProperty("host", "tiyu.baidu.com");
      //   conn.setRequestProperty("Sec-Fetch-Dest", "document");
      //   conn.setRequestProperty("Sec-Fetch-Mode", "navigate");
      //   conn.setRequestProperty("Sec-Fetch-Site", "none");
      //   conn.setRequestProperty("Sec-Fetch-User", "?1");
      //   conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
      //   conn.connect();
      //   if (conn.getResponseCode() != 200) {
      //     throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
      //   } 
        
      //   InputStream inputStream = conn.getInputStream();
      //   InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
      //   BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
      //   String str = null;
      //   StringBuffer buffer = new StringBuffer();
      //   while ((str = bufferedReader.readLine()) != null) {
      //     buffer.append(str);
      // }
      //   System.out.println("响应：" + JSONObject.parseObject(buffer.toString()));
      // } catch (MalformedURLException e) {
      //   e.printStackTrace();
      // } catch (Exception e) {
      //   e.printStackTrace();
      // }
    // }

    // String title = page.select("h1[class=lh-condensed]").first().text();
    // String content = page.selectText("div.content.markdown-body");

    // System.out.println("URL:\n" + page.url());
    // System.out.println("title:\n" + title);
    // System.out.println("content:\n" + content);

    /* If you want to add urls to crawl,add them to nextLink */
    /* WebCollector automatically filters links that have been fetched before */
    /*
     * If autoParse is true and the link you add to nextLinks does not match the
     * regex rules,the link will also been filtered.
     */
    // next.add("http://xxxxxx.com");
  }

  @Override
  public void visit(Page page, CrawlDatums next) {
    System.out.println("visit pages that don't match any annotation rules: " + page.url());
  }

  public static void main(String[] args) throws Exception {
    DemoAnnotatedAutoNewsCrawler crawler = new DemoAnnotatedAutoNewsCrawler("crawl", true);
    /* start crawl with depth of 4 */
    crawler.start(2);
  }

}
