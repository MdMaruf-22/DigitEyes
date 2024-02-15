import java.io.BufferedReader;
import java.io.FileReader;

public class Dataset {
    private static String[] attributes = null;
    public String[][] getDataset(String path){
        int rows = cntLine(path)-1;
        String[][] dataset = new String[rows][];
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String header = br.readLine();
            attributes = header.split(",");
            int index = 0;
            String line;
            while((line = br.readLine())!=null){
                String values[] = line.split(",");
                dataset[index] = new String[attributes.length];
                for(int i=0;i<attributes.length;i++){
                    String value = values[i];
                    dataset[index][i]=value;
                }
                index++;
            }
            br.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return dataset;
    }
    public int cntLine(String path){
        int cnt=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while(br.readLine()!=null){
                cnt++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }
    public String[] getHeaderattributes(String keyattr){
        String[] headerAttributes = new String[attributes.length-1];
        int ind=0;
        for(int i=0;i<attributes.length;i++){
            if(!attributes[i].equals(keyattr)) headerAttributes[ind++]=attributes[i];

        }
        return headerAttributes;
    }
    public static String[] getAttributes(){
        return attributes;
    }
}
