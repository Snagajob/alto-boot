package api;

import data.User;
import data.UserRepository;
import data.entity.TaggingSession;
import data.repository.TaggingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/alto-boot")
public class TaggingSessionController {

    @Autowired
    TaggingSessionRepository sessionRepository;

    @Autowired
    UserRepository userRepository;

    @PostMapping("session")
    public String newSession(@ModelAttribute("username") String userName) {

        TaggingSession session = userRepository.findByUserName(userName)
                .map(this::getOrCreateMostRecentSession)
                .orElseGet(() -> createSessionForNewUser(userName));

        sessionRepository.save(session);

        String sessionId = session.getSessionId().toString();
        return String.format("redirect:/alto-boot/ui.html?username=%s&session_id=%s&studycondition=TA", userName, sessionId);
    }

    private TaggingSession getOrCreateMostRecentSession(User user) {
        List<TaggingSession> previousSessions = sessionRepository.findByUser(user);

        return previousSessions.stream()
                .findFirst()
                .orElseGet(() -> newSession(user));
    }

    static TaggingSession newSession(User u) {
        return new TaggingSession(UUID.randomUUID(), LocalDateTime.now(Clock.systemUTC()), u);
    }

    TaggingSession createSessionForNewUser(String userName) {
        User u = new User();
        u.setUserName(userName);
        userRepository.save(u);

        return newSession(u);
    }
}
