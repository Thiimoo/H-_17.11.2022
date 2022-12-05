package net.htlgkr.mastermind;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
            //dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING,true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            NodeList list = document.getElementsByTagName("userInput");
            NodeList reslustslist = document.getElementsByTagName("result");
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                Node result = reslustslist.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element e = (Element) node;
                    System.out.println(e.getTextContent());
                    Element res = (Element) result;
                    System.out.println(e.getTextContent()+res.getTextContent());
                    this.m.list.add(e.getTextContent()+res.getTextContent());
                }
            }
            NodeList n = document.getElementsByTagName("code");
            Node node = n.item(0);
            Element element = (Element) node;
            pattern = element.getTextContent();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
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
        code.setTextContent(pattern);
        rootElement.appendChild(code);
        Element guess;
        Element userinput;
        String[] parts;
        for (int i = 0; i < this.m.list.size(); i++) {
            parts = this.m.list.get(i).split("\\|");
            guess = doc.createElement("guess"+i);
            rootElement.appendChild(guess);
            userinput = doc.createElement("userInput");
            userinput.setTextContent(parts[0]);
            guess.appendChild(userinput);
            userinput = doc.createElement("result");
            if (parts[1] != null)
            {
                userinput.setTextContent(parts[1]);
            }
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

    public void writeHighScore(File f) throws IOException {
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try
        {
            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            pw.println(userguess);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            pw.close();
            bw.close();
            fw.close();
        }
    }

    public void settingsbutton(View view)
    {
        this.m.list.clear();
        for(Map.Entry<String, String> m : settings.entrySet()){
            this.m.list.add(m.getKey() + " " + m.getValue());
        }
        this.m.list.add("Start New Game");
        adapter.notifyDataSetChanged();
    }

    public String checkPattern(String userInput)
    {
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
        return userInput + " | " + dd;
    }

    public String submitbutton(View view, EditText guess, File file)
    {
        Button submit = view.findViewById(R.id.submit);

        if(userguess == Integer.parseInt(settings.get("guessRounds"))){
            this.m.list.add("Not Solved, Solution: " + pattern);
            submit.setVisibility(View.GONE);
            return "verloren";
        }
        userguess++;
        System.out.println(pattern);
        if(guess.getText().toString().length() != 4 || isStringinside(guess.getText().toString())){
            return "toast";
        }
        String l = checkPattern(guess.getText().toString().toUpperCase());
        if(l.equals("w")){
            Button b = view.findViewById(R.id.submit);
            b.setEnabled(false);
            this.m.list.add("Solved: " + pattern+" in "+userguess+" guesses");
            adapter.notifyDataSetChanged();
            try {
                writeHighScore(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "win";
        }
        this.m.list.add(l);
        adapter.notifyDataSetChanged();
        guess.setText("");
        return "casual";
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
        this.m.list.clear();
        this.m.list.add("High-Scores: ");
        String path = "data/data/net.htlgkr.mastermind/files/score.sc";
        System.out.println(path);
        List<Integer> list = new ArrayList<>();
        File f = new File(path);
        try {
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                list.add(Integer.valueOf(s.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            f.getAbsolutePath();
        }

        Collections.sort(list);
        copyListToAdapterList(list);
        adapter.notifyDataSetChanged();
    }

    public void copyListToAdapterList(List<Integer> list)
    {
        for (int i = 0; i < list.size(); i++) {
            this.m.list.add(String.valueOf(list.get(i)));
        }
        this.m.list.add("New Game");
    }
}
