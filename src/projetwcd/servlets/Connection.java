package projetwcd.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.*;

@SuppressWarnings("serial")
public class Connection extends HttpServlet {
	public static final String VUE              = "/WEB-INF/connection.jsp";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		this.getServletContext().getRequestDispatcher( VUE ).forward( request, response );
	}
}
