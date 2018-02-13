package com.github.mangatmodi.concurrent.FileCountServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncServer {

    private int portNumber;
    private int threads;
    ExecutorService clientConnections;

    public AsyncServer(int portNumber) {
        this.portNumber = portNumber;
        // number of cpu
        int cores = Runtime.getRuntime().availableProcessors();
        this.threads = cores*50;
        clientConnections = new ThreadPoolExecutor(threads, threads, 500, TimeUnit.MILLISECONDS,
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
                // TODO Auto-generated method stub
                try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
                    out.println(in.readLine());
                    in.close();
                    out.close();
                    return 200;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 500;
                } finally {
                    clientSocket.close();
                }
            }

        });
    }
}
