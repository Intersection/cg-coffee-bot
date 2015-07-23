package com.controlgroup.coffeesystem.gae;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by timmattison on 1/2/15.
 */
public class CORSFilter implements Filter {
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    private static final String ALL = "*";

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        ((HttpServletResponse) resp).addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {

    }

}
