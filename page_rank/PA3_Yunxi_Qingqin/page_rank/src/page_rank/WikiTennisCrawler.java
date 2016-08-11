package page_rank;

/**
 * Created by Guo on 3/16/16.
 */
public class WikiTennisCrawler {
    public static void main (String[] args) {
        String[] keywords = {"tennis","grand slam"};
        WikiCrawler wc = new WikiCrawler("/wiki/Tennis",keywords,1000,"WikiTennisGraph.txt");
        long start = System.currentTimeMillis();
        wc.crawl();
        long end = System.currentTimeMillis();
        long time = (end - start)/1000;
        System.out.println(time);
    }
}
