package pt.tecnico.cnv;

import com.sun.net.httpserver.HttpServer;
import pt.tecnico.cnv.database.RDSClient;
import pt.tecnico.cnv.handler.PrimeFactorizationHandler;
import pt.tecnico.cnv.jobs.UpdateInstances;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class LoadBalancer {

    private static final int PORT = 8082;

    private HttpServer httpServer;
    
    public static void main(String[] args) throws IOException {
        System.out.println("[Load Balancer] System is starting...");
        new LoadBalancer().start();
    }

    public void start() throws IOException {
        RDSClient.getInstance(); // This initializes the RDS Client Singleton

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/f.html", new PrimeFactorizationHandler());
        httpServer.setExecutor(Executors.newCachedThreadPool());
        new Thread(new UpdateInstances(this)).start();
    }
    
    public void serve() {
        httpServer.start();
        System.out.println("[LoadBalancer] Load Balancer running on port " + PORT);
    }

}
