import java.io.*;
import java.util.*; 

/* I am not doing strong error checking. Sorry... */

public class WordCountJ{
    public static void main(String args[]){
        int nfiles; 
        nfiles = args.length;
        if ( nfiles == 0 ) {
            System.out.println("Usage: java WordCountJ inputfile0 inputfile1 ...");
            System.exit(0); 
        }

        System.out.println("Input files:");
        for ( int i = 0 ; i < args.length ; i++ ) {
            System.out.println(args[i]);
        }

        List< Map<String, Integer> > wordCounts 
            = new ArrayList< Map<String, Integer> >();

        // Build thread list
        ArrayList<ThreadCountOneFile> threadlist 
            = new ArrayList<ThreadCountOneFile>();
        for ( int i = 0 ; i < args.length ; i++ ) {
            ThreadCountOneFile t = new ThreadCountOneFile(args[i]);
            threadlist.add( t );
            t.start();
        }

        for ( ThreadCountOneFile t: threadlist ) {
            try{
                t.join();
                wordCounts.add( t.getWordCount() );
            } catch (Exception ex){}
        }

        Map<String, Integer> totalWCount = new HashMap<String, Integer>();
        for ( Map<String, Integer> wcount: wordCounts ) {
            for (Map.Entry<String, Integer> entry : wcount.entrySet())
            {
                String iword = entry.getKey();
                Integer icount = entry.getValue();
                Integer count = totalWCount.get(iword);
                totalWCount.put(iword, count == null ? icount : count + icount);
            }           
        }
        
        System.out.println("-- Display the total word count --");
        for ( Map.Entry<String, Integer> entry : totalWCount.entrySet() )
        {
            System.out.println(entry.getKey() + "      " + entry.getValue());
        }
    }
}

class ThreadCountOneFile extends Thread
{
    private String _filename;
    private Map<String, Integer> _wordCount;

    public ThreadCountOneFile(String fname)
    {
        this._filename = fname;
        this._wordCount = new HashMap<String, Integer>();
    }
    
    public Map<String, Integer> getWordCount()
    {
        return this._wordCount;
    }

    @Override
    public void run()
    {
         try{
            BufferedReader br = new BufferedReader(new FileReader(_filename));
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                //System.out.println("---");
                for (String word: line.split("\\s+")){
                    // trim prefixing nonalphameric
                    word = word.replaceFirst("[^a-zA-Z0-9\\s]*", "");
                    word = new StringBuffer(word).reverse().toString();
                    word = word.replaceFirst("[^a-zA-Z0-9\\s]*", "");
                    word = new StringBuffer(word).reverse().toString();
                    //System.out.println(word);

                    Integer count = _wordCount.get(word);
                    _wordCount.put(word, count == null ? 1 : count + 1);
                }
            }
            br.close();

            // Display map
            /*
            for (Map.Entry<String, Integer> entry : _wordCount.entrySet())
            {
                System.out.println(entry.getKey() + "/" + entry.getValue());
            }
            */

            } catch(IOException e) {
                System.out.print("Exception");
            }

    }
}

