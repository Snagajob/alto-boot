package api;

import data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/alto-boot")
@Configuration
@Component
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
                                  @RequestParam(value = "source", defaultValue = "DEFAULT") Label.LabelCreationSource source)
    {

        Corpus corpus = corpusRepository.findByCorpusName(corpusName)
                .orElseThrow(() -> new IllegalArgumentException("Requested corpus name is not configured."));

        int corpusId = corpus.getCorpusId();

        return labelRepository.findByLabelSourceAndCorpus(source, corpus).stream()
            .map(Label::getLabelName)
            .collect(Collectors.toList());
    }

    @GetMapping("/{corpus}/labels/{userName}")
    @ResponseBody
    public List<String> getLabelsByUser(@PathVariable("corpus") String corpusName,
                                        @PathVariable("userName") String userName,
                                        @RequestParam(value = "source", defaultValue = "DEFAULT") Label.LabelCreationSource source)
    {
        User user = getUser(userName);
        Corpus corpus = getCorpus(corpusName);

        return labelRepository.findByLabelSourceAndCorpusAndUser(source, corpus, user).stream()
                .map(Label::getLabelName)
                .collect(Collectors.toList());
    }

    @PutMapping("{corpus}/labels/{userName}/{labelName}")
    @ResponseBody
    public Label addLabel(@PathVariable("corpus") String corpusName,
                         @PathVariable("userName") String userName,
                         @PathVariable String labelName,
                         @RequestParam("source") Label.LabelCreationSource source){

        User user = getUser(userName);
        Corpus corpus = getCorpus(corpusName);

        Label label = new Label(
                labelName,
                corpus,
                source,
                user
        );

        return labelRepository.save(label);
    }


    private User getUser(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Couldn't find specified user."));
    }

    private Corpus getCorpus(String corpusName) {
        return corpusRepository.findByCorpusName(corpusName)
                .orElseThrow(() -> new IllegalArgumentException("Requested corpus is not configured"));

    }
}
