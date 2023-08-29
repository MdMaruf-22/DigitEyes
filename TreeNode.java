import java.util.HashMap;

public class TreeNode {
    public String attribute;
    public HashMap<String, TreeNode> children;
    public String leafValue;

    public TreeNode(String attribute) {
        this.attribute = attribute;
        this.children = new HashMap<>();
        this.leafValue = null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public String getLeafValue() {
        return leafValue;
    }

    public void setLeafValue(String leafValue) {
        this.leafValue = leafValue;
    }
}
