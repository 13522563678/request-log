package com.github.requestlog.apachehttpclient.support;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BasicHeaderValueParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Utility class for working with {@link HttpClient}.
 */
public class HttpClientUtils {


    /**
     * Converts the entity of an {@link HttpRequest} to a repeatable entity if needed.
     */
    public static void convertEntityRepeatable(HttpRequest request) throws IOException {
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return;
        }
        HttpEntity httpEntity = ((HttpEntityEnclosingRequest) request).getEntity();
        if (httpEntity instanceof BufferedHttpEntity) {
            return;
        }
        ((HttpEntityEnclosingRequest) request).setEntity(new BufferedHttpEntity(httpEntity));
    }


    /**
     * Converts the entity of an {@link HttpResponse} to a repeatable entity if needed.
     */
    public static void convertEntityRepeatable(HttpResponse response) throws IOException {
        if (response == null) {
            return;
        }
        HttpEntity httpEntity = response.getEntity();
        if (httpEntity == null || httpEntity.getContentLength() <= 0 || httpEntity instanceof BufferedHttpEntity) {
            return;
        }
        response.setEntity(new BufferedHttpEntity(httpEntity));
    }


    /**
     * Converts an array of {@link Header} objects into a Map of header names and values.
     */
    public static Map<String, List<String>> convertHeaders(Header[] headers) {
        if (headers == null || headers.length == 0) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> headersMap = new HashMap<>();
        for (Header header : headers) {
            headersMap.put(header.getName(), Arrays.stream(BasicHeaderValueParser.parseElements(header.getValue(), null))
                    .map(HeaderElement::getName).collect(Collectors.toList()));
        }

        return headersMap;
    }


    /**
     * Builds a complete URI object using the provided HttpHost and HttpRequest objects.
     */
    public static URI buildURI(HttpHost httpHost, HttpRequest httpRequest) {
        try {
            return new URIBuilder()
                    .setScheme(httpHost.getSchemeName())
                    .setHost(httpHost.getHostName())
                    .setPort(httpHost.getPort())
                    .build()
                    .resolve(new URI(httpRequest.getRequestLine().getUri()));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
