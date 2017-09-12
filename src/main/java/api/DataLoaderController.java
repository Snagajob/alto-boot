package api;

import alto.Backend;
import alto.ErrorForUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class DataLoaderController
 */
@Controller
@RequestMapping("/alto-release")
public class DataLoaderController {

	@Autowired
    Backend backend;

	@RequestMapping(value = "DataLoader", method = RequestMethod.GET)
    public void dataLoaderGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @RequestMapping(value = "DataLoader", method = RequestMethod.POST)
    public void dataLoaderPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String json="";
	try{	
		String username = req.getParameter("username");
		json = backend.newSession(username);
		
		System.out.println(json);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		out.print(json);
		out.flush();
	}catch(ErrorForUI e){
		out.print("{\"hasError\": true, \"message\":"+e.getMessage()+", \"stack:\":+"+e.getStack()
				+", \"base directory: "+util.Constants.RESULT_DIR+"\" }");
		out.flush();
	}
	}
}
