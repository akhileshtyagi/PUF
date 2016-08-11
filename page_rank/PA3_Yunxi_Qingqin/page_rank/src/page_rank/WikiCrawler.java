package page_rank;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Guo on 3/23/16.
 */
public class WikiCrawler {
    static final String BASE_URL = "https://en.wikipedia.org";
    String seedUrl;
    String[] keywords;
    String fileName;
    HashSet<String> blockPages;
    HashMap<String, String> pageContent;
    HashSet<String> allPages;
    Queue<String> q;
    int pagecount;
    int edge;
    int max;
    int requestcount;
    int NumberofNodes;

    public WikiCrawler(String seedUrl, String[] keywords, int max, String fileName){
        this.seedUrl = seedUrl;
        this.keywords = keywords;
        this.fileName = fileName;
        this.blockPages = new HashSet<String>();
        this.pageContent = new HashMap<String,String>();
        this.q = new LinkedList<String>();
        this.edge = 0;
        this.max = max;
        this.requestcount = 0;
        this.pagecount = 0;
        this.NumberofNodes = 0;
        initialBlockList();
        //crawl();
    }

    /**
     * Crawl from seed file
     */
    public void crawl() {
        //push seed web page into queue
        q.add(seedUrl);

        //create a hashset to save all visited pages
        HashSet<String> visited = new HashSet<String>();
        visited.add(seedUrl);
        NumberofNodes++;

        try {
            //Create file and write number of nodes
            File file = new File(fileName);
            FileWriter fw = new FileWriter(file);
            fw.write(max + "\r\n");


            while (!q.isEmpty()) {
                //get next url
                String first = q.poll();

                    //get page content of current url
                    String content;
                    if (pageContent.containsKey(first)) {
                        content = pageContent.get(first);
                    } else {
                        content = fatchPage(first, true);
                        pageContent.put(first,content);
                    }

                    //get all page referred from current page
                    ArrayList<String> outPage = findAllLinks(content, first);
                    //System.out.println("out page size : " + outPage.size());

                    //Add referred page to visited
                    for (int i = 0; i < outPage.size(); i++) {
                        // if current link has not been visited
                        // and this link contains keywords
                        if (!visited.contains(outPage.get(i)) && hasKeywords(outPage.get(i))) {
                            if (visited.size() < max){
                                visited.add(outPage.get(i));
                                NumberofNodes++;
                                q.add(outPage.get(i));
                            } else {
                                break;
                            }
                        }
                    //System.out.println("cur.size: "+visited.size());
                }
                if (visited.size() >= max) {
                    break;
                }
            }

            //Write edges into document
            for (String url : visited) {
                String c = pageContent.get(url);
                ArrayList<String> list = findAllLinks(c,url);
                for (String out : list) {
                    if (visited.contains(out)) {
                        fw.write(url + " " + out + "\r\n");
                        edge++;
                    }
                }
            }

            fw.close();
            //System.out.println("request count: " + requestcount);
            //System.out.println("Number of Node: " + visited.size());
        } catch (IOException e) {

        } catch (Exception f) {

        }
    }



    /**
     * Check whether this URL could be visited.
     * @param target target URL need to be checked.
     * @return whether this URL could be visited.
     */
    private boolean validURL(String target) {
        //See whether the page is blocked
        if (blockPages.contains(target)) {
            System.out.println("You are requesting a blocking web page");
            return false;
        }
        //If url contains "#" and ":",then the web page is out of domain
        if (target.contains("#") || target.contains(":")) {
            //System.out.println("You are requesting a web page out of domain");
            return false;
        }
        //See whether it is a wiki web page
        if (!target.startsWith("/wiki")) {
            //System.out.println("You are requesting a web page out of domain");
            return false;
        }
        return true;
    }


    /**
     * Find all link in current page
     * @param content page content of current url
     * @param url target url
     * @return a list of valid link which referred by current url
     */
    private ArrayList<String> findAllLinks(String content, String url) {
        //Create an ArrayList to save links
        ArrayList<String> result = new ArrayList<String>();

        //find all valid links
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        Matcher matcher = p.matcher(content);


        while (matcher.find()) {
            String s = matcher.group(1);

            if (validURL(s) && !result.contains(s) && !s.equals(url)) {
                result.add(s);
            }
        }
        return result;
    }


    /**
     * initialize the list of block website
     */
    private void initialBlockList() {
        try {
            //get content of robots.txt page
            URL url = new URL(BASE_URL + "/robots.txt");
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            //get each line find out the disallow links
            String currentline = "";
            while ((currentline = br.readLine()) != null) {
                String[] list = currentline.split(":");
                if (list[0].equals("Disallow")) {
                    //if a link is blocked, then add link to the list of block pages.
                    if (list.length > 1) {
                        blockPages.add(list[1].substring(1));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't find robots.txt file.");
        }
    }

    /**
     * Get page content of a target url
     * @param target target url
     * @param filter if filter is true, get rid of all panels with navigational links.
     * @return page content
     */
    private String fatchPage(String target, boolean filter) {

        try {
            //Get original page content
            URL url = new URL(BASE_URL + target);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //Create a string buffer to save page content
            StringBuffer sb = new StringBuffer();

            //If we have send 100 page download request, sleep for 5 seconds.
            requestcount++;

            if (requestcount != 0 && requestcount % 100 == 0) {
                try{
                    Thread.sleep(5000);
                } catch (Exception e){
                    //e.getStackTrace();
                    System.out.println("Failed to pause thread.");
                }

            }

            //handle each line of page content
            String currentline = "";
            boolean start = false;
            if (filter) {
                while ((currentline = br.readLine()) != null) {
                    if (!start && currentline.contains("<p>")) {
                        start = true;
                        //sb.append(" ");
                        sb.append(currentline);
                    } /*else if (start && currentline.contains("</p>")) {
                        start = false;
                    } */else if (start) {
                        //sb.append(" ");
                        sb.append(currentline);
                    }
                }
            } else {
                while ((currentline = br.readLine()) != null) {
                    //sb.append(" ");
                    sb.append(currentline);
                }
            }
            //Case insensitive
            return sb.toString();
        } catch (Exception e) {
            return null;
            //System.out.println("Can't open current page : " + BASE_URL + target);
        }
        //return null;
    }

    /**
     * Check whether target page contains all keywards,save pages contains all keywords into HashMap
     * @param target
     * @return
     */
    private boolean hasKeywords(String target) {
        String[] list = target.split("/");
        //fatch content from wiki raw text page
        //System.out.println("/w/index.php?title=" + list[2] + "&action=raw");
        String content = fatchPage("/w/index.php?title=" + list[2] + "&action=raw", false).toLowerCase();

        if (content != null) {
            for (int i = 0; i < keywords.length; i++) {
                if (!content.contains(keywords[i])) {
                    return false;
                }
            }
            if (!pageContent.containsKey(target)) {
                pageContent.put(target, fatchPage(target, true));
            }
            pagecount++;
            return true;
        } else {
            return false;
        }
    }

    private int getEdge() {
        return edge;
    }

    private int getNode() {
        return NumberofNodes;
    }
}
