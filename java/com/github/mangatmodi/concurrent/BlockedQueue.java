package com.github.mangatmodi.concurrent;

public class BlockedQueue<T> {
    final Object[] arr;
    final int capacity;
    int start;
    int count;

    private final Object notEmpty;
    private final Object notFull;

    public BlockedQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }
        arr = new Object[capacity];
        this.capacity = capacity;
        notEmpty = new Object();
        notFull = new Object();
        start = 0;
        count = 0;
    }

    public synchronized void clear() {
        for (int i = 0; i < this.capacity; i++) {
            arr[i] = null;
        }
        start = 0;
        count = 0;
        signalNotify(notFull);
    }

    public synchronized int size() {
        return this.count;
    }

    public int capacity() {
        return capacity;
    }

    private void signalNotify(Object o) {
        synchronized (o) {
            o.notifyAll();
        }
    }

    private void signalWait(Object o) throws InterruptedException {
        synchronized (o) {
            o.wait();
        }
    }

    @SuppressWarnings("unchecked")
    public T dequeue() throws ClassCastException, InterruptedException {
        if (size() == 0) {
            signalWait(notEmpty);
        }
        T temp = null;
        synchronized (this) {
            temp = (T) arr[start];
            arr[start] = null;
            start = (start + 1) % capacity;
            count--;
            System.out.println("dequeued" + ",Size= "+size()+",Start= "+start + ",count= " + count);
        }
        signalNotify(notFull);
        return temp;
    }

    public void enqueue(T obj) throws ClassCastException, InterruptedException {
        int pos = (start+count)%capacity;
        if (size() == capacity) {
            signalWait(notFull);
        }
        synchronized (this) {
            arr[pos] = obj;
            count++;
            System.out.println("enqueued: " + obj+" "+size()+",Start= "+start + ",count=  " + count);
        }
        signalNotify(notEmpty);
    }

    private synchronized void dumpArray() {
        System.out.println(start + "  " + count);
        for (Object i : arr) {
            System.out.print(i == null ? "NULL " : i.toString() + " ");
        }
    }

    private void dequeueTask() {
        Runnable t = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println("Removed-->" + BlockedQueue.this.dequeue());
                        Thread.sleep(1000 * 5);
                    } catch (ClassCastException | InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(t).start();
    }

    public static void main(String[] args) throws Exception {
        BlockedQueue<Integer> q = new BlockedQueue<>(2);
        q.dequeueTask();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        q.enqueue(6);
        // System.out.println(q.dequeue());
    }
}
