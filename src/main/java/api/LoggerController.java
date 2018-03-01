package api;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//TODO: if there is no label, throws an exception. FIX!
@Controller
@RequestMapping("/alto-boot")
public class LoggerController {

    @Value("${alto.data.corpus_name:synthetic}")
    String corpusName;

    @Value("${alto.data.base_dir:/usr/local/alto-boot}")
    String dataDirectory;

	@RequestMapping("Logger")
	public void loggerRoute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //System.out.println("taking logs.....");
        String userName = req.getParameter("username");
        String logFileNum = req.getParameter("logfilenum");
        String logStr = req.getParameter("logStr");
        //String condition = req.getParameter("condition")
        String dir = this.dataDirectory + "/" + "/" + this.corpusName+"/log/";
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        String filename = dir + userName+"_log_"+logFileNum+"_"+timeStamp+".log";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF8"));
        writer.write(logStr);
        writer.close();
        //System.out.println("finished taking logs");
	}
}
