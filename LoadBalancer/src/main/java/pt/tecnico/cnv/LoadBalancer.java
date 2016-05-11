package pt.tecnico.cnv;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import pt.tecnico.cnv.database.RDSClient;
import pt.tecnico.cnv.handler.PrimeFactorizationHandler;
import pt.tecnico.cnv.jobs.UpdateInstances;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
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
