package TwitterAnalytics.TextAnalysis.Sentimenter;

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


    public static Vector<Vector<String>> readFile(String filename)
    {
        Vector<Vector<String>> lines = new Vector<Vector<String>>();

        try
        {
            FileInputStream fileStream = new FileInputStream( IO.getFilePath(filename) );

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileStream));

            String line;

            while( (line = bufferedReader.readLine()) != null )
            {
                lines.add( IO.parseTSVLine(line) );
            }

            bufferedReader.close();
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return lines;
    }


    private static Vector<String> parseTSVLine(String tsv_line)
    {
        Vector<String> parts = new Vector<String>();

        for(String part : tsv_line.split("\t"))
        {
            parts.add(part);
        }

        return parts;
    }


    private static String getFilePath(String filename)
    {
        String package_str = IO.class.getPackage().getName().replace(".", File.separator);

        String filePath = package_str + File.separator + "data" + File.separator + filename;

        return new File(filePath).getAbsolutePath();
    }

}
