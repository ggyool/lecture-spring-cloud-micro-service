package org.ggyool.zuulservice.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    private final Logger logger = LoggerFactory.getLogger(ZuulLoggingFilter.class);

    // 사전 필터인지 사후 필터인지
    @Override
    public String filterType() {
        return "pre";
    }

    // 여러개 필터가 있을 경우 순서
    @Override
    public int filterOrder() {
        return 1;
    }

    // 필터를 사용할지 말지 여부
    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        log.info("********** printing logs: ");
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        log.info("********** " + request.getRequestURI());

        return null;
    }
}
