package com.amadeus.audiobook;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.amadeus.audiobook.MainActivity.file;
import static com.amadeus.audiobook.MainActivity.file1;


public class Next_page extends AppCompatActivity {
    List_view object;
    static TextToSpeech tts = null;
    static Integer pageNumber = 0;
    Button start;
    SeekBar speed;
    SeekBar pitch;
    Spinner spiner;
    CharSequence parsedText;
    static Boolean ref;
    static Boolean flag ;
    PDDocument doc;

    @TargetApi(Build.VERSION_CODES.DONUT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_page);
        start = (Button) findViewById(R.id.start);
        speed = (SeekBar) findViewById(R.id.seekBar);
        pitch = (SeekBar) findViewById(R.id.seekBar3);
        spiner = (Spinner) findViewById(R.id.spinner);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                tts.setLanguage(Locale.US);
                System.out.println("\n TTS Created ");}
                else{
                    Toast.makeText(getApplicationContext(),"Something went wrong...",Toast.LENGTH_LONG).show();
                }
            }
        });

        tts.speak("HELLO FROM THE OTHER SIDE",0,null);

        final String[] Lang_list ={" ","UK","US","CANADA_FRENCH","JAPANESE","GERMAN","FRENCH","CANADA","ENGLISH","KOREAN","CHINESE","ITALIAN"};
        Arrays.sort(Lang_list);
        Lang_list[0] = "Select Language";
        System.out.println(Lang_list.toString());

        ArrayAdapter<String > adapter = new ArrayAdapter<String>(spiner.getContext(),android.R.layout.simple_spinner_dropdown_item,Lang_list);
        spiner.setAdapter(adapter);
        spiner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 1 :
                        tts.setLanguage(Locale.CANADA);
                        break;
                    case 2 :
                        tts.setLanguage(Locale.CANADA_FRENCH);
                        break;
                    case 3:
                        tts.setLanguage(Locale.CHINESE);
                        break;
                    case 4:
                        tts.setLanguage(Locale.ENGLISH);
                        break;
                    case 5:
                        tts.setLanguage(Locale.FRANCE);
                        break;
                    case 6:
                        tts.setLanguage(Locale.GERMAN);
                        break;
                    case 7:
                        tts.setLanguage(Locale.ITALIAN);
                        break;
                    case 8:
                        tts.setLanguage(Locale.JAPANESE);
                        break;
                    case 9:
                        tts.setLanguage(Locale.KOREAN);
                        break;
                    case 10:
                        tts.setLanguage(Locale.UK);
                        break;
                    case 11:
                        tts.setLanguage(Locale.US);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                tts.setLanguage(Locale.getDefault());
                System.out.println("TTS IS CREATED");
            }
        });



        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float d = 4;
                float rate = speed.getProgress()/d;
                tts.setSpeechRate(rate);

            }
        });

        pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float s = 4;
                float p = pitch.getProgress()/s;
                tts.setPitch(p);

            }
        });
        init();
    }


    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView) findViewById(R.id.ListView);
                ArrayList<File> Item = new ArrayList<File>();
                Item.add(file);
                object = new List_view(getApplicationContext(), Item);
                list.setAdapter(object);


                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), PDF_view.class);
                        startActivity(intent);
                    }
                });

            }
        }).run();

    }

    public void onstart(View view) throws IOException {


        flag = true;

        Toast.makeText(getApplicationContext(),"Playing.. Please wait.",Toast.LENGTH_LONG).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    speak();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).run();






    }
    public void speak() throws IOException {
        PDFBoxResourceLoader.init(getApplicationContext());
        InputStream inputStream = getContentResolver().openInputStream(file1);
        final PDDocument doc = PDDocument.load(inputStream);
        PDFTextStripper pdfstripper = new PDFTextStripper();
        if(flag){
            pdfstripper.setStartPage(pageNumber+1);
            pdfstripper.setEndPage(pageNumber+1);
            try {
                parsedText = pdfstripper.getText(doc);
                tts.speak(parsedText, TextToSpeech.QUEUE_ADD, null,"1");
                tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {

                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if(pageNumber < doc.getNumberOfPages()){
                            switch(utteranceId){

                                case "1":

                                    pageNumber++;


                                    try {
                                        speak();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        }

                    };


                    @Override
                    public void onError(String utteranceId) {

                    }
                });
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        tts.shutdown();
        if(doc!=null){
            try {
                doc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onstop(View view){
        if(tts.isSpeaking()){
        flag = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                tts.stop();
            }
        }).run();
        pageNumber--;}

        }

}





