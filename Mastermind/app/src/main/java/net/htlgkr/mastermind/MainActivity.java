package net.htlgkr.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    Map<String, String> settings = new HashMap<>();
    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> list = new ArrayList<>();
    String pattern = "";
    int userguess = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        loadSettings();
        generatePattern();
        System.out.println(pattern);
    }


    public void loadSettings(){
        Scanner sc = new Scanner(getInputStreamForAsset("config.conf"));
        while(sc.hasNext()){
            String line = sc.nextLine().replace(" ", "");
            String[] parts = line.split("=");
            settings.put(parts[0], parts[1]);

        }
    }

    public void generatePattern(){
        String[] pat = settings.get("alphabet").split(",");
        for (int i = 0; i < Integer.parseInt(settings.get("codeLength")); i++){
            int random = new Random().nextInt(6);
            pattern += pat[random];
        }
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

    public void settingsbutton(View view){
        if (!list.isEmpty()) {
            System.out.println(list.get(0));
        }

        if(list.contains("correctPositionSign +")||list.contains("correctCodeElementSign −")||list.contains("codeLength 4")||list.contains("doubleallowed true"))
        {
            list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        list.clear();
        for(Map.Entry<String, String> m : settings.entrySet()){
            list.add(m.getKey() + " " + m.getValue());
        }
        adapter.notifyDataSetChanged();
    }

    public void highScoresButton(View view)
    {

        list.add("High-Scores: ");
        adapter.notifyDataSetChanged();
        String path = getFilesDir().getAbsolutePath()+"/data/data/Mastermind";
        System.out.println(path);
        File f = new File(path);
        try {
            Scanner s = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            f.getAbsolutePath();
        }
    }

    public void submitbutton(View view){
        Button submit = findViewById(R.id.submit);

        if(userguess == Integer.parseInt(settings.get("guessRounds"))){
            list.add("Verloren: " + pattern);
            submit.setVisibility(View.GONE);
            return;
        }
        userguess++;
        System.out.println(pattern);
        EditText guess = findViewById(R.id.guess);

        if(guess.getText().toString().length() != 4){
            Toast.makeText(this, "Enter 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        String l = checkPattern(guess.getText().toString().toUpperCase());
        if(l.equals("w")){
            list.add("gewonnen: " + pattern);
            adapter.notifyDataSetChanged();
            return;
        }
        list.add(checkPattern(guess.getText().toString().toUpperCase()));
        adapter.notifyDataSetChanged();
        guess.setText("");
    }

    private InputStream getInputStreamForAsset(String filename) {
        AssetManager assets = getAssets();
        try {
            return assets.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}