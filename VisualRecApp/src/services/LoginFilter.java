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

/**
 * Filtro per tutte le richieste: draft velocissima
 * @author Marco Dondio
 *
 */
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {
	// http://stackoverflow.com/questions/6560969/how-to-define-servlet-filter-order-of-execution-using-annotations-in-war

	@Override
	public void init(FilterConfig config) throws ServletException {
		// If you have any <init-param> in web.xml, then you could get them
		// here by config.getInitParameter("name") and assign it as field.
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String uri = request.getRequestURI();

		// Allow different files without filter:
		if (uri.matches(".*(css|jpg|png|gif|js)") || 
				uri.matches(".*AAHV8svrX81twiW48d2N6bpmQuv6BnnKKQU") ||
				uri.contains("login.html") ||
				uri.contains("loginservlet")||
				uri.contains("logoutservlet")) {
			chain.doFilter(request, response);
			return;
		}

		// check for logged status...
		if (session == null || session.getAttribute("user") == null) {

			System.out.println("[LoginFilter] Not logged in");

			// Check if api method
			if (uri.matches(".*/api/.*")){
				System.out.println("[LoginFilter] Returning API error");
				response.setContentType("application/json");
				response.getOutputStream().println("{error: \"unauthorized\"}");
				return;
			} else // not logged and not api: redirect to login
				response.sendRedirect(request.getContextPath() + "/login.html");
		} else {
			System.out.println("[LoginFilter] logged in");
			chain.doFilter(req, res);
		}

	}

	@Override
	public void destroy() {
		// If you have assigned any expensive resources as field of
		// this Filter class, then you could clean/close them here.
	}

}