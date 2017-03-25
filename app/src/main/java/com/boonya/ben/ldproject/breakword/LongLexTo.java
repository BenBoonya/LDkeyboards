package com.boonya.ben.ldproject.breakword;

import android.content.Context;

import com.boonya.ben.ldproject.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by apple on 12/29/15.
 */
public class LongLexTo {
    //Private variables
    private Trie dict;               //For storing words from dictionary
    private LongParseTree ptree;     //Parsing tree (for Thai words)

    //Returned variables
    private Vector indexList;  //List of word index positions
    private Vector lineList;   //List of line index positions
    private Vector typeList;   //List of word types (for word only)
    private Iterator iter;     //Iterator for indexList OR lineList (depends on the call)
    private Context context;

    /*******************************************************************/
    /*********************** Return index list *************************/
    /*******************************************************************/
    public Vector getIndexList() {
        return indexList;
    }

    /*******************************************************************/
    /*********************** Return type list *************************/
    /*******************************************************************/
    public Vector getTypeList() {
        return typeList;
    }

    /*******************************************************************/
    /******************** Iterator for index list **********************/
    /*******************************************************************/
    //Return iterator's hasNext for index list
    public boolean hasNext() {
        if (!iter.hasNext())
            return false;
        return true;
    }

    //Return iterator's first index
    public int first() {
        return 0;
    }

    //Return iterator's next index
    public int next() {
        return ((Integer) iter.next()).intValue();
    }

    /*******************************************************************/
    /********************** Constructor (default) **********************/
    /*******************************************************************/
    public LongLexTo(String input) throws IOException {
        System.out.println("Self dict");
        dict = new Trie();
//    File dictFile=new File("lexitron.txt");
//    if(dictFile.exists())
//      addDict(dictFile);
//    else
//      System.out.println(" !!! Error: Missing default dictionary file, lexitron.txt");
        indexList = new Vector();
        lineList = new Vector();
        typeList = new Vector();
        ptree = new LongParseTree(dict, indexList, typeList);
    }

    public LongLexTo(Context context) throws IOException {
        this.context = context;
        dict = new Trie();
        addDict();
//        File sdcard = Environment.getExternalStorageDirectory();
//
//        File dictFile=new File(sdcard,"lexitron.txt");
//        if(dictFile.exists())
//            addDict(dictFile);
//        else
//            System.out.println(" !!! Error: Missing default dictionary file, lexitron.txt");
        indexList = new Vector();
        lineList = new Vector();
        typeList = new Vector();
        ptree = new LongParseTree(dict, indexList, typeList);
    } //Constructor

    /*******************************************************************/
    /************** Constructor (passing dictionary file ) *************/
    /*******************************************************************/
    public LongLexTo(File dictFile) throws IOException {
        System.out.println("INLEXTO");
        dict = new Trie();
        if (dictFile.exists()) {
            addDict(dictFile);
            System.out.println("ADDED DICT");
        } else
            System.out.println(" !!! Error: The dictionary file is not found, " + dictFile.getName());
        indexList = new Vector();
        lineList = new Vector();
        typeList = new Vector();
        ptree = new LongParseTree(dict, indexList, typeList);
    } //Constructor

    /*******************************************************************/
    /**************************** addDict ******************************/
    /*******************************************************************/
    public void addDict(String line) throws IOException {

        line = line.trim();
        if (line.length() > 0)
            dict.add(line);
    }

    private void addDict() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                dict=new Trie();
//                MySQLiteHelper db = new MySQLiteHelper(context);
//                ArrayList<String> thaiWords = db.getAllThaiWords();
//                for(String s:thaiWords ){
//                    dict.add(s);
//                    //Log.i("Thai Dict",s);
//                }
//
//
//            }
//        }).start();
        InputStream inputStream = context.getResources().openRawResource(R.raw.lexitron);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        new Thread((new Runnable() {

            String line = null;

            @Override
            public void run() {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (line != null) {

                    //Log.i("Each Line", line);
                    dict.add(line);
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        })).start();


    }

    public void addDict(File dictFile) throws IOException {

        //Read words from dictionary
        String line, word, word2;
        int index;
        FileReader fr = new FileReader(dictFile);
        BufferedReader br = new BufferedReader(fr);

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() > 0)
                dict.add(line);
            //System.out.println(line);
        }


    } //addDict

    /****************************************************************/
    /************************** wordInstance ************************/
    /****************************************************************/
    public void wordInstance(String text) {
        System.out.println("I'm In wordInStance");
        indexList.clear();
        typeList.clear();
        int pos, index;
        String word;
        boolean found;
        char ch;

        pos = 0;
        while (pos < text.length()) {

            //Check for special characters and English words/numbers
            ch = text.charAt(pos);

            //English
            if (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z'))) {
                while ((pos < text.length()) && (((ch >= 'A') && (ch <= 'Z')) || ((ch >= 'a') && (ch <= 'z'))))
                    ch = text.charAt(pos++);
                if (pos < text.length())
                    pos--;
                indexList.addElement(new Integer(pos));
                typeList.addElement(new Integer(3));
            }
            //Digits
            else if (((ch >= '0') && (ch <= '9')) || ((ch >= '๐') && (ch <= '๙'))) {
                while ((pos < text.length()) && (((ch >= '0') && (ch <= '9')) || ((ch >= '๐') && (ch <= '๙')) || (ch == ',') || (ch == '.')))
                    ch = text.charAt(pos++);
                if (pos < text.length())
                    pos--;
                indexList.addElement(new Integer(pos));
                typeList.addElement(new Integer(3));
            }
            //Special characters
            else if ((ch <= '~') || (ch == 'ๆ') || (ch == 'ฯ') || (ch == '“') || (ch == '”') || (ch == ',')) {
                pos++;
                indexList.addElement(new Integer(pos));
                typeList.addElement(new Integer(4));
            }
            //Thai word (known/unknown/ambiguous)
            else {
                //  System.out.println(text);
                //System.out.println("I do in ELSE");

                pos = ptree.parseWordInstance(pos, text);

            }
        } //While all text length
        iter = indexList.iterator();
    } //wordInstance

    /****************************************************************/
    /************************** lineInstance ************************/
    /****************************************************************/
    public void lineInstance(String text) {

        int windowSize = 10; //for detecting parentheses, quotes
        int curType, nextType, tempType, curIndex, nextIndex, tempIndex;
        lineList.clear();
        wordInstance(text);
        int i;
        for (i = 0; i < typeList.size() - 1; i++) {
            curType = ((Integer) typeList.elementAt(i)).intValue();
            curIndex = ((Integer) indexList.elementAt(i)).intValue();

            if ((curType == 3) || (curType == 4)) {
                //Parenthesese
                if ((curType == 4) && (text.charAt(curIndex - 1) == '(')) {
                    int pos = i + 1;
                    while ((pos < typeList.size()) && (pos < i + windowSize)) {
                        tempType = ((Integer) typeList.elementAt(pos)).intValue();
                        tempIndex = ((Integer) indexList.elementAt(pos++)).intValue();
                        if ((tempType == 4) && (text.charAt(tempIndex - 1) == ')')) {
                            lineList.addElement(new Integer(tempIndex));
                            i = pos - 1;
                            break;
                        }
                    }
                }
                //Single quote
                else if ((curType == 4) && (text.charAt(curIndex - 1) == '\'')) {
                    int pos = i + 1;
                    while ((pos < typeList.size()) && (pos < i + windowSize)) {
                        tempType = ((Integer) typeList.elementAt(pos)).intValue();
                        tempIndex = ((Integer) indexList.elementAt(pos++)).intValue();
                        if ((tempType == 4) && (text.charAt(tempIndex - 1) == '\'')) {
                            lineList.addElement(new Integer(tempIndex));
                            i = pos - 1;
                            break;
                        }
                    }
                }
                //Double quote
                else if ((curType == 4) && (text.charAt(curIndex - 1) == '\"')) {
                    int pos = i + 1;
                    while ((pos < typeList.size()) && (pos < i + windowSize)) {
                        tempType = ((Integer) typeList.elementAt(pos)).intValue();
                        tempIndex = ((Integer) indexList.elementAt(pos++)).intValue();
                        if ((tempType == 4) && (text.charAt(tempIndex - 1) == '\"')) {
                            lineList.addElement(new Integer(tempIndex));
                            i = pos - 1;
                            break;
                        }
                    }
                } else
                    lineList.addElement(new Integer(curIndex));
            } else {
                nextType = ((Integer) typeList.elementAt(i + 1)).intValue();
                nextIndex = ((Integer) indexList.elementAt(i + 1)).intValue();
                if ((nextType == 3) ||
                        ((nextType == 4) && ((text.charAt(nextIndex - 1) == ' ') || (text.charAt(nextIndex - 1) == '\"') ||
                                (text.charAt(nextIndex - 1) == '(') || (text.charAt(nextIndex - 1) == '\''))))
                    lineList.addElement(new Integer(((Integer) indexList.elementAt(i)).intValue()));
                else if ((curType == 1) && (nextType != 0) && (nextType != 4))
                    lineList.addElement(new Integer(((Integer) indexList.elementAt(i)).intValue()));
            }
        }
        if (i < typeList.size())
            lineList.addElement(new Integer(((Integer) indexList.elementAt(indexList.size() - 1)).intValue()));
        iter = lineList.iterator();
    } //lineInstance

    public void genOutput(String line, String outFileName) throws IOException {
        String ProcessedText = "";
        LongLexTo tokenizer = new LongLexTo(new File("lexitron.txt"));
        File unknownFile = new File("unknown.txt");
        if (unknownFile.exists())
            tokenizer.addDict(unknownFile);
        Vector typeList;
        String text = "";
        char ch;
        int begin, end, type;

        File inFile, outFile;
        FileReader fr;
        BufferedReader br;
        FileWriter fw;

        BufferedReader streamReader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\n\n*******************************");
        System.out.println("*** LexTo: Lexeme Tokenizer ***");
        System.out.println("*******************************");
//  System.out.println(System.getProperty("user.dir"));
//    do {
//    	System.out.print("\n >>> Enter input file ('q' to quit): ");
//      //inFileName=(streamReader.readLine()).trim();
//      if(inFileName.equals("q"))
//        System.exit(1);
//       System.out.println(System.getProperty("user.dir"));
//      inFile=new File(System.getProperty("user.dir") + "//" + inFileName);
//    } while(!inFile.exists());

        //Get output file name
        System.out.print(" >>> Enter output file (.html only): ");

//    outFileName=(streamReader.readLine()).trim();
        outFile = new File(System.getProperty("user.dir") + "//" + outFileName);

        //  fr=new FileReader(inFile);
        //   br=new BufferedReader(fr);
        fw = new FileWriter(outFile);

//    while((line=br.readLine())!=null) {
        // line=line.trim();
        if (line.length() > 0) {

            fw.write("<b>Text:</b> " + line);
            fw.write("<br>\n");

            fw.write("<b>Word instance:</b> : LINE  ");

            tokenizer.wordInstance(line);
            typeList = tokenizer.getTypeList();
            begin = tokenizer.first();
            int i = 0;
            while (tokenizer.hasNext()) {
                end = tokenizer.next();
                type = ((Integer) typeList.elementAt(i++)).intValue();
                if (type == 0) {
                    fw.write("<font color=#ff0000>" + line.substring(begin, end) + "</font>");
                    ProcessedText += "<font color=#ff0000>" + line.substring(begin, end) + "</font>";
                } else if (type == 1) {
                    fw.write("<font color=#00bb00>" + line.substring(begin, end) + "</font>");
                    ProcessedText += "<font color=#0000bb>" + line.substring(begin, end) + "</font>";
                } else if (type == 2) {
                    fw.write("<font color=#0000bb>" + line.substring(begin, end) + "</font>");
                    ProcessedText += "<font color=#0000bb>" + line.substring(begin, end) + "</font>";
                } else if (type == 3) {
                    fw.write("<font color=#aa00aa>" + line.substring(begin, end) + "</font>");
                    ProcessedText += "<font color=#aa00aa>" + line.substring(begin, end) + "</font>";
                } else if (type == 4) {
                    fw.write("<font color=#00aaaa>" + line.substring(begin, end) + "</font>");
                    ProcessedText += "<font color=#00aaaa>" + line.substring(begin, end) + "</font>";
                }
                fw.write("<font color=#000000>|</font>");
                ProcessedText += "<font color=#000000>" + line.substring(begin, end) + "</font>";
                begin = end;
            }
            fw.write("<br>\n");
            ProcessedText += "<br>\n";
            fw.write("<b>Line instance:</b> ");
            tokenizer.lineInstance(line);
            begin = tokenizer.first();
            while (tokenizer.hasNext()) {
                end = tokenizer.next();
                fw.write(line.substring(begin, end) + "<font color=#ff0000>|</font>");
                begin = end;
            }
            fw.write("<br><br>\n");
        }
        fw.write("<hr>");
        fw.write("<font color=#ff0000>unknown</font> | ");
        fw.write("<font color=#00bb00>known</font> | ");
        fw.write("<font color=#0000bb>ambiguous</font> | ");
        fw.write("<font color=#a00aa>English/Digits</font> | ");
        fw.write("<font color=#00aaaa>special</font>\n");
        //   fr.close();
        fw.close();
        System.out.println("\n *** Status: Use Web browser to view result: " + outFileName);
        System.out.println(ProcessedText);
    } //main
    /****************************************************************/
    /*************************** Demo *******************************/
    /****************************************************************/
//public static void main(String[] args) throws IOException {
//
//  LongLexTo tokenizer=new LongLexTo(new File("lexitron.txt"));
//  tokenizer.genOutput("news.txt","nat.html");
//} //main
}

