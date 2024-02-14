package com.github.requestlog.core.model;

import com.github.requestlog.core.enums.HttpMethod;
import com.github.requestlog.core.enums.RequestContextType;
import lombok.Data;

import java.util.List;
import java.util.Map;


@Data
public class HttpRequestContextModel {

    private HttpMethod httpMethod;
    private RequestContextType requestContextType;

    private String requestUrl;
    private String requestPath;
    private Map<String, List<String>> requestHeaders;
    private String requestBody;

    private Integer responseCode;
    private Map<String, List<String>> responseHeaders;
    private String responseBody;

}
