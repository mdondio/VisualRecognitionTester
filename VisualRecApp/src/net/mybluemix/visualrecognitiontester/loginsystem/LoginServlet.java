package net.mybluemix.visualrecognitiontester.loginsystem;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/loginservlet")
/**
 * This servlet handles authentication process.
 * @author Marco Dondio
 *
 */
public class LoginServlet extends HttpServlet {


	private static final long serialVersionUID = 1L;


	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String username = request.getParameter("username");
        String password = request.getParameter("password");
 
    	System.out.println("[LoginServlet] doPost: username = \"" + username + "\", password = \"" + password + "\"");

            String user = Authenticator.authenticateUser(username, password);
            
            // Logged correctly
            if (user != null) {
            	System.out.println("[LoginServlet] Logged correctly as \"" + user + "\"");
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home.html");
                return;
            } 

            // Invalid credentials!
        	System.out.println("[LoginServlet] Invalid credentials!");

        request.getRequestDispatcher("/login.html").forward(request, response);
    }

}