import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Main {
    public static void main(String[] args) {
        Sobel sb = new Sobel();
        Dataset d = new Dataset();
        DecisionTree dt = new DecisionTree();
        String[][] dataset = d.getDataset("100.csv");
        String[] attributes = d.getHeaderattributes("label");
        dt.setKey("label");
        TreeNode root = dt.makeTree(dataset, attributes);
        // dt.printDecisionTree(root, "");
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
                BufferedImage processedImage = sb.sobelAlgo(grayScale);
                int val = 150;
                int mat[][] = sb.convertMatrix(processedImage, val);
                sb.saveToCSV(mat, "mat.csv");
                File output = new File("maruf.jpg");
                ImageIO.write(processedImage, "jpg", output);

            } catch (Exception e) {
                e.printStackTrace();
            }
            String input = "mat.csv";
            String output = "arr.csv";
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
            sb.formatCSV("arr.csv", "stringmatrix.csv");

            String csvFilePath = "stringmatrix.csv";
            String[] test = new String[784];
            try {
                BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
                String line = reader.readLine();

                if (line != null) {
                    String[] values = line.split(",");
                    for (int j = 0; j < 784; j++) {
                        test[j] = values[j].replaceAll("\"", "");
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            dt.predict(test, root);
        }
    }
}
