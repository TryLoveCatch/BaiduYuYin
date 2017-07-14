package io.github.trylovecatch.baiduyuyin;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;

import android.content.Context;
import android.util.Log;

/**
 * Created by lipeng21 on 2017/7/14.
 */

public class WakeManager {
    private static final String TAG = WakeManager.class.getSimpleName();

    private EventManager mWpEventManager;

    public WakeManager(Context pContext){
        // 1) 创建唤醒事件管理器
        mWpEventManager = EventManagerFactory.create(pContext, "wp");
    }

    public void startWake(final WakeListener pWakeListener){
        // 2) 注册唤醒事件监听器
        mWpEventManager.registerListener(new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d(TAG, String.format("event: name=%s, params=%s", name, params));
                try {
                    JSONObject json = new JSONObject(params);
                    if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                        String word = json.getString("word");
                        Log.d(TAG, "唤醒成功, 唤醒词: " + word + "\r\n");
                        pWakeListener.onWakeSuc();
                    } else if ("wp.exit".equals(name)) {
                        Log.d(TAG, "唤醒已经停止: " + params + "\r\n");
                        pWakeListener.onWakeExit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pWakeListener.onWakeFail();
                }
            }
        });

        // 3) 通知唤醒管理器, 启动唤醒功能
        HashMap params = new HashMap();
        params.put("kws-file", "assets:///WakeUp.bin"); // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
    }

    public void onDestroy(){
        // 停止唤醒监听
        mWpEventManager.send("wp.stop", null, null, 0, 0);
    }

    public interface WakeListener{
        void onWakeSuc();
        void onWakeFail();
        void onWakeExit();
    }
}
