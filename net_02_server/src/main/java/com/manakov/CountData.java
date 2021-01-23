package com.manakov;

import java.util.concurrent.atomic.AtomicLong;

public class CountData {
    private AtomicLong totalByteData;
    private AtomicLong currentByteData;

    public CountData(){
        this.totalByteData = new AtomicLong(0);
        this.currentByteData = new AtomicLong(0);
    }

    public void add(long bytes){
        this.totalByteData.addAndGet(bytes);
        this.currentByteData.addAndGet(bytes);
    }

    public long getCurrentByteData(){
        long res = currentByteData.get();
        currentByteData.set(0);
        return res;
    }

    public long getTotalByteData(){
        long res = totalByteData.get();
        return res;
    }


}
