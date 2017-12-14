import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Final->3186184 timeTaken:30883 Final->7216316 timeTaken:34007 Final->2858928
 * timeTaken:31540 Final->3012852 timeTaken:31321 Final->7349471 timeTaken:24552
 * Final->5625075 timeTaken:35262 Final->4019798 timeTaken:31458 Final->320391
 * timeTaken:35240 Final->6007861
timeTaken:35236

 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */

public class WebCrawlerParallel {
    static AtomicInteger count = new AtomicInteger();
    static Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    static ExecutorService linkExtractorService = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();
        try {
            System.out.println("Final->" + parseLink("http://de.wikipedia.com/"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("timeTaken:" + (System.currentTimeMillis() - start));

        // Runtime.getRuntime().addShutdownHook(new Thread() {
        // public void run() {
        // System.out.println("number of urls->"+urls.size());
        // System.out.println("timeTaken:"+(System.currentTimeMillis() - start));
        // }
        // });
    }

    public static Long parseLink(String url) {
        Document doc = null;
        final int length;
        if (!urls.contains(url)) {
            urls.add(url);
        }
        if (urls.size() > 500) {
            linkExtractorService.shutdown();
            return 0L;
        }
        try {
            doc = Jsoup.connect(url).get();
            length = doc.data().length();
        } catch (Exception e) {
            return 0L;
        }
        Elements links = doc.select("a");
        ArrayList<Future<Long>> tasks = new ArrayList<>(links.size());
        for (Element link : links) {
            if (link.attr("href").contains("http") && !linkExtractorService.isShutdown()) {
                final String foundUrl = link.attr("abs:href").toLowerCase();
                if (!linkExtractorService.isShutdown()) {
                    tasks.add(linkExtractorService.submit(() -> parseLink(foundUrl)));
                }
                // parseLink(foundUrl);
            }
        }
        return waitAndGetWordCount(tasks, length);
    }

    public static Long waitAndGetWordCount(ArrayList<Future<Long>> tasks, long currentLength) {
        for (Future<Long> t : tasks) {
            try {
                currentLength += t.get();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        // System.out.println(currentLength);
        return currentLength;
    }
}
