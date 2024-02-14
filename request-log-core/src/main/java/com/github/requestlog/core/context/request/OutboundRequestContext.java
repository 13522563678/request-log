package com.github.requestlog.core.context.request;


import com.github.requestlog.core.context.LogContext;
import com.github.requestlog.core.enums.RequestLogErrorType;
import com.github.requestlog.core.enums.RetryWaitStrategy;
import com.github.requestlog.core.model.HttpRequestContextModel;
import com.github.requestlog.core.model.RequestRetryJob;
import com.github.requestlog.core.support.CollectionUtils;
import com.github.requestlog.core.support.Predicates;
import com.github.requestlog.core.support.SupplierChain;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.function.Predicate;


/**
 * Request context 4 outbound
 */
@RequiredArgsConstructor
public abstract class OutboundRequestContext extends BaseRequestContext {


    protected final LogContext logContext;


    @Override
    public boolean logRequest() {

        if (super.logRequestCache != null) {
            return super.logRequestCache;
        }

        if (logContext == null) {
            return (super.logRequestCache = false);
        }

        if (exception != null) {
            requestLogErrorType = RequestLogErrorType.EXCEPTION;
            Predicate<Exception> exceptionPredicate = SupplierChain.of(logContext.getExceptionPredicate()).or(Predicates.getExceptionPredicate(getRequestContextType())).get();
            return super.logRequestCache = exceptionPredicate.test(exception);
        }


        Predicate<HttpRequestContextModel> httpResponsePredicate = SupplierChain.of(logContext.getHttpResponsePredicate()).or(Predicates.getResponsePredicate(getRequestContextType())).get();

        // TODO: 2024/1/31 catch predicate error
        if (super.logRequestCache = httpResponsePredicate.test(buildHttpRequestContextModel())) {
            requestLogErrorType = RequestLogErrorType.RESPONSE;
        }

        return super.logRequestCache;
    }

    @Override
    public boolean retryRequest() {
        if (retryRequestCache != null) {
            return retryRequestCache;
        }
        return (retryRequestCache = (logRequest() && Boolean.TRUE.equals(logContext.getRetry())));
    }


    @Override
    public RequestRetryJob buildRequestRetryJob() {
        if (super.requestRetryJobCache != null) {
            return super.requestRetryJobCache;
        }

        RequestRetryJob retryJob = new RequestRetryJob();
        retryJob.setRequestLog(buildRequestLog());
        retryJob.setRetryWaitStrategy(Optional.ofNullable(logContext.getRetryWaitStrategy()).orElse(RetryWaitStrategy.FIXED));
        retryJob.setRetryInterval(Optional.ofNullable(logContext.getRetryInterval()).orElse(60));
        retryJob.setLastExecuteTimeMillis(System.currentTimeMillis()); // TODO: 2024/2/13 是否要 interceptor 之前拿取？
        retryJob.setExecuteCount(1);
        retryJob.setNextExecuteTimeMillis(retryJob.getRetryWaitStrategy().nextExecuteTime(retryJob.getLastExecuteTimeMillis(), 1, retryJob.getRetryInterval()));

        return (super.requestRetryJobCache = retryJob);
    }


    // TODO: 2024/2/12 super?
    public HttpRequestContextModel buildHttpRequestContextModel() {
        HttpRequestContextModel httpRequestContext = new HttpRequestContextModel();
        httpRequestContext.setHttpMethod(getRequestMethod());
        httpRequestContext.setRequestContextType(getRequestContextType());
        httpRequestContext.setRequestUrl(getRequestUrl());
        httpRequestContext.setRequestPath(getRequestPath());
        httpRequestContext.setRequestHeaders(CollectionUtils.unmodifiableMap(getRequestHeaders()));
        httpRequestContext.setRequestBody(getRequestBody());
        httpRequestContext.setResponseCode(getResponseCode());
        httpRequestContext.setResponseHeaders(CollectionUtils.unmodifiableMap(getResponseHeaders()));
        httpRequestContext.setResponseBody(getResponseBody());
        return httpRequestContext;
    }

}
