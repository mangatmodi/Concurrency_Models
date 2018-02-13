package com.github.mangatmodi.concurrent.Server;

import java.net.Socket;

public interface ServerTask {
    /**
     * @param clientSocket:
     *            Socket at where the server is connected.
     * 
     *            <p>
     *            Perform the desired task for the client request, received over the
     *            given socket. May return the output on clientSocket. Returns the
     *            appropriate return status. Take it as an example of HTTP
     *            POST/GET/DELETE etc. methods on any arbitory socket
     *            </p>
     * @return HTTP Status Code
     *
     */
    public Integer methodExecute(Socket clientSocket) throws Exception;

}
