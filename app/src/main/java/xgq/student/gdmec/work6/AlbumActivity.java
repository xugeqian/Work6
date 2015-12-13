package xgq.student.gdmec.work6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivity extends AppCompatActivity {
    private ViewFlipper flipper;
    private Bitmap[] mBgList;
    private long startTime = 0;
    private SensorManager sm;
    private SensorEventListener sel;

    /**
     * 加载相册
     */
    public String[] loadAlbum(){
        String pathName = android.os.Environment.getExternalStorageDirectory().getPath()+"/com.demo.pr4";
        File file = new File(pathName);
        Vector<Bitmap> fileName = new Vector<Bitmap>();
        if(file.exists()&& file.isDirectory()){
            String[] str = file.list();
            for (String s : str){
                if(new File(pathName + "/" + s).isFile()){
                    fileName.addElement(loadImage(pathName + "/" + s));
                }
            }
            mBgList = fileName.toArray(new Bitmap[]{});
        }
        return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        flipper = (ViewFlipper)this.findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if(mBgList == null){
            Toast.makeText(this,"相册为图片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            for(int i = 0;i<=mBgList.length - 1;i++){
                flipper.addView(addImage(mBgList[i]),i,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));

            }
        }
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[SensorManager.DATA_X];
                if(x>10&&System.currentTimeMillis()>startTime+1000){
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_out));
                    flipper.showPrevious();
                }else if(x<-10&&System.currentTimeMillis()>startTime+1000){
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_out));
                    flipper.showNext();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sm.registerListener(sel,sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(sel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }
    public Bitmap loadImage(String pathName){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(pathName,options);
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        int screenWidth = display.getWidth();
        options.inSampleSize = options.outWidth / screenWidth;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName,options);
        return  bitmap;
    }
    private View addImage(Bitmap bitmap){
        ImageView img = new ImageView(this);
        img.setImageBitmap(bitmap);
        return  img;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
