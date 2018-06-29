import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class UserRetweeterGraph {

    Graph<Long, DefaultEdge> graph;

    public UserRetweeterGraph() {

        this.graph = new DefaultDirectedGraph<Long, DefaultEdge>(DefaultEdge.class);

    }

    private static class SingletonHelper
    {
        private static final UserRetweeterGraph INSTANCE = new UserRetweeterGraph();
    }


    public static Graph<Long, DefaultEdge> getInstance()
    {
        return SingletonHelper.INSTANCE.graph;
    }
}
