package com.example.demo.filter;

import com.example.demo.util.FilterUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RequestFilter extends FilterUtil implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        if (isFilterWhiteList(path)) {
            chain.doFilter(request, response);
        } else {
            RereadableRequestWrapper rereadableRequestWrapper = new RereadableRequestWrapper((HttpServletRequest) request);
            chain.doFilter(rereadableRequestWrapper, response);
        }
    }

    @Override
    public void destroy() { }

    @Override
    public void init(FilterConfig config) { }

}
