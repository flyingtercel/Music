package us.mifeng.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button start;
    private Button stop;
    private Intent intent;
    private String[]musics = {"ainy.mp3","beiwei.mp3","woxiang.mp3","yuanfeng.mp3"};
    public static String  ACTION_MAIN = "us.mifeng.action.music";
    private TextView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //创建启动服务的intent对象
        intent = new Intent(this,MusicService.class);
        //注册广播。
        IntentFilter filter = new IntentFilter(ACTION_MAIN);
        registerReceiver(new MusicReceiver(),filter);
    }

    private void initView() {
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        tView = (TextView) findViewById(R.id.title);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                //1,代表播放
                intent.putExtra("type",1);
                break;
            case R.id.stop:
                //2,代表停止
                intent.putExtra("type",2);
                break;
        }
        startService(intent);
    }
    class MusicReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取状态数据，并设置相应数据
            int num = intent.getIntExtra("num",-1);
            int state = intent.getIntExtra("state",-1);
            if (num>-1){
                tView.setText(musics[num]);
            }
            if (state ==0x111 || state == 0x113){
                start.setText("播放");
            }else{
                start.setText("暂停");
            }
        }
    }
}
