package com.pcc;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {

	private static final long serialVersionUID = -1079681049977214895L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.sendRedirect("./login");
		
	}
	
}
