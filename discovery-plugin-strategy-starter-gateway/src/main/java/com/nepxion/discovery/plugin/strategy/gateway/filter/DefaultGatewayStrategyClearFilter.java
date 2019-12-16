package com.nepxion.discovery.plugin.strategy.gateway.filter;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import reactor.core.publisher.Mono;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;

import com.nepxion.discovery.plugin.strategy.gateway.context.GatewayStrategyContext;
import com.nepxion.discovery.plugin.strategy.gateway.tracer.GatewayStrategyTracer;

public class DefaultGatewayStrategyClearFilter implements GatewayStrategyClearFilter {
    @Autowired(required = false)
    private List<GatewayStrategyTracer> gatewayStrategyTracerList;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayStrategyContext.clearCurrentContext();

        // 调用链释放
        if (CollectionUtils.isNotEmpty(gatewayStrategyTracerList)) {
            for (GatewayStrategyTracer gatewayStrategyTracer : gatewayStrategyTracerList) {
                gatewayStrategyTracer.release(exchange);
            }
        }

        return chain.filter(exchange);
    }
}