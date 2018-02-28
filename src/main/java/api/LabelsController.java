package api;

import com.google.gson.Gson;
import data.CorpusRepository;
import data.Label;
import data.LabelRepository;
import data.UserRepository;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alto-boot")
@Configuration
@Component
@EnableAutoConfiguration
@EntityScan("data")
@EnableJpaRepositories("data")
public class LabelsController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private CorpusRepository corpusRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("defaultLabels")
    public void defaultLabels(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String corpusname = req.getParameter("corpusname");
        int corpusId = corpusRepository.findByCorpusName(corpusname).getCorpusId();
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String labels = new Gson().toJson(
                labelRepository.findByLabelSourceAndCorpus_CorpusId(
                        Label.LabelCreationSource.DEFAULT, corpusId
                ).stream()
                .map(Label::getLabelName)
                .collect(Collectors.toList()));

        System.out.println(labels);
        out.print(labels);
        out.flush();
    }

    @RequestMapping("addLabel")
    public void addLabel(HttpServletRequest req, HttpServletResponse resp){
        Label label = new Label(
                req.getParameter("labelName"),
                corpusRepository.findByCorpusName(req.getParameter("corpusname")),
                Label.LabelCreationSource.valueOf(req.getParameter("labelCreationSource")),
                userRepository.findByUserName(req.getParameter("username"))
        );
        labelRepository.save(label);
    }


}
