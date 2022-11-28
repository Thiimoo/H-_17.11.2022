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

    Game g;
    Map<String, String> settings = new HashMap<>();

    ListView listView;
    ArrayAdapter<String> adapter;
    List<String> list = new ArrayList<>();
    String pattern;

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
        g = new Game(pattern, adapter,settings,this);
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

    public void submitbutton(View view)
    {
        String temp = g.submitbutton(view);
        if (temp.equals("toast"))
        {
            Toast.makeText(this, "Enter valid guess", Toast.LENGTH_SHORT).show();
        }
    }


    public void settingsbutton(View view)
    {
        g.settingsbutton(view);
    }

    public void highScoresButton(View view)
    {
        g.highScoresButton(view);
    }

    private InputStream getInputStreamForAsset(String filename)
    {
        AssetManager assets = getAssets();
        try {
            return assets.open(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}