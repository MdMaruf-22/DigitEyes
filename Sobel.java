import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class Sobel {
    public void saveToCSV(int[][] matrix, String fileName) throws IOException {
        FileWriter csvWriter = new FileWriter(fileName);
        for (int y = 0; y < matrix[0].length; y++) {
            for (int x = 0; x < matrix.length; x++) {
                csvWriter.append(String.valueOf(matrix[x][y]));
                if (x < matrix.length - 1) {
                    csvWriter.append(",");
                }
            }
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
    public BufferedImage convertToGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage grascaleImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = (rgb) & 0xFF;
                int intensity = (int) (red + green + blue) / 3;
                int grayScaleIntensity = (intensity << 16) | (intensity << 8) | intensity;
                grascaleImage.setRGB(x, y, grayScaleIntensity);
            }
        }

        return grascaleImage;
    }
    public BufferedImage sobelAlgo(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        int[][] sobelHorizontal = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int[][] sobelVertical = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int pixelX = filterApply(image, x, y, sobelHorizontal);
                int pixelY = filterApply(image, x, y, sobelVertical);
                int gradient = (int) Math.sqrt(pixelX * pixelX + pixelY * pixelY);
                gradient = Math.min(gradient, 255);
                Color color = new Color(gradient, gradient, gradient);
                processedImage.setRGB(x, y, color.getRGB());
            }
        }

        return processedImage;
    }

    public int filterApply(BufferedImage image, int x, int y, int[][] operator) {
        int answer = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int pixel = image.getRGB(x + i, y + j) & 0xFF;
                answer += pixel * operator[i + 1][j + 1];
            }
        }
        return answer;
    }
    public int[][] convertMatrix(BufferedImage img,int val){
        int width = img.getWidth();
        int height = img.getHeight();
        int[][] mat = new int[width][height];
        for(int y=0;y<height;y++){
            for(int x=0;x<width;x++){
                int pixel = img.getRGB(x, y) & 0xFF;
                if(pixel>val) mat[x][y]=1;
                else mat[x][y]=0;
            }
        }
        return mat;
    }
    public void formatCSV(String inputFilePath, String outputFilePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] integers = line.split(",");
                StringBuilder formattedLine = new StringBuilder();

                for (String integer : integers) {
                    formattedLine.append("\"").append(integer).append("\",");
                }
                if (formattedLine.length() > 0) {
                    formattedLine.setLength(formattedLine.length() - 1);
                }

                writer.write(formattedLine.toString());
                writer.newLine();
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return resizedImage;
    }
}
