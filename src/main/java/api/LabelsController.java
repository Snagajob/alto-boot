package api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alto-boot")
public class LabelsController {

    private Map<String, List<String>> defaultLabels = ImmutableMap.of(
            "snag", ImmutableList.of(
                    "admin and paraprofessional",
                    "animal services",
                    "automotive and vehicle maintenence services",
                    "beauty and grooming services",
                    "childcare and early education",
                    "corporate professional and internships",
                    "corporate sales and marketing professional",
                    "direct sales and customer support",
                    "home housekeeping and maintenance services",
                    "exclude spanish language",
                    "food and beverage",
                    "health and wellness direct care",
                    "health and wellness home care",
                    "health and wellness professional",
                    "health and wellness technical",
                    "hospitality",
                    "industrial warehouse and manufacturing front line",
                    "industrial warehouse and manufacturing professional",
                    "industrial warehouse and manufacturing skilled/technical",
                    "information technology professional",
                    "military recruiting and contracting",
                    "personal instruction and tutoring",
                    "real estate sales and related services",
                    "renovation contracting construction commercial",
                    "renovation contracting construction home",
                    "retail",
                    "retail grocery",
                    "security services",
                    "trucking and transportation specialized licensed",
                    "trucking and transportation taxi and delivery"
            ),
            "synthetic", ImmutableList.of(
                    "directions",
                    "animals",
                    "cleaning",
                    "numbers",
                    "days"
            )

    );

    @RequestMapping("Labels")
    public void defaultLabels(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String corpusname = req.getParameter("corpusname");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        String labels = new Gson().toJson(defaultLabels.get(corpusname));
        System.out.println(labels);
        out.print(labels);
        out.flush();
    }


}
