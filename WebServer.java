import java.lang.String;
import java.lang.System;
import java.math.BigInteger;
import java.util.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new VanillaHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // creates a default executor
        server.start();
        System.out.println("[INFO] SERVER SUCCESSFULLY STARTED!");
    }

    static class VanillaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            long threadId = Thread.currentThread().getId();

            BigInteger query = new BigInteger(t.getRequestURI().getQuery());
            System.out.println("[DEBUG] Thread " + threadId + " requested to factorize " + query);
            String response = "The prime factorization of " + t.getRequestURI().getQuery() + " is: " + primeFactorize(query);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String primeFactorize(BigInteger num) {

            System.out.println("[INFO] Factorizing " + num);

            long startTime = System.currentTimeMillis();

            IntFactorization factorizer = new IntFactorization();
            ArrayList<BigInteger> result = factorizer.calcPrimeFactors(num);

            long totalTime = System.currentTimeMillis() - startTime;

            System.out.println("[INFO] Factorized " + num + " in " + totalTime + "ms!");

            boolean isFirst = true;
            String outPut = "";

            for (BigInteger el : result) {
                if(isFirst) {
                    outPut = outPut + el;
                    isFirst = false;
                }
                else {
                    outPut = outPut + " * " + el;
                }
            }

            return outPut;
        }

        public class IntFactorization {

            private BigInteger zero = new BigInteger("0");
            private BigInteger one = new BigInteger("1");
            private BigInteger divisor = new BigInteger("2");
            private ArrayList<BigInteger> factors = new ArrayList<BigInteger>();


            ArrayList<BigInteger>  calcPrimeFactors(BigInteger num) {

                if (num.compareTo(one)==0) {
                    return factors;
                }

                while(num.remainder(divisor).compareTo(zero)!=0) {
                    divisor = divisor.add(one);
                }

                factors.add(divisor);
                return calcPrimeFactors(num.divide(divisor));
            }
        }
    }

}