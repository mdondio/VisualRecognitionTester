package services;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// TODO cambiare qui sotto per filtrare tutto, tranne loginservlet
// cosi facendo, tutte le pagine html diventano .jsp!!!! da modificare

@WebFilter(filterName="ApiFilter", urlPatterns="/api/*")
public class ApiFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {
        // If you have any <init-param> in web.xml, then you could get them
        // here by config.getInitParameter("name") and assign it as field.
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

  
        // Ignore loginservlet in filter
        // to make it work with *
        // so all pages are filtered except login, avoids loops
        String uri = request.getRequestURI();
        if ( (session == null || session.getAttribute("user") == null) && !uri.endsWith("loginservlet")) {
//        if (session == null || session.getAttribute("user") == null) {
        	
        	System.out.println("[ApiFilter] Not logged in");
        	

        	// Get the printwriter object from response to write the required json object to the output stream      

        	response.setContentType("application/json");
        	response.getOutputStream().println("{error: \"unauthorized\"}");
//        	return;
//            response.sendRedirect(request.getContextPath() + "/login.html"); // No logged-in user found, so redirect to login page.
        } else {
        	System.out.println("[ApiFilter] logged in");
            chain.doFilter(req, res); // Logged-in user found, so just continue request.
        }
    }

    @Override
    public void destroy() {
        // If you have assigned any expensive resources as field of
        // this Filter class, then you could clean/close them here.
    }

}