import TwitterAnalytics.DB;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.HttpStatus;

public class TopUsersServlet extends HttpServlet {

    private final long userID;

    private static final Joiner JOINER = Joiner.on(",\n");

    public TopUsersServlet(long userID) {
        this.userID = userID;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        int k = 10;
        String p = req.getParameter("k");
        if (p != null) {
            try {
                k = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                // Just eat it, don't need to worry.
            }
        }

        String user_tweets = "select userID, count(statusID) as count from tempStatus group by userID";

        PreparedStatement ps = null;

        List<String> entries = new ArrayList<String>(k);

        int counter = 0;

        try {
            ps = DB.conn().prepareStatement(user_tweets);

            ResultSet rs = ps.executeQuery();

            boolean checkDB = rs.next();

            if(checkDB){

                do {
                    counter++;
                    entries.add(String.format("{\"id_str\": \"%d\", \"cnt\": %d}", rs.getLong("userID"), rs.getInt("count")));
                    if(counter==k) break;
                }while (rs.next());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //resp.setStatus(HttpStatus.OK_200);
        resp.getWriter().println("[\n" + JOINER.join(Lists.reverse(entries)) + "\n]");
    }
}