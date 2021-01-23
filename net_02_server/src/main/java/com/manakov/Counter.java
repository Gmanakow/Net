package com.manakov;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.*;

public class Counter {

    private Timer timer = null;
    private TimerTask timerTask = null;
    private double time;
    private CountData countData;
    private int number;

    public Counter(CountData countData , int number){
        this.number = number;
        this.countData = countData;
        this.timer = new Timer();
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                printData();
            }
        };
    }

    public void start(){
        this.time = 0;
        this.timer.schedule(timerTask, 3000, 3000);
    }

    public void printData(){
        time += 3;
        out.println(number + " current : " + (int) Math.floor(countData.getCurrentByteData() / 3   ) + " Bytes per sec");
        out.println(number + " total : "   + (int) Math.floor(countData.getTotalByteData()   / time) + " Bytes per sec");
    }

    public void stop(){
        this.timer.cancel();
        printData();
    }
}
