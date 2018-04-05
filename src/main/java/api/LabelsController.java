package api;

import data.*;
import data.entity.TaggingSession;
import data.repository.LabelRepository;
import data.repository.TaggingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private TaggingSessionRepository taggingSessionRepository;


    @GetMapping("/{corpus}/labels")
    @ResponseBody
    public List<String> getLabels(@PathVariable("corpus") String corpusName,
                                  @RequestParam(value = "source", defaultValue = "DEFAULT") Label.LabelCreationSource source)
    {

        Corpus corpus = corpusRepository.findByCorpusName(corpusName)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Requested corpus name %s is not configured.", corpusName)));

        return labelRepository.findByLabelSourceAndCorpus(source, corpus).stream()
            .map(Label::getLabelName)
            .collect(Collectors.toList());
    }

    @GetMapping("/{corpus}/{sessionId}/labels")
    @ResponseBody
    public List<String> getLabelsBySessionId(@PathVariable("corpus") String corpusName,
                                             @PathVariable("sessionId") UUID sessionId,
                                             @RequestParam(value = "source", defaultValue = "CREATED") Label.LabelCreationSource source) {

        return labelRepository.findByLabelSourceAndSession_SessionIdAndCorpus_CorpusName(source, sessionId, corpusName).stream()
            .map(l -> l.getLabelName())
            .collect(Collectors.toList());
    }

    @PostMapping("{corpus}/labels/{sessionId}/{labelName}")
    @ResponseBody
    public Label addLabel(@PathVariable("corpus") String corpusName,
                         @PathVariable("sessionId") UUID sessionId,
                         @PathVariable String labelName,
                         @RequestParam("source") Label.LabelCreationSource source){

        TaggingSession taggingSession = taggingSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Could not find valid session id: " + sessionId.toString()));

        Corpus corpus = getCorpus(corpusName);

        Label label = new Label(
                labelName,
                corpus,
                source,
                taggingSession
        );

        return labelRepository.save(label);
    }

    @DeleteMapping("{corpus}/labels/{sessionId}/{labelName}")
    @ResponseBody
    public int deleteLabel(@PathVariable("corpus") String corpusName,
                               @PathVariable("sessionId") UUID sessionId,
                               @PathVariable String labelName) {

        int res = labelRepository.deleteBySession_SessionIdAndLabelName(sessionId, labelName);
        return res;
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
