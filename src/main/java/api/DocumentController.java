package api;

import data.CorpusRepository;
import data.Label;
import data.UserRepository;
import data.entity.DocumentLabel;
import data.entity.TaggingSession;
import data.repository.DocumentLabelsRepository;
import data.repository.LabelRepository;
import data.repository.TaggingSessionRepository;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import util.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/alto-boot")
public class DocumentController {

    private final DocumentLabelsRepository documentLabelsRepository;
    private final LabelRepository labelRepository;
    private final CorpusRepository corpusRepository;
    private final UserRepository userRepository;
    private final TaggingSessionRepository taggingSessionRepository;

    public DocumentController(DocumentLabelsRepository documentLabelsRepository,
                              CorpusRepository corpusRepository,
                              LabelRepository labelRepository,
                              UserRepository userRepository,
                              TaggingSessionRepository taggingSessionRepository) {
        this.documentLabelsRepository = documentLabelsRepository;
        this.labelRepository = labelRepository;
        this.corpusRepository = corpusRepository;
        this.userRepository = userRepository;
        this.taggingSessionRepository = taggingSessionRepository;
    }

    @PostMapping("/{corpusName}/documents/{documentId}/{sessionId}/labels/{labelName}")
    @ResponseBody
    public DocumentLabel associateLabels(@PathVariable String corpusName,
                            @PathVariable String documentId,
                            @PathVariable String sessionId,
                            @PathVariable String labelName,
                            @RequestParam("score") java.lang.Double score) {

        //basic logic -

        // - if the label does not exist, create it and associate with teh document,
        // - if it does exist, just add the linkage to a new document label table.

        DocumentLabel documentLabel = this.labelRepository.findBySession_SessionIdAndLabelName(UUID.fromString(sessionId), labelName)
                .map(createDocLabelFrom(documentId, score))
                .orElseGet(() -> {
                    //create the label first, then asscociate the fields with the label...
                    //get the session:
                    TaggingSession session =
                            taggingSessionRepository.findBySessionId(UUID.fromString(sessionId))
                                .orElseThrow(() -> new IllegalArgumentException());

                    //get the corpus:
                    return corpusRepository.findByCorpusName(corpusName)
                            .map(c -> {
                                Label l = new Label(labelName, c, Label.LabelCreationSource.CREATED, session);
                                return labelRepository.save(l);
                            })
                            .map(createDocLabelFrom(documentId, score))
                            .orElseThrow(() -> new IllegalStateException("Specified corpus does not exist."));
                });

        return documentLabelsRepository.save(documentLabel);
    }

    @GetMapping("/{corpusName}/documents/{sessionId}/labels")
    @ResponseBody
    public Map<Integer,String> getAllDocumentLabelsForSession(@PathVariable String corpusName,
                                                              @PathVariable UUID sessionId) {

        //this is pretty sloppy, but its easiest - hibernate is starting to get unwieldly....

        Map<Integer, List<Label>> labelsMap = labelRepository.findBySession_SessionId(sessionId).stream()
                .collect(Collectors.groupingBy(x -> x.getLabelId()));

        Map<Integer,String> output = new HashMap<>();

        documentLabelsRepository.findBySessionId(sessionId)
                .stream()
                .forEach(docLabel -> {
                    Label label = labelsMap.get(docLabel.getLabelId()).stream().findFirst().get();
                    output.put(Integer.parseInt(docLabel.getDocumentId()), label.getLabelName());
                });

        return output;
    }


    Function<Label,DocumentLabel> createDocLabelFrom(String documentId, Double score) {
        Function<Label,DocumentLabel> docLabelCreateFunc = x -> {
            DocumentLabel l = new DocumentLabel();
            l.setLabelId(x.getLabelId());
            l.setConfirmed(false);
            l.setScore(score);
            l.setDocumentId(documentId);
            l.setSessionId(x.getSession().getSessionId());

            return l;
        };

        return docLabelCreateFunc;
    }
}
