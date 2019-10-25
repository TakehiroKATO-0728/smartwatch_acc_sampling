package jp.aoyama.a5815025.acccsv_10_17;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import static android.content.ContentValues.TAG;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                    SensorEventListener {

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient = null;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        //センサーマネージャーを取得
        SensorManager sma = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //センサマネージャに TYPE_ACCELEROMETER(加速度センサ) とサンプリング周波数を指定
        sma.registerListener(this,sma.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Enables Always-on
        setAmbientEnabled();
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        mTextView = (TextView) findViewById(R.id.text);
        double x,y,z;
        //mTextView.setText("test");
        if(count>= 10) {
            count = 0;
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            //mTextView.setText("x="+x+"\ny="+y+"\nz="+z);
            //mTextView.setText(String.format("X : %f\nY : %f\nZ : %f" , x, y, z));
            String SEND_DATA = x + "," + y + "," + z;
            mTextView.setText(SEND_DATA);
            sendDataByDataApi(SEND_DATA);
        }else count++;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){

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
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    /**
     * テキストを送信します。
     *
     * @param text テキスト
     */

    private void sendDataByDataApi(String text){
        PutDataMapRequest putDataMapReq =PutDataMapRequest.create("/data_comm");
        putDataMapReq.getDataMap().putString("key_data", text);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient,putDataReq);
    }
}
