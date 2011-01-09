package sagex.phoenix.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.phoenix.fanart.AdvancedFanartMediaRequestHandler;

public class FanartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private transient AdvancedFanartMediaRequestHandler handler = new AdvancedFanartMediaRequestHandler();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FanartServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handler.processRequest(req, resp);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
