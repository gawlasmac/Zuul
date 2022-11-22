package com.example.zuul;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomersFilter extends ZuulFilter {

    private final Map<String, Integer> portsUsage = new HashMap<>();

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        final RequestContext currentContext = RequestContext.getCurrentContext();
        return currentContext.getRequest().getRequestURI().startsWith("/api/customers");
    }

    @Override
    public Object run() throws ZuulException {
        final RequestContext currentContext = RequestContext.getCurrentContext();
        Optional<String> port = currentContext.getZuulResponseHeaders().stream().filter(pair -> pair.first().equals("x-port")).map(Pair::second).findFirst();
        if (!port.isPresent()) {
            return null;
        }
        portsUsage.merge(port.get(), 1, Integer::sum);
        System.out.println("*********************");
        portsUsage.forEach((key, value) -> System.out.println("Port: " + key + " uses: " + value));
        System.out.println("*********************");
        return null;
    }
}
