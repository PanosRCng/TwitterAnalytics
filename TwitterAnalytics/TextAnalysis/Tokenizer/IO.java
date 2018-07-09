package TwitterAnalytics.TextAnalysis.Tokenizer;


import java.util.HashMap;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;


public class IO
{

    public IO()
    {
        //
    }


    public static Vector<String> readFile(String filename)
    {
        Vector<String> lines = new Vector<>();

        try
        {
            FileInputStream fileStream = new FileInputStream( IO.getFilePath(filename) );

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream));

            String line;

            while( (line = bufferedReader.readLine()) != null )
            {
                lines.add(line);
            }

            bufferedReader.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return lines;
    }


    public static HashMap<String, String> readMapFile(String filename)
    {
        HashMap<String, String> map = new HashMap<>();

        try
        {
            FileInputStream fileStream = new FileInputStream( IO.getFilePath(filename) );

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream));

            String line;

            while( (line = bufferedReader.readLine()) != null )
            {
                String[] parts = line.split("\t");

                map.put(parts[0], parts[1]);
            }

            bufferedReader.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return map;
    }


    private static String getFilePath(String filename)
    {
        String package_str = IO.class.getPackage().getName().replace(".", File.separator);

        String filePath = package_str + File.separator + "data" + File.separator + filename;

        return new File(filePath).getAbsolutePath();
    }

}
