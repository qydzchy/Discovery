package com.nepxion.discovery.plugin.strategy.gateway.wrapper;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @author Hao Huang
 * @version 1.0
 */

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ServerWebExchange;

import com.nepxion.discovery.plugin.strategy.gateway.context.GatewayStrategyContext;
import com.nepxion.discovery.plugin.strategy.tracer.StrategyTracerContextManager;
import com.nepxion.discovery.plugin.strategy.wrapper.CallableWrapper;

public class DefaultCallableWrapper implements CallableWrapper {
    @Autowired(required = false)
    private StrategyTracerContextManager strategyTracerContextManager;

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        ServerWebExchange exchange = GatewayStrategyContext.getCurrentContext().getExchange();

        Object tracerContext = getTracerContext();

        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    GatewayStrategyContext.getCurrentContext().setExchange(exchange);

                    setTracerContext(tracerContext);

                    return callable.call();
                } finally {
                    GatewayStrategyContext.clearCurrentContext();

                    clearTracerContext();
                }
            }
        };
    }

    private Object getTracerContext() {
        if (strategyTracerContextManager == null) {
            return null;
        }

        return strategyTracerContextManager.getContext();
    }

    private void setTracerContext(Object context) {
        if (strategyTracerContextManager == null) {
            return;
        }

        strategyTracerContextManager.setContext(context);
    }

    private void clearTracerContext() {
        if (strategyTracerContextManager == null) {
            return;
        }

        strategyTracerContextManager.clearContext();
    }
}