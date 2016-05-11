package pt.tecnico.cnv.handler;

import com.amazonaws.services.ec2.model.Instance;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pt.tecnico.cnv.InstanceManager;
import pt.tecnico.cnv.database.RDSClient;
import pt.tecnico.cnv.http.HTTPClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PrimeFactorizationHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) {

        String response = "Oops, something went wrong! :(";

        try {
            // Get the number to factorize
            String queryNumber = httpExchange.getRequestURI().getQuery().split("=")[1];
            BigInteger number = new BigInteger(queryNumber);

            // Compute the cost of such factorization
            BigInteger cost = findCost(number);

            // Allocate an instance to perform the factorization
            Instance instance = InstanceManager.getInstance().allocateInstance(cost);
            System.out.println("[Prime Factorization Handler] " + instance.getInstanceId() + " allocated to factorize " + number + " (cost = " + cost + ")");

            try {
                // Send the request to that instance
                response = HTTPClient.GET("http://" + instance.getPublicDnsName() + ":8000/f.html?n=" + number);
            } finally {
                // Remove the request from internal data structures
                InstanceManager.getInstance().removeInstanceRequest(instance, cost);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Send response to the client
                httpExchange.sendResponseHeaders(200, response.length());
                httpExchange.getResponseBody().write(response.getBytes());
                httpExchange.getResponseBody().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BigInteger findCost(BigInteger request) {
        RDSClient rdsClient = RDSClient.getInstance();
        ResultSet resultSet = rdsClient.queryNumbersNextTo(request);
        List<BigInteger> basicBlocksList = new ArrayList<BigInteger>();
        try {
            while (resultSet.next()) {
                BigInteger number = resultSet.getBigDecimal(RDSClient.NUM_COL).toBigInteger();
                BigInteger basicBlocks = resultSet.getBigDecimal(RDSClient.BB_COL).toBigInteger();
                if (request.equals(number)) {
                    return basicBlocks;
                }
                basicBlocksList.add(basicBlocks);
            }
            BigInteger sum = new BigInteger("0");

            if (basicBlocksList.isEmpty()) {
                return sum;
            }

            for (BigInteger basicBlocks : basicBlocksList) {
                sum = sum.add(basicBlocks);
            }
            return sum.divide(BigInteger.valueOf(basicBlocksList.size()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
