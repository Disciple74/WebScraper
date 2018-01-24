import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.util.ArrayList;

import static java.lang.Character.*;

public class HTMLParser extends HTMLEditorKit.ParserCallback{
    private StringBuffer stringBuffer;
    final private String[] regex = {"\\[\\d*]", "\\[edit]", "\\.\"", "\\^"};
    final private String[] toReplace = {" ", ". ", "\\.\"", "\\.\"", " "};


    HTMLParser(){
    }

    public void parse(String html) throws IOException {
        FileReader in = makeReader(html);
        stringBuffer = new StringBuffer();
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(in, this, Boolean.TRUE);
        in.close();
    }

    private FileReader makeReader(String html) throws IOException {
        File file = File.createTempFile("temp", ".txt");
        file.deleteOnExit();
        FileWriter fw = new FileWriter(file, false);
        fw.write(html);
        fw.flush();
        fw.close();
        return new FileReader(file);
    }

    public void handleText(char[] text, int pos){
        stringBuffer.append(text);
    }

    public String getText(){//Здесь будет очень костыльное написание превращения строки в относительно удобочитаемую. В количестве символов не теряем.
        StringBuilder buf = new StringBuilder(stringBuffer.toString());
        for (int i = 0; i < regex.length; i++)
            buf = new StringBuilder(buf.toString().replaceAll(regex[i], toReplace[i]));
        char[] bufChar = buf.toString().toCharArray();
        ArrayList<Character> chars = new ArrayList<>(bufChar.length);
        for (int i = 0; i < bufChar.length - 2; i++) {
            if ((i + 2) < bufChar.length - 1 && bufChar[i] == '.'&&bufChar[i+1] == '.'&&bufChar[i+2] == '.' ){
                i+=2;
                for (int j = 0; j < 3; j++)
                    chars.add('.');
            } else
            if ((i + 2) < bufChar.length - 1 &&
                    (bufChar[i] == '.' && (!isDigit(bufChar[i + 1]) && bufChar[i + 1] != ')'))
                    ||
                    (isUpperCase(bufChar[i]) && isUpperCase(bufChar[i+1]) && isLowerCase(bufChar[i+2]))
                    ||
                    (isDigit(bufChar[i]) && isLetter(bufChar[i+1]))
                    ||
                    ((bufChar[i] == ')'||(bufChar[i] == ':' && !isDigit(bufChar[i])) || bufChar[i] == '!' || bufChar[i] == '?')
                            && bufChar[i+1] != '"')) {
                chars.add(bufChar[i]);
                chars.add(' ');
            }else
            if ((i + 2) < bufChar.length - 1 && isUpperCase(bufChar[i+1]) && isLowerCase(bufChar[i]) &&
                    (bufChar[i]!= 'i'&&bufChar[i+1]!= 'O'&&bufChar[i+2]!= 'S')) {
                chars.add(bufChar[i]);
                chars.add('.');
                chars.add(' ');
            }else
                chars.add(bufChar[i]);
        }
        buf.delete(0, buf.length());
        for (int i = 0; i < chars.size(); i++){
            if ((chars.get(i) == ' ' && chars.get(i+1) == ' ')
                    ||(chars.get(i) == ')' && ((chars.get(i+2) == ':')||(chars.get(i+2) == ','))))
                chars.remove(i+1);
            buf.append(chars.get(i));
        }
        return buf.toString();
    }

}
