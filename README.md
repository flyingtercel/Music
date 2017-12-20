# Music
Studing Service And BroadCastReciver
学习服务和广播，制作一个简单的音乐播放器，能够实现音乐播放器的开始播放，暂停播放与停止播放功能
![播放前](https://github.com/flyingtercel/Music/blob/master/app/src/main/res/mipmap-hdpi/ss.png)
![播放时](https://github.com/flyingtercel/Music/blob/master/app/src/main/res/mipmap-hdpi/zz.png)

MainActivity中代码如下
```
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
    ```
    ```
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
```
Service代码如下：
```
public class MusicService extends Service {
    private String[]musics = {"ainy.mp3","beiwei.mp3","woxiang.mp3","yuanfeng.mp3"};
    private int state = 0x111;//代表停止
    private AssetManager manager;
    private MediaPlayer player;
    private int num = 0;
    public MusicService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        //获取Assets对象
        manager = getAssets();
        //创建音乐播放器对象
        player = new MediaPlayer();
        //音乐播放完成时候的监听事件
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //改变当前的音乐值
                if (num<musics.length-1){
                    num++;
                }else{
                    num = 0;
                }
                //播放成后应该自动播放，因此再次调用该方法
                startMusic(musics[num]);
            }
        });
        //播放完成，要通知Activity进行更改数据
        Intent intent = new Intent(MainActivity.ACTION_MAIN);
        intent.putExtra("num",num);
        intent.putExtra("state",state);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int type = intent.getIntExtra("type",0);
        switch (type){
            case 1:
                if (state == 0x111){//停止的状态
                    startMusic(musics[num]);
                    state = 0x112;//正在播放
                }else if (state == 0x112){
                    player.pause();//暂停播放
                    state = 0x113;//处于暂停的状态
                }else if (state == 0x113){
                    player.start();//开始播放
                    state = 0x113;
                }
                break;
            case 2:
                if (state == 0x112 || state == 0x113){
                    player.stop();//停止播放
                    state = 0x111;
                }
                break;
        }
        //向Activity的广播中发送消息
        Intent resultIntent = new Intent(MainActivity.ACTION_MAIN);
        //将发送的状态和音乐名字传过去
        resultIntent.putExtra("num",num);
        resultIntent.putExtra("state",state);
        sendBroadcast(resultIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startMusic(String name) {
        try {
            //获取资产文件的描述对象
            AssetFileDescriptor afd = manager.openFd(name);
            //获取文件
            FileDescriptor fd = afd.getFileDescriptor();
            //重置音乐播放器
            player.reset();
            //设置播放源
            player.setDataSource(fd,afd.getStartOffset(),afd.getLength());
            //准备播放
            player.prepare();
            //开始播放
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
```
在清单文件中对Service进行注册
```
<service
            android:name=".MusicService"
            android:enabled="true"
            android:exported="true"></service>
```
