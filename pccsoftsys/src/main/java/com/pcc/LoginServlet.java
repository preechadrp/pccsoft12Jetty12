package com.pcc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = -1079681049977214895L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//System.out.println("session timeout : " + request.getSession().getServletContext().getSessionTimeout());
		request.getRequestDispatcher("./login.zul").forward(request, response);
		//response.sendRedirect("./menu.zul");
		
	}
	
}
