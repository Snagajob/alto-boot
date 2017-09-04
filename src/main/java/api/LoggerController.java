package api;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//TODO: if there is no label, throws an exception. FIX!
@Controller
@RequestMapping("/alto-release")
public class LoggerController {

	@RequestMapping("Logger")
	public void loggerRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //System.out.println("taking logs.....");
        String userName = req.getParameter("username");
        String corpusName = req.getParameter("corpusname");
        String logFileNum = req.getParameter("logfilenum");
        String logStr = req.getParameter("logStr");
        //String condition = req.getParameter("condition")
        String dir = util.Constants.RESULT_DIR+corpusName+"/log/";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        String filename = req.getServletContext().getRealPath("/"+dir+userName+"_log_"+logFileNum+"_"+timeStamp+".log");
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF8"));
        writer.write(logStr);
        writer.close();
        //System.out.println("finished taking logs");
	}
}
