import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * *
 * 
 * timeTaken:12035 4102095 ; timeTaken:11993 4102095; timeTaken:13807 4158568
 * timeTaken:14689 5839794; timeTaken:10977 5291768
 *
 * 
 * 
 * 
 * ### Node timeTaken:22653 total:4358691
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class WebCrawlerNodeEquivalent {
    // static AtomicInteger count = new AtomicInteger();
    static int count = 0;
    static int urlCount = 0;
    static Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    static ExecutorService linkExtractorService = Executors.newCachedThreadPool();
    static long start = System.currentTimeMillis();

    public static void main(String[] args) throws InterruptedException {

        start = System.currentTimeMillis();
        try {
            parseLink("http://de.wikipedia.com/");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void parseLink(String url) {
        if (urls.size() > 500) {
            linkExtractorService.shutdown();
            return;
        }
        Document doc = null;
        if (!urls.contains(url)) {
            urls.add(url);
        }
        try {
            doc = Jsoup.connect(url).get();
            synchronized (WebCrawlerNodeEquivalent.class) {
                count += doc.data().length();
                urlCount++;
                if (urlCount > 500) {
                    System.out.println("timeTaken:" + (System.currentTimeMillis() - start));
                    System.out.println(count);
                    linkExtractorService.shutdownNow();
                    System.exit(-1);
                }
            }
        } catch (Exception e) {
            return;
        }
        Elements links = doc.select("a");
        for (Element link : links) {
            if (link.attr("href").contains("http") && !linkExtractorService.isShutdown()) {
                final String foundUrl = link.attr("abs:href").toLowerCase();
                if (!urls.contains(foundUrl)) {
                    urls.add(foundUrl);
                }
                if (urls.size() > 500) {
                    linkExtractorService.shutdown();
                }
                if (!linkExtractorService.isShutdown()) {
                    linkExtractorService.submit(() -> parseLink(foundUrl));
                }
                // parseLink(foundUrl);
            }
        }
        return;
    }

}
