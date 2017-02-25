package myservlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import services.Authenticator;

@WebServlet("/loginservlet")
public class LoginServlet extends HttpServlet {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println("[LoginServlet] doGet");
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	System.out.println("[LoginServlet] doPost");

    	String username = request.getParameter("username");
        String password = request.getParameter("password");
 

        // TODO metodo:
            String user = Authenticator.find(username, password);

            //String user = null;
            
            // Logged correctly
            if (user != null) {
            	System.out.println("[LoginServlet] Logged as " + user);
                request.getSession().setAttribute("user", user);
                response.sendRedirect(request.getContextPath() + "/home.html");
                return;
            } 

            // Invalid credentials!
        	System.out.println("[LoginServlet] Invalid credentials!");

        request.getRequestDispatcher("/login.html").forward(request, response);
    }

}