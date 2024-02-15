import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class DecisionTree {
    private String keyAttribute;
    private double calculateEntropy(String[][] dataset) {
        double entropy = 0.0;
        HashMap<String, Integer> cntclass = new HashMap<>();
        for (String[] pair : dataset) {
            String val = pair[getIndex(keyAttribute)];
            cntclass.put(val, cntclass.getOrDefault(val, 0) + 1);
        }
        for (int cnt : cntclass.values()) {
            double prob = (double) cnt / dataset.length;
            entropy -= (prob * Math.log(prob) / Math.log(2.0));
        }
        return entropy;
    }

    private double calculateAttributeEntropy(String[][] dataset, String attribute) {
        double entropy = 0.0;
        HashSet<String> attrValues = getAttributevalue(dataset, attribute);
        for (String value : attrValues) {
            String[][] subset = getSubset(dataset, attribute, value);
            double prob = (double) subset.length / dataset.length;
            entropy += calculateEntropy(subset) * prob;
        }
        return entropy;
    }

    private HashSet<String> getAttributevalue(String[][] dataset, String attribute) {
        HashSet<String> attributeValue = new HashSet<>();
        for (String[] pair : dataset) {
            attributeValue.add(pair[getIndex(attribute)]);
        }
        return attributeValue;
    }

    private String[][] getSubset(String[][] dataset, String attribute, String value) {
        int cnt = 0;
        for (String[] pair : dataset) {
            if (pair[getIndex(attribute)].equals(value)) {
                cnt++;
            }
        }
        String[][] subset = new String[cnt][dataset[0].length];
        int index = 0;
        for (String[] pair : dataset) {
            if (pair[getIndex(attribute)].equals(value)) {
                subset[index] = pair;
                index++;
            }
        }
        return subset;
    }

    private String bestAttribute(String[][] dataset, String[] attributes) {
        double maxInfogain = Double.MIN_VALUE;
        String bestAttribute = "";
        double entropy = calculateEntropy(dataset);
        for (String attribute : attributes) {
            double infoGain = entropy - calculateAttributeEntropy(dataset, attribute);
            if (infoGain > maxInfogain) {
                maxInfogain = infoGain;
                bestAttribute = attribute;
            }
        }
        return bestAttribute;
    }

    private int getIndex(String name) {
        String[] headerAttributes = Dataset.getAttributes();
        
        for (int i = 0; i < headerAttributes.length; i++) {
            if (headerAttributes[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private String getMajority(String[][] dataset) {
        HashMap<String, Integer> cntclass = new HashMap<>();
        for (String[] pair : dataset) {
            String val = pair[getIndex(keyAttribute)];
            cntclass.put(val, cntclass.getOrDefault(val, 0) + 1);
        }
        int mxcnt = -1;
        String majority = "";
        for (Map.Entry<String, Integer> entry : cntclass.entrySet()) {
            if (entry.getValue() > mxcnt) {
                mxcnt = entry.getValue();
                majority = entry.getKey();
            }
        }
        return majority;
    }

    private String[] removeAttr(String[] attributes, String remove) {
        String[] remaining = new String[attributes.length - 1];
        int ind = 0;
        for (String attribute : attributes) {
            if (!attribute.equals(remove)) {
                remaining[ind++] = attribute;
            }
        }
        return remaining;
    }

    private boolean isHomogeneous(String[][] dataset) {
        String target = dataset[0][getIndex(keyAttribute)];
        for (int i = 1; i < dataset.length; i++) {
            if (!dataset[i][getIndex(keyAttribute)].equals(target)) {
                return false;
            }
        }
        return true;
    }

    public TreeNode makeTree(String[][] dataset, String[] attributes) {
        if (attributes.length == 0) {
            String majority = getMajority(dataset);
            TreeNode leafNode = new TreeNode(majority);
            leafNode.setLeafValue(majority);
            return leafNode;
        }
        if (isHomogeneous(dataset)) {
            String target = dataset[0][getIndex(keyAttribute)];
            TreeNode leafNode = new TreeNode(target);
            leafNode.setLeafValue(target);
            return leafNode;
        }
        String bestAttribute = bestAttribute(dataset, attributes);
        TreeNode root = new TreeNode(bestAttribute);
        HashSet<String> attrValues = getAttributevalue(dataset, bestAttribute);
        for (String attribute : attrValues) {
            String[][] subset = getSubset(dataset, bestAttribute, attribute);
            if (subset.length == 0) {
                String majority = getMajority(subset);
                root.children.put(attribute, new TreeNode(majority));
            } else {
                String[] remainingAttributes = removeAttr(attributes, bestAttribute);
                root.children.put(attribute, makeTree(subset, remainingAttributes));
            }
        }
        return root;

    }

    public void printDecisionTree(TreeNode node, String indent) {
        System.out.println(indent + node.attribute + " ?");
        for (String value : node.children.keySet()) {
            System.out.println(indent + "|-- " + value);
            printDecisionTree(node.children.get(value), indent + "   ");
        }
    }

    public String predict(String[] instance, TreeNode node, Map<String, Integer> attributeIndices) {
        TreeNode current = node;
        while (!current.isLeaf()) {
            String attributeValue = instance[attributeIndices.get(current.attribute)];
            current = current.children.get(attributeValue);
        }
        //System.out.println("The digit is: " + current.getLeafValue());
        return current.getLeafValue();
    }
    
    public void setKey(String attr){
        this.keyAttribute=attr;
    }
    public void saveTree(TreeNode root,String path){
        try{
            FileOutputStream fo = new FileOutputStream(path);
            ObjectOutputStream os = new ObjectOutputStream(fo);
            os.writeObject(root);
            os.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public TreeNode loadTree(String path){
        TreeNode root = null;
        try {
            FileInputStream fi = new FileInputStream(path);
            ObjectInputStream os = new ObjectInputStream(fi);
            root = (TreeNode) os.readObject();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }
}
