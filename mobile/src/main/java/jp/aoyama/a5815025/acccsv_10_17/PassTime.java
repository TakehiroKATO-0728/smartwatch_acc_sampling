package jp.aoyama.a5815025.acccsv_10_17;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takehiro on 2018/10/18.
 * とりあえず保留
 */

public class PassTime {

    long startTime = 0;
    long endTime = System.currentTimeMillis();
    int realTime;

    public PassTime(){
    }

    public long printTime(){
        /*
        Date date = new Date();

        int moreTime = 59;
        int count = 0;

        //==== 表示形式を設定 ====//
        SimpleDateFormat sdf = new SimpleDateFormat("ss");

        endTime = Integer.parseInt(sdf.format(date));
        startTime = endTime;

        if(endTime >= 59){
            moreTime += count;
            count++;
            Thread.sleep(1000);
        }
        else
            return realtime;

        */
        if(startTime == 0)
            startTime = endTime;

        return (endTime - startTime);
    }


}
