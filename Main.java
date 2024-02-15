import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        Sobel sb = new Sobel();
        //Dataset d = new Dataset();
        DecisionTree dt = new DecisionTree();
        // String[][] dataset = d.getDataset("E:\\NEW\\mnist_modified(0_1).csv");
        // String[] attributes = d.getHeaderattributes("label");
        // TreeNode root = dt.makeTree(dataset, attributes);
        // dt.saveTree(root, "decision_tree.ser");
        // dt.printDecisionTree(root, "");
        TreeNode load = dt.loadTree("E:\\PROGRAMMING\\SPL-1\\res\\decision_tree.ser");
        // dt.printDecisionTree(load, "");
        dt.setKey("label");
        Map<String, Integer> attributeIndices = loadAttributeIndices();

        Scanner sc = new Scanner(System.in);
        System.out.print("How many pictures?: ");
        int cnt = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < cnt; i++) {
            System.out.print("Enter the image name(with extension): ");
            String path = sc.nextLine();
            try {
                File input = new File(path);
                BufferedImage image = ImageIO.read(input);
                BufferedImage resized = sb.resizeImage(image, 28, 28);
                BufferedImage grayScale = sb.convertToGrayscale(resized);
                BufferedImage procImg = sb.sobelAlgo(grayScale);
                
                int val = 150;
                int mat[][] = sb.convertMatrix(procImg, val);
                sb.saveToTXT(mat, "E:\\PROGRAMMING\\SPL-1\\res\\mat.txt");
                File output = new File("E:\\PROGRAMMING\\SPL-1\\res\\maruf.jpg");
                ImageIO.write(procImg, "jpg", output);

            } catch (Exception e) {
                e.printStackTrace();
            }

            makeMatrix();
            String[] test = loadTestData();
            dt.predict(test, load, attributeIndices);
        }
    }

    private static void makeMatrix() {
        String input = "E:\\PROGRAMMING\\SPL-1\\res\\mat.txt";
        String output = "E:\\PROGRAMMING\\SPL-1\\res\\arr.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            String line;
            StringBuilder concat = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                concat.append(line).append(",");
            }
            if (concat.length() > 0) {
                concat.deleteCharAt(concat.length() - 1);
            }
            writer.write(concat.toString());
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String[] loadTestData() {
        String[] test = new String[784];
        try (BufferedReader reader = new BufferedReader(new FileReader("E:\\PROGRAMMING\\SPL-1\\res\\arr.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] value = line.split(",");
                for (int j = 0; j < 784; j++) {
                    test[j] = value[j];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return test;
    }

    private static Map<String, Integer> loadAttributeIndices() {
        Map<String, Integer> attributeIndices = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("E:\\PROGRAMMING\\SPL-1\\res\\attributes.txt"))) {
            String line;
            if ((line = reader.readLine()) != null) {
                List<String> attributeList = Arrays.asList(line.split(","));
                for (int i = 0; i < attributeList.size(); i++) {
                    attributeIndices.put(attributeList.get(i), i);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attributeIndices;
    }
}