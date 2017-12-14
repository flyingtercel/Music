package us.mifeng.music;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.FileDescriptor;
import java.io.IOException;

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
