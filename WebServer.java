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

import java.io.*;

public class WebServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/f.html", new VanillaHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool()); // creates a default executor
        server.start();
        System.out.println("[INFO] SERVER SUCCESSFULLY STARTED!");
    }

    static class VanillaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            long threadId = Thread.currentThread().getId();

            String rawQuery = t.getRequestURI().getQuery();

            BigInteger query = new BigInteger(rawQuery.split("=")[1]);// get the number
            System.out.println("[DEBUG] Thread " + threadId + " requested to factorize " + query);
            String response = "The prime factorization of " + t.getRequestURI().getQuery() + " is: " + primeFactorize(query);
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String primeFactorize(BigInteger num) {

            System.out.println("[INFO] Factorizing " + num);

            /*String output = num + " took ";
            Writer outputWriter;
            try {
                outputWriter = new BufferedWriter(new FileWriter("log.txt", true));
                outputWriter.append(output);
                outputWriter.close();
            }catch (IOException e) {
                e.printStackTrace();
            }*/
            

            long startTime = System.currentTimeMillis();

            IntFactorization factorizer = new IntFactorization(num);
            ArrayList<BigInteger> result = factorizer.calcPrimeFactors();
            System.out.println("Testing...");
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

    }

}
