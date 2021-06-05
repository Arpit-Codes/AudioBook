package com.amadeus.audiobook;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import static com.amadeus.audiobook.MainActivity.file1;
import static com.amadeus.audiobook.MainActivity.name;
import static com.amadeus.audiobook.Next_page.pageNumber;
import static com.amadeus.audiobook.Next_page.tts;
import static com.amadeus.audiobook.Next_page.flag;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PDF_view extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener {

    PDFView pdfView;
    String pdfFileName;
    String TAG = "PDFview";
    Button pl_btn ;
    private String parsedText;
    Handler handler = new Handler();
    Runnable refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pl_btn = (Button) findViewById(R.id.PB);
        setContentView(R.layout.activity_pdfview);
        init();
    }

    public void init() {
        pdfView = (PDFView) findViewById(R.id.pdfView);
        refresh = new Runnable() {
            @Override
            public void run() {
                displayFromSdcard();
            }
        };
        refresh.run();

    }

    private void displayFromSdcard() {
        pdfFileName = name;
        System.out.println(pdfFileName);

        pdfView.fromUri(file1)
                .defaultPage(pageNumber)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page+1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");
    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    public void clk(View view) throws IOException {

        flag = true;
        Toast.makeText(getApplicationContext(),"Playing.. Please wait.",Toast.LENGTH_SHORT).show();

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
    @TargetApi(Build.VERSION_CODES.DONUT)
    public void clk2(View view){
        if(tts.isSpeaking()){

        flag = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                tts.stop();
            }
        }).run();
        pageNumber -= 1;}

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
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
}
