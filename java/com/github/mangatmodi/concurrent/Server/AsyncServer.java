package com.github.mangatmodi.concurrent.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncServer {

    private final int portNumber;
    private final int threads;
    private final ExecutorService clientConnections;

    public AsyncServer(int portNumber) {
        this.portNumber = portNumber;
        // number of cpu
        int cores = Runtime.getRuntime().availableProcessors();
        this.threads = cores * 100;
        this.clientConnections = new ThreadPoolExecutor(threads, threads, 500, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(threads * 10, false), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public AsyncServer(int portNumber, int threads) {
        this.portNumber = portNumber;
        this.threads = threads;
        clientConnections = new ThreadPoolExecutor(threads, threads, 500, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(threads * 10, false), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String[] args) throws Exception {
        int portNumber = Integer.parseInt(args[0]);
        AsyncServer s = new AsyncServer(portNumber);
        ServerSocket serverSocket = new ServerSocket(portNumber);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            s.handleClient(clientSocket);
        }
    }

    public void handleClient(Socket clientSocket) throws Exception {
        clientConnections.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                return new ServerTaskCPUIntensive().methodExecute(clientSocket);
            }

        });
    }
}
