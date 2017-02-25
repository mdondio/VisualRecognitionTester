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

// http://stackoverflow.com/questions/6560969/how-to-define-servlet-filter-order-of-execution-using-annotations-in-war
@WebFilter(filterName="LoginFilter", urlPatterns="/*")
//@WebFilter(filterName="LoginFilter", urlPatterns="/logged/*")
public class LoginFilter implements Filter {

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

  
        String uri = request.getRequestURI();

        // Allow different files
        if(uri.matches(".*(css|jpg|png|gif|js)") || uri.contains("login.html")){
            chain.doFilter(request, response);
            return;
        }
        
        
        
        if ( (session == null || session.getAttribute("user") == null) && !uri.contains("loginservlet")) {
//            if ( (session == null || session.getAttribute("user") == null) && !uri.endsWith("loginservlet")) {
//        if (session == null || session.getAttribute("user") == null) {
        	
        	System.out.println("[LoginFilter] Not logged in");
            response.sendRedirect(request.getContextPath() + "/login.html"); // No logged-in user found, so redirect to login page.
        } else {
        	System.out.println("[LoginFilter] logged in");
            chain.doFilter(req, res); // Logged-in user found, so just continue request.
        }
    }

    @Override
    public void destroy() {
        // If you have assigned any expensive resources as field of
        // this Filter class, then you could clean/close them here.
    }

}