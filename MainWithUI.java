import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MainWithUI {
    private static String selectedImagePath;
    private static Sobel sb = new Sobel();
    private static DecisionTree dt = new DecisionTree();
    private static Map<String, Integer> attributeIndices = loadAttributeIndices();
    private static TreeNode load = dt.loadTree("E:\\PROGRAMMING\\SPL-1\\res\\decision_tree.ser");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Digit Eyes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        JButton selectImageButton = new JButton("Select Image");
        JButton exitButton = new JButton("Exit");

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(selectImageButton);
        buttonPanel.add(exitButton);

        frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImagePath = fileChooser.getSelectedFile().getAbsolutePath();
            processSelectedImage();
        }
    }

    private static void processSelectedImage() {
        try {
            File input = new File(selectedImagePath);
            System.out.println("Selected Image Path: " + selectedImagePath);
            BufferedImage image = ImageIO.read(input);
            BufferedImage resized = sb.resizeImage(image, 28, 28);
            BufferedImage grayScale = sb.convertToGrayscale(resized);
            BufferedImage procImg = sb.sobelAlgo(grayScale);

            int val = 150;
            int mat[][] = sb.convertMatrix(procImg, val);
            sb.saveToTXT(mat, "E:\\PROGRAMMING\\SPL-1\\res\\mat.txt");
            File output = new File("E:\\PROGRAMMING\\SPL-1\\res\\maruf.jpg");
            ImageIO.write(procImg, "jpg", output);

            makeMatrix();
            String[] test = loadTestData();
            String predictionResult = dt.predict(test, load, attributeIndices);

            // Show the output on a side of the window
            JFrame outputFrame = new JFrame("Image Processing Output");
            outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JTextArea outputTextArea = new JTextArea();
            outputTextArea.setEditable(false);
            outputTextArea.setText("The digit may be: " + predictionResult);

            JScrollPane scrollPane = new JScrollPane(outputTextArea);
            outputFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            outputFrame.setSize(300, 200);
            outputFrame.setLocationRelativeTo(null);
            outputFrame.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing the image.", "Error", JOptionPane.ERROR_MESSAGE);
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