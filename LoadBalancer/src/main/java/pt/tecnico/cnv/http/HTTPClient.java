package pt.tecnico.cnv.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

public class HTTPClient {
    public static String GET(String url) throws IOException {
        InputStream stream = new URL(url).openStream();
        return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
    }
}
