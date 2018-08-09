package TwitterAnalytics.InvertedIndex;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Vector;


public class InvertedIndex
{
    public static final String INVERTED_INDEXES_PATH = "Data/inverted_indexes/";


    private String indexName;
    private IndexWriter indexWriter;



    public InvertedIndex(String indexName, IndexWriter indexWriter)
    {
        this.indexName = indexName;
        this.indexWriter = indexWriter;
    }


    public void insert(String id, String text)
    {
        try
        {
            Field idField = new StringField("id", id, Field.Store.YES);
            Field termsField = new Field("terms", text, TextField.TYPE_STORED);

            Document doc = new Document();
            doc.add(idField);
            doc.add(termsField);

            this.indexWriter.updateDocument(new Term("id", id), doc);
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }


    public Vector<String> topNerms(int N)
    {
        Vector<String> terms = new Vector<>();

        try
        {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INVERTED_INDEXES_PATH + indexName)));

            HighFreqTerms.TotalTermFreqComparator comparator = new HighFreqTerms.TotalTermFreqComparator();

            TermStats[] termStats = HighFreqTerms.getHighFreqTerms(reader, N, "terms", comparator);

            for(TermStats termStat : termStats)
            {
                terms.add( termStat.termtext.utf8ToString() );
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }

        return terms;
    }


    public void showLexicon()
    {
        try
        {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INVERTED_INDEXES_PATH + indexName)));

            Terms terms = MultiFields.getTerms(reader, "terms");

            TermsEnum termsEnum = terms.iterator();

            int counter = 0;

            while(termsEnum.next() != null)
            {
                long tf = termsEnum.totalTermFreq();
                long df = termsEnum.docFreq();

                System.out.println(counter++ + ". " + termsEnum.term().utf8ToString() + " tf:" + tf + " df:" + df);
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.toString());
        }
    }


    public void close()
    {
        try
        {
            this.indexWriter.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString());
        }
    }


    public static InvertedIndex open(String indexName)
    {

        try
        {
            Directory directory = FSDirectory.open(Paths.get(INVERTED_INDEXES_PATH + indexName));

            Analyzer analyzer = new SimpleAnalyzer();

            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(directory, config);

            return new InvertedIndex(indexName, writer);
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString());
        }

        return null;
    }


}
