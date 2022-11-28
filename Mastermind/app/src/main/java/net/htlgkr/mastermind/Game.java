package net.htlgkr.mastermind;

import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Game {

    private String currentguess;
    ArrayAdapter<String> adapter;
    private List<String> templist;
    private String pattern = "";
    private int userguess = 0;
    Map<String, String> settings = new HashMap<>();
    MainActivity m;

    public Game(String pattern, ArrayAdapter<String> adapter, Map<String, String> settings, MainActivity m)
    {
        this.adapter = adapter;
        this.templist = new ArrayList<>();
        this.pattern = pattern;
        this.userguess = 0;
        this.settings = settings;
        this.m = m;
    }

    public void onLoad(View view, File file)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            NodeList list = document.getElementsByTagName("saveState");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element e = (Element) node;
                    String guess = e.getAttribute("guess"+i);
                    System.out.println(guess);
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public void onSave(View view, File file)
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("saveState");
        doc.appendChild(rootElement);
        Element code = doc.createElement("code");
        rootElement.appendChild(code);
        Element guess;
        Element userinput;
        String[] parts;
        for (int i = 0; i < m.list.size(); i++) {
            parts = m.list.get(i).split("/");
            guess = doc.createElement("guess"+i);
            rootElement.appendChild(guess);
            userinput = doc.createElement("userInput");
            userinput.setTextContent(parts[0]);
            guess.appendChild(userinput);
            userinput = doc.createElement("result");
            userinput.setTextContent(parts[1]);
            guess.appendChild(userinput);
        }
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            System.out.println("write data success to file"+ file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void writeHighScore() throws IOException, SAXException {
        File f = new File("data/data/net.htlgkr.mastermind/files/highscores.xml");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        //TODO: schau ob des file 0 is sonst des sonst docBuilder.newDocument;
        Document doc = docBuilder.parse(f);
        Element rootElement = doc.createElement("highScore");
        rootElement.setTextContent(String.valueOf(userguess));
        doc.appendChild(rootElement);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(f,true);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
            System.out.println("write data success to file"+ f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public void settingsbutton(View view){

        //TODO: es is des pattern in da listn nd nua da guess
        if (!m.list.isEmpty()&&!m.list.contains("correctPositionSign +")&&!m.list.contains("High-Scores: ")&&!m.list.isEmpty()&&!m.list.contains(currentguess)) {
            savetotemp();
            System.out.println(m.list.get(0));
        }

        if(m.list.contains("correctPositionSign +")||m.list.contains("correctCodeElementSign −")||m.list.contains("codeLength 4")||m.list.contains("doubleallowed true"))
        {
            m.list.clear();
            getfromtemp();
            adapter.notifyDataSetChanged();
            return;
        }
        m.list.clear();
        for(Map.Entry<String, String> m : settings.entrySet()){
            this.m.list.add(m.getKey() + " " + m.getValue());
        }
        adapter.notifyDataSetChanged();
    }

    public String checkPattern(String userInput) {
        String dd = "";
        if (pattern.equals(userInput)) {
            return "w";
        } else {

            StringBuilder patternBuilder = new StringBuilder(pattern);
            StringBuilder userPatternBuilder = new StringBuilder(userInput);

            for (int i = 0; i < patternBuilder.length(); i++) {
                if (patternBuilder.charAt(i) == userPatternBuilder.charAt(i)) {
                    dd += settings.get("correctPositionSign");
                    patternBuilder.deleteCharAt(i);
                    userPatternBuilder.deleteCharAt(i);
                    i--;
                }
            }
            for (int i = 0; i < patternBuilder.length(); i++) {
                for (int j = 0; j < userPatternBuilder.length(); j++) {
                    if (patternBuilder.charAt(i) == userPatternBuilder.charAt(j)) {
                        dd += settings.get("correctCodeElementSign");
                        patternBuilder.deleteCharAt(i);
                        userPatternBuilder.deleteCharAt(j);
                        i--;
                        break;
                    }
                }
            }
        }
        return userInput + " / " + dd;
    }

    public String submitbutton(View view, EditText guess){
        Button submit = view.findViewById(R.id.submit);

        if(userguess == Integer.parseInt(settings.get("guessRounds"))){
            m.list.clear();
            m.list.add("Verloren: " + pattern);
            submit.setVisibility(View.GONE);
            return "verloren";
        }
        userguess++;
        System.out.println(pattern);
        this.currentguess = guess.getText().toString();
        if(guess.getText().toString().length() != 4 || isStringinside(guess.getText().toString())){
            return "toast";
        }
        String l = checkPattern(guess.getText().toString().toUpperCase());
        if(l.equals("w")){
            m.list.clear();
            Button b = view.findViewById(R.id.submit);
            b.setEnabled(false);
            m.list.add("won! Pattern: " + pattern+" in "+userguess+" guesses");
            adapter.notifyDataSetChanged();
            try {
                writeHighScore();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return "win";
        }
        m.list.add(l);
        adapter.notifyDataSetChanged();
        guess.setText("");
        return "casual";
    }

    private void savetotemp()
    {
        if (m.list.size()>0) {
            for (int i = 0; i < m.list.size(); i++) {
                templist.add(i, m.list.get(i));
            }
        }
    }

    private void getfromtemp()
    {
        for (int i = 0; i < templist.size(); i++) {
            m.list.add(i, templist.get(i));
        }
    }


    private boolean isStringinside(String check)
    {
        for(char c : check.toCharArray()) {
            if(!Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public void highScoresButton(View view)
    {
        if (m.list.contains("High-Scores: ")||m.list.contains("correctPositionSign +"))
        {
            m.list.clear();
            getfromtemp();
            adapter.notifyDataSetChanged();
            return;
        }
        if (!m.list.contains("correctPositionSign +")&&!m.list.contains("High-Scores: ")&&!m.list.isEmpty()&&!m.list.contains(currentguess))
        {
            savetotemp();
        }
        m.list.clear();
        m.list.add("High-Scores: ");
        adapter.notifyDataSetChanged();
        String path = m.getFilesDir().getAbsolutePath()+"/data/data/Mastermind";
        System.out.println(path);
        File f = new File(path);
        try {
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                System.out.println(s.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            f.getAbsolutePath();
        }
    }
}
