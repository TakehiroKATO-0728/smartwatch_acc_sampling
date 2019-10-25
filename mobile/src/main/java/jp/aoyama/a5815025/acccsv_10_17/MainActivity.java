package jp.aoyama.a5815025.acccsv_10_17;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;
/**
 * Created by takehiro on 2018/10/17.
 * 加藤岳大制作，wearの加速度をmobileに送信するアプリ
 * 2018/10/17.改良：wearの加速度をmobileに送信し、csvファイルを出力するアプリ
 * 2018/11/21.改良：計測を開始してからの経過時間(ms)を表示し、csvファイルにも出力する
 */

public class MainActivity extends Activity implements DataApi.DataListener,
                                                        GoogleApiClient.ConnectionCallbacks,
                                                            GoogleApiClient.OnConnectionFailedListener{

    private GoogleApiClient mGoogleApiClient = null;
    TextView textView;
    OutputCsv outputCsv = new OutputCsv();
    int flag = 0;
    private long startTime;
    private long progress_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.text) ;
        textView.setText("STARTを押して計測開始");

        //GoogleApiインスタンス生成
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
            Wearable.DataApi.removeListener(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
        Wearable.DataApi.addListener(mGoogleApiClient,this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final Button bt = findViewById(R.id.button);
        final Button bt2 = findViewById(R.id.button2);

        for(DataEvent event : dataEvents){
            if(event.getType() == DataEvent.TYPE_CHANGED){
                DataItem item = event.getDataItem();
                if(item.getUri().getPath().equals("/data_comm")){
                    final DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            //textView.setText("STARTを押して計測開始");
                            //textView.setText(Date().getTime());
                            //outputCsv.write(dataMap.getString("key_data"));
                            bt.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //STARTボタンクリック時の処理
                                    //textView.setText("計測中\n"+dataMap.getString("key_data"));
                                    flag = 1;
                                    startTime = System.currentTimeMillis();
                                }
                            });

                            if(flag == 1){
                                progress_time = System.currentTimeMillis() - startTime;
                                outputCsv.write(dataMap.getString("key_data") + "," + String.valueOf(progress_time));
                                textView.setText("計測中\n" + "\n" + dataMap.getString("key_data") + "\n" + "経過時間(ミリ秒):" + String.valueOf(progress_time));
                            }

                            //outputCsv.write(dataMap.getString("key_data"));
                            bt2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //STOPクリック時の処理
                                    //ファイルを閉じて保存
                                    outputCsv.close();
                                    textView.setText("計測終了");
                                    flag = 0;
                                }
                            });
                        }
                    });
                }
            }else if(event.getType() == DataEvent.TYPE_DELETED){
            }
        }

    }
}
