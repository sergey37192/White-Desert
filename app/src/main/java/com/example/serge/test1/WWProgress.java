package com.example.serge.test1;

import android.content.Context;

import com.example.serge.test1.CustomEvents.Event;
import com.example.serge.test1.CustomEvents.RandomEvent;
import com.example.serge.test1.CustomEvents.Stage;
import com.example.serge.test1.CustomEvents.StageJump;
import com.example.serge.test1.CustomEvents.Waiting;
import com.example.serge.test1.Person.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Created by sergey37192 on 28.01.2018.
 */

public class WWProgress {

    private static Progress progress = null;

    public static void loadProgress(Context context) {
        File file = new File(context.getFilesDir(), "save.dat");
        ObjectInputStream obj = null;
        if(file.exists())
        try {
            obj = new ObjectInputStream( new FileInputStream( file ) );
            progress = (Progress) obj.readObject();
            obj.close();
        } catch (FileNotFoundException ignored) {

        } catch (ClassNotFoundException ignored) {

        } catch (IOException ingored) {

        }
        if(progress == null)
            progress = new Progress();


    }
    public static void saveProgress(Context context){

        try {
            if(progress!=null){
                FileOutputStream fout = context.openFileOutput( "save.dat", Context.MODE_PRIVATE );
                ObjectOutputStream objout = new ObjectOutputStream( fout );
                objout.writeObject(progress);
                objout.close();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<Event> addToProgress(String stage_name) throws NoSuchElementException{

        Stage stage = (Stage) Scenario.scenarioList.get( stage_name );

        if(stage == null)
            throw new NoSuchElementException();

        ArrayList<Event> newEventList = null;
        try {
            ArrayList<Event> EventList = stage.getArray();
            newEventList = new ArrayList<>( );
            for(Event e : EventList){
                Event currE = (Event) e.clone();
                currE.setStage( stage_name );

                if(currE instanceof RandomEvent){
                    RandomEvent re = (RandomEvent) currE;
                    if(!re.check()){
                        planningScheduleTime( re );
                        addToProgress( re );
                        newEventList.add( re );
                        break;
                    }else continue;
                }else if(currE instanceof StageJump){
                    planningScheduleTime( currE );
                    addToProgress( currE );
                    newEventList.add(currE);
                    break;
                }
                planningScheduleTime( currE );
                addToProgress( currE );
                newEventList.add( currE );
            }
//                getProgressList().addAll( newEventList );
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        CustomTimer.clearTimer();
        return newEventList;

    }



    public static void addToProgress(Event customEvent){
        getProgressList().add( customEvent );
    }

    public static void planningScheduleTime(Event item){
        item.setScheduledtime( CustomTimer.getValue() );
        if(item instanceof Waiting){
            Waiting waiting = (Waiting) item;
            CustomTimer.addTestTime( waiting.getValue() );
        }
    }

    public static ArrayList<Event> getProgressList(){
        return progress.getProgressList();
    }

    public static Person getPerson(){
        return progress.getPerson();
    }

    public static void dump_of_progress(){
        progress = new Progress();
    }

    public static void setItem(int itemId){
         getPerson().setItem( itemId );
    }

    public static void unsetItem(int itemId){
        getPerson().unsetItem( itemId );
    }

    public static boolean checkItem(int itemId){
        return getPerson().checkItem( itemId );
    }

    public static void backInTime(Event customEvents){
        ArrayList<Event> arrayList = WWProgress.getProgressList();
        int end = arrayList.size();
        int start = 0;
        for(Event customEvents1 : arrayList){

            if(customEvents == customEvents1)
                break;
            start++;
        }
        int count = end - start;
        while(count>0){
            arrayList.remove( start );
            count--;
        }


    }

    public static Event getEventById(String id, Class<?> specialClass){
        Stage stage = (Stage) Scenario.scenarioList.get( id );
        if(stage == null || id == null)
            return null;

        ArrayList<Event> events = stage.getArray();
        try{
            for(Event event : events){
                if(event.getClass() == specialClass){
                    Event clone = (Event) event.clone();
                    clone.setStage( id );
                    return clone;
                }
            }
        }catch (CloneNotSupportedException ex){
            //TODO
        }
        return null;
    }

    public static Event getLastEvent(Class<?> specialClass){
        ArrayList<Event> arrayList = getProgressList();
        Event customEvents = null;
        for(Event customEvents1 : arrayList){
            if(customEvents1.getClass() == specialClass)
                customEvents = customEvents1;
        }
        return customEvents;
    }


}
