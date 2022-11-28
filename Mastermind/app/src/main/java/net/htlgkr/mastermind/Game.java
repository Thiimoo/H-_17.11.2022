package net.htlgkr.mastermind;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Game {

    private String currentguess;
    private List<String> list;
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
        this.m = m;
    }

    public void settingsbutton(View view){

        //TODO: es is des pattern in da listn nd nua da guess
        if (!list.isEmpty()&&!list.contains("correctPositionSign +")&&!list.contains("High-Scores: ")&&!list.isEmpty()&&!list.contains(currentguess)) {
            savetotemp();
            System.out.println(list.get(0));
        }

        if(list.contains("correctPositionSign +")||list.contains("correctCodeElementSign −")||list.contains("codeLength 4")||list.contains("doubleallowed true"))
        {
            list.clear();
            getfromtemp();
            adapter.notifyDataSetChanged();
            return;
        }
        list.clear();
        for(Map.Entry<String, String> m : settings.entrySet()){
            list.add(m.getKey() + " " + m.getValue());
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
        return userInput + " | " + dd;
    }

    public void submitbutton(View view){
        Button submit = view.findViewById(R.id.submit);

        if(userguess == Integer.parseInt(settings.get("guessRounds"))){
            list.clear();
            list.add("Verloren: " + pattern);
            submit.setVisibility(View.GONE);
            return;
        }
        userguess++;
        System.out.println(pattern);
        EditText guess = view.findViewById(R.id.guess);
        this.currentguess = guess.getText().toString();
        if(guess.getText().toString().length() != 4 || isStringinside(guess.getText().toString())){
            //Toast.makeText(this, "Enter valid guess", Toast.LENGTH_SHORT).show();
            return;
        }
        String l = checkPattern(guess.getText().toString().toUpperCase());
        if(l.equals("w")){
            list.clear();
            Button b = view.findViewById(R.id.submit);
            b.setEnabled(false);
            list.add("won! Pattern: " + pattern+" in "+userguess+" guesses");
            adapter.notifyDataSetChanged();
            return;
        }
        list.add(checkPattern(guess.getText().toString().toUpperCase()));
        adapter.notifyDataSetChanged();
        guess.setText("");
    }

    private void savetotemp()
    {
        if (list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                templist.add(i, list.get(i));
            }
        }
    }

    private void getfromtemp()
    {
        for (int i = 0; i < templist.size(); i++) {
            list.add(i, templist.get(i));
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
        if (list.contains("High-Scores: ")||list.contains("correctPositionSign +"))
        {
            list.clear();
            getfromtemp();
            adapter.notifyDataSetChanged();
            return;
        }
        if (!list.contains("correctPositionSign +")&&!list.contains("High-Scores: ")&&!list.isEmpty()&&!list.contains(currentguess))
        {
            savetotemp();
        }
        list.clear();
        list.add("High-Scores: ");
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
