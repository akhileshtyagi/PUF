package page_rank;

/**
 * Created by Guo on 3/16/16.
 */
public class MyWikiCrawler {
    public static void main (String[] args) {
        String[] keywords = {"basketball", "national basketball association" };

        WikiCrawler wc = new WikiCrawler("/wiki/basketball", keywords, 1000, "MyWikiGraph.txt");
        long start = System.currentTimeMillis();
        wc.crawl();
        long end = System.currentTimeMillis();
        long time = (end - start)/1000;
        System.out.println(time);
        System.out.println("Number of Nodes : " + wc.NumberofNodes);
        System.out.println("Number of Edges : " + wc.edge);
    }
}
