package net.htlgkr.mastermind;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    String pattern = "";

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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = findViewById(R.id.listview);
                Button b = findViewById(R.id.submit);
                if (position == listView.getAdapter().getCount()-1&&!b.isEnabled())
                {
                    newGame();
                }
                if (position == listView.getAdapter().getCount()-2&&!b.isEnabled()&&listView.)
                {
                    //eif de map settings ändern und in game a set method mocha dass oise gändert wird
                    newGame();
                }
            }
        });
        
    }

    public void onLoad(View view)
    {
        g.onLoad(view,new File("data/data/net.htlgkr.mastermind/files/saved.xml"));
    }

    public void onSave(View view)
    {
        File f = new File("data/data/net.htlgkr.mastermind/files/saved.xml");
        try {
            if (f.createNewFile())
            {
                g.onSave(view, f);
            }
            else
            {
                g.onSave(view,f);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSettings(){
        Scanner sc = new Scanner(getInputStreamForAsset("config.conf"));
        while(sc.hasNextLine()){
            String line = sc.nextLine().replace(" ", "");
            String[] parts = line.split("=");
            settings.put(parts[0], parts[1]);
        }
    }

    public void generatePattern(){
        String[] pat = settings.get("alphabet").split(",");
        for (int i = 0; i < Integer.parseInt(settings.get("codeLength")); i++){
            int random = new Random().nextInt(6);
            if (settings.get("doubleAllowed").equals("true"))
            {
                pattern += pat[random];
            }
            else
            {
               do
                    {
                        random = new Random().nextInt(6);
                    }
               while (pattern.contains(pat[random]));
               pattern += pat[random];
            }
        }
    }

    public void submitbutton(View view)
    {
        File f = new File("data/data/net.htlgkr.mastermind/files/score.sc");
        String temp = "";
        try {
            if (f.createNewFile())
            {
                temp = g.submitbutton(view,findViewById(R.id.guess),f);
            }
            else
            {
                temp = g.submitbutton(view,findViewById(R.id.guess),f);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (temp.equals("toast"))
        {
            Toast.makeText(this, "Enter valid guess", Toast.LENGTH_SHORT).show();
        }
       else if (temp.equals("win"))
        {
            Button b = findViewById(R.id.save);
            b.setEnabled(false);
        }
    }

    public void settingsbutton(View view)
    {
        g.settingsbutton(view);
        Button b = findViewById(R.id.submit);
        b.setEnabled(false);
    }

    public void highScoresButton(View view)
    {

        g.highScoresButton(view);
        Button b = findViewById(R.id.submit);
        b.setEnabled(false);
        EditText t = findViewById(R.id.guess);
        t.setText("");
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


    public void newGame()
    {
        this.list.clear();
        this.pattern = "";
        this.settings = new HashMap<>();
        loadSettings();
        generatePattern();
        this.g = new Game(this.pattern,this.adapter,this.settings,this);
        this.list.clear();
        this.adapter.notifyDataSetChanged();
        Button b = findViewById(R.id.submit);
        b.setEnabled(true);
        EditText t = findViewById(R.id.guess);
        t.setText("");
        b = findViewById(R.id.save);
        b.setEnabled(true);
    }

}
