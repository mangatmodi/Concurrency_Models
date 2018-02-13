package com.github.mangatmodi.concurrent.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerTaskCPUIntensive implements ServerTask{

    @Override
    public Integer methodExecute(Socket clientSocket) throws IOException {
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

}
