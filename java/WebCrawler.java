import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
    static Set<String> urls = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    static ExecutorService linkExtractorService = Executors.newCachedThreadPool();
    static ExecutorService wordCountService = Executors.newFixedThreadPool(100);
    static long start = System.currentTimeMillis();
    static int urlCount = 0;

    /**
     * 
     * Final->1272219 timeTaken:34107
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
     * @throws InterruptedException
     * @throws ExecutionException
     * 
     **/
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        start = System.currentTimeMillis();
        try {
            System.out.println("Final->" + parseLink("http://de.wikipedia.com/").get());
            wordCountService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("timeTaken:" + (System.currentTimeMillis() - start));
        System.out.println(urlCount);
    }

    public static Future<Long> parseLink(String url) {
        Document doc = null;
        final int length;
        try {
            doc = Jsoup.connect(url).get();
            length = doc.data().length();
        } catch (Exception e) {
            return CompletableFuture.completedFuture(0L);
        }
        Elements links = doc.select("a");
        ArrayList<Future<Future<Long>>> tasks = new ArrayList<>(links.size());
        for (Element link : links) {
            if (link.attr("href").contains("http") && !linkExtractorService.isShutdown()) {
                final String foundUrl = link.attr("abs:href").toLowerCase();
                if (urls.size() > 500) {
                    linkExtractorService.shutdownNow();
                    return CompletableFuture.completedFuture(0L);
                }
                if (!urls.contains(foundUrl)) {
                    urls.add(foundUrl);
                }
                if (!linkExtractorService.isShutdown()) {
                    synchronized (WebCrawler.class) {
                        if (!linkExtractorService.isShutdown()) {
                            tasks.add(linkExtractorService.submit(() -> parseLink(foundUrl)));
                        }
                    }
                }
                // parseLink(foundUrl);
            }
        }
            if (wordCountService.isShutdown()) {
                return CompletableFuture.completedFuture(length * 1L);
            }
        return wordCountService.submit(() -> waitAndGetWordCount(tasks, length));
    }

    public static Long waitAndGetWordCount(ArrayList<Future<Future<Long>>> tasks, long currentLength) {
        if (wordCountService.isShutdown()) {
            return 0L;
        }

        for (Future<Future<Long>> t : tasks) {
            try {
                currentLength += t.get().get();
                synchronized (WebCrawler.class) {
                    urlCount++;
                    if (urlCount > 500) {
                        wordCountService.shutdownNow();
                        return currentLength;

                    }
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        // System.out.println(currentLength);
        return currentLength;
    }
}
