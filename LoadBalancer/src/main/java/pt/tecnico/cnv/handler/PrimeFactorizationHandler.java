package pt.tecnico.cnv.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import pt.tecnico.cnv.database.RDSClient;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrimeFactorizationHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) {
        try {
            String queryNumber = httpExchange.getRequestURI().getQuery().split("=")[1];
            BigInteger number = new BigInteger(queryNumber);
            BigInteger cost = findCost(number);
            String response = "Cost(" + number + ") = " + cost;
            httpExchange.sendResponseHeaders(200, response.length());
            httpExchange.getResponseBody().write(response.getBytes());
            httpExchange.getResponseBody().close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Instance instance = chooseInstanceBasedOnCost(cost);
        // instance.execute(httpExchange);
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
