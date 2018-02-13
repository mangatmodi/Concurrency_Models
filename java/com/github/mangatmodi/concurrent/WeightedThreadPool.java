package com.github.mangatmodi.concurrent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WeightedThreadPool {
    public static final class WeightedArrayBlockingQueue<T>implements BlockingQueue<T> {

        private final ArrayList<Integer> wts;
        private final ArrayList<T> tasks;
        private final int capacity;

        public WeightedArrayBlockingQueue(int capacity) {
            this.capacity = capacity;
            wts = new ArrayList<>(capacity);
            tasks = new ArrayList<>(capacity);
        }

        private synchronized void revaluate() {

        }

        @Override
        public T remove() {
            // TODO Auto-generated method stub
            exceptionIfEmpty();
            return null;
        }

        @Override
        public T poll() {
            // TODO Auto-generated method stub
            return null;
        }
        
        private void exceptionIfEmpty() throws NoSuchElementException{
            if(wts.size()==0) {
                throw new NoSuchElementException();
            }
        }

        @Override
        public synchronized T element() throws NoSuchElementException{
            exceptionIfEmpty();
            return peek();
        }

        /**
         * Return the task where the coresponding weight is just bigger or equal than
         * the required wt
         **/
        private T getTask(int wt) {
            int i = 0;
            for (int w : wts) {
                if (w >= wt) {
                    break;
                }
                i++;
            }
            return tasks.get(i);
        }

        @Override
        public synchronized T peek() {
            if (wts.size() == 0) {
                return null;
            }
            int max = wts.get(wts.size() - 1);
            int r = ThreadLocalRandom.current().nextInt(0, max + 1);
            return getTask(r);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public synchronized void clear() {
            wts.clear();
            tasks.clear();

        }

        @Override
        public boolean add(T e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean offer(T e) {
            // TODO Auto-generated method stub
            tasks.add(e);
            WeightedRunnable t = (WeightedRunnable) e;
            wts.add(t.getWeight());
            return false;
        }

        @Override
        public void put(T e) throws InterruptedException {
            // TODO Auto-generated method stub

        }

        @Override
        public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public T take() throws InterruptedException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public T poll(long timeout, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public synchronized int remainingCapacity() {
            return this.capacity-wts.size();
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean contains(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public int drainTo(Collection<? super T> c) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int drainTo(Collection<? super T> c, int maxElements) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public synchronized int size() {            
            return wts.size();
        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Iterator<T> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }

    }
    public static interface WeightedRunnable extends Runnable{
        public void setWeight(int wt);
        public int getWeight();        
    }
    public static void main(String[] args) {
       //[47][9][7]
       // [nano time][threadId][counter for request in same time]
       long prev = 0;
       long counter = 0;
       long id1 = System.nanoTime();       
       System.out.println(Long.toBinaryString(id1));
       id1 = id1 << 16;
       System.out.println(Long.toBinaryString(id1));
       long thread = 420l << 7;
       id1 = id1+thread;
       System.out.println(Long.toBinaryString(id1));       
       if(id1==prev) {
           counter++;
           id1+=counter;
       }
       System.out.println(Long.toBinaryString(id1));       
       System.out.println(id1);
    }

}
