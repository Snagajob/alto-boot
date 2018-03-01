package api;

import com.google.gson.Gson;
import data.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/alto-boot")
@Configuration
@Component
//@EnableAutoConfiguration
//@EntityScan("data")
//@EnableJpaRepositories("data")
public class LabelsController {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private CorpusRepository corpusRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/{corpus}/labels")
    @ResponseBody
    public List<String> getLabels(@PathVariable("corpus") String corpusName,
                                  @RequestParam(value = "source", defaultValue = "DEFAULT") Label.LabelCreationSource source) throws IOException {

        Optional<Corpus> corpus = corpusRepository.findByCorpusName(corpusName);

        if(!corpus.isPresent()) {
            throw new IllegalArgumentException("Requested corpus name is not configured.");
        }

        int corpusId = corpus.get().getCorpusId();

        return labelRepository.findByLabelSourceAndCorpus_CorpusId(source, corpusId).stream()
            .map(Label::getLabelName)
            .collect(Collectors.toList());
    }

    @RequestMapping(name = "{corpus}/labels", method = RequestMethod.PUT)
    public void addLabel(HttpServletRequest req, HttpServletResponse resp){

        Label label = new Label(
                req.getParameter("labelName"),
                corpusRepository.findByCorpusName(req.getParameter("corpusname")).get(),
                Label.LabelCreationSource.valueOf(req.getParameter("labelCreationSource")),
                userRepository.findByUserName(req.getParameter("username"))
        );
        labelRepository.save(label);
    }


}
