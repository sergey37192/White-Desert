package com.example.serge.test1;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.serge.test1.Objects.CustomButton;
import com.example.serge.test1.Objects.CustomEvents;
import com.example.serge.test1.Objects.Question;
import com.example.serge.test1.Objects.Questions;
import com.example.serge.test1.Objects.TextMessage;
import com.example.serge.test1.Objects.Waiting;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;


public class MainActivity extends AppCompatActivity {

    LinearLayout mainLayout;
    ScrollView mainScrollView;
    String stage;
    private Handler handler = new Handler( Looper.getMainLooper() );


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testscroll);
        mainLayout = (LinearLayout) findViewById(R.id.mainlayout);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        try {
            Scenario.loadSceanrio( this );
            gameProcessed();
        }catch (XmlPullParserException | IOException e){
            e.printStackTrace();
        }

    }
    protected void gameProcessed(){
        //getStringForGame();
        getCurrentEpisode();
    }
    //получение нужной стадии сценария и добавление в прогресс
    protected void getCurrentEpisode(){
        try {
            if (stage == null)
                stage = new String( "start" );
            Progress.addToProgress( stage );
            Progress.planningScheduleTime();
            testGameProcessed();
        }catch (NoSuchElementException e){

        }
    }
    //Обработка прогресса, отрисовка view
    protected void testGameProcessed(){
        if(Progress.list!=null){
            long currentTime = System.currentTimeMillis();
            //проходим по коллекции прогресса, получая объекты для дальнейшего взаимодействия
            for(CustomEvents e : Progress.list){
                if(!e.getAdded()){
                    long scheduleTime = e.getScheduledtime() - currentTime;
                    if(e.getClass() == TextMessage.class){
                        final TextMessage textMessage = (TextMessage) e;
                        final TextView textView = new TextView( this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(15,50,0,50);
                        textView.setLayoutParams(layoutParams);
                        textView.setText(textMessage.getText());
                        if(scheduleTime>=0)
                            handler.postDelayed( new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.addView(textView);
                                    textMessage.setAdded(true);
                                }
                            }, scheduleTime );
                        else mainLayout.addView(textView);
                    }else if(e.getClass() == Waiting.class){
                        final Waiting waiting = (Waiting) e;
                        final TextView waitView = new TextView( this );
                        waitView.setText( "Джозеф занят" );
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        waitView.setLayoutParams(layoutParams);
                        waitView.setGravity(Gravity.CENTER_HORIZONTAL);
                        if(scheduleTime>0){
                            handler.postDelayed( new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.addView( waitView );
                                    waiting.setAdded( true );
                                }
                            }, scheduleTime );

                        }else mainLayout.addView( waitView );
                        CustomTimer.addTestTime(waiting.getValue());
                    }else if(e.getClass() == Questions.class){
                        final Questions questions = (Questions) e;
                        final ScrollView scrollView = new ScrollView( this );
                        scrollView.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
                        scrollView.setLayoutParams( Settings.layoutParams );
                        LinearLayout linearLayout = new LinearLayout( this );
                        linearLayout.setLayoutParams( Settings.layoutParams );
                        scrollView.addView( linearLayout );
                        ArrayList<Question> questionArray = questions.getList();
                        for(Question q : questionArray){
                            final CustomButton customButton = new CustomButton(this, q.getGoTo());
                            customButton.setText( q.getText() );
                            customButton.setOnClickListener( new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setStage(customButton.getGoTo());
                                    gameProcessed();
                                }
                            } );
                            linearLayout.addView(customButton);
                        }
                        if(scheduleTime>0){
                            handler.postDelayed( new Runnable() {
                                @Override
                                public void run() {
                                    mainLayout.addView(scrollView);
                                    questions.setAdded(true);
                                }
                            }, scheduleTime );
                        }else mainLayout.addView(scrollView);
                    }
                }
            }
        }
    }//old version
    /*
    protected void getStringForGame(){
        if(stage==null)
          stage = new String("nextstage2");
        Progress.addToProgress(stage);
        Progress.planningScheduleTime();
        int id = this.getResources().getIdentifier(stage, "array", this.getPackageName());
        if(id != 0){
            String[] s = getResources().getStringArray(id);
            addTextView(s);
        }
    }


    public void addTextView(final String s[]){
        final Handler handler = new Handler( Looper.getMainLooper());
        int length = s.length;
        for(int i = 0; i<=length;i++)
        {
            if(i!=length){
                final CustomTextView customTextView = new CustomTextView( this );
                if(s[i].indexOf("/")!=-1){
                    String[] teststr = s[i].split("/",2);
                    s[i] = teststr[0];
                    final TextView waitView = new TextView(this);
                    waitView.setText("Джозеф занят");
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    waitView.setLayoutParams(layoutParams);
                    waitView.setGravity(Gravity.CENTER_HORIZONTAL);
                    handler.postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            mainLayout.addView( waitView );
                        }
                    }, CustomTimer.getValue() );
                    CustomTimer.addTime(teststr[1]);
                }
                customTextView.setText( s[i] );
                handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,50,0,50);
                        customTextView.setLayoutParams( layoutParams );
                        mainLayout.addView(customTextView);
                    }
                }, CustomTimer.getValue());
            }
            else{ //final CreateButton answer = new CreateButton();
                int id = this.getResources().getIdentifier(stage+"_questions", "array", this.getPackageName());
                if(id != 0){
                    String str[] = getResources().getStringArray(id);
                    final LinearLayout answerLayout = CreateButton.crAnswer(this, str[0],str[1]);
                    handler.postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        getMainLayout().addView(answerLayout);
                    }
                }, CustomTimer.getValue() );
                }else{
                        final Button but = CreateButton.getRestartButton(this);
                        handler.postDelayed( new Runnable() {
                            @Override
                            public void run() {
                                getMainLayout().addView(but);
                            }
                        }, CustomTimer.getValue() );
                    }
            }
            CustomTimer.addTime();
        }
        CustomTimer.clearTimer();
    }*/

    public String getStage(){
        return stage;
    }
    public void setStage(String stage){
        this.stage = stage;
    }
    public void firstButtonClick(View view){
        onClickActive(0);
    }
    public void secondButtonClick(View view){
        onClickActive(1);
    }
    public void onClickActive(int n){
        int id = this.getResources().getIdentifier("logic_"+getStage(), "array", this.getPackageName());
        if(id != 0){
            String[] logicStage = this.getResources().getStringArray(id);
           // MyServiceForGameProcess service = new MyServiceForGameProcess();
           // startService(new Intent(this, service.getClass()));
            setStage(logicStage[n]);
            mainLayout.removeViewAt(mainLayout.getChildCount()-1);
            gameProcessed();
        }
    }

    public void restartGame(View view){
        stage = null;
        mainLayout.removeAllViews();
        gameProcessed();
    }

    public ScrollView getMainScrollView(){
        return  mainScrollView;
    }
    public LinearLayout getMainLayout(){
        return mainLayout;
    }
}
