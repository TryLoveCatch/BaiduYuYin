package io.github.trylovecatch.baiduyuyin;

import static android.os.MessageQueue.OnFileDescriptorEventListener.EVENT_ERROR;

import java.util.ArrayList;
import java.util.Arrays;

import com.baidu.speech.VoiceRecognitionService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

/**
 * Created by lipeng21 on 2017/7/14.
 */

public class RecognizerManager implements RecognitionListener {
    private static final String TAG = RecognizerManager.class.getSimpleName();

    public static final int STATUS_None = 0;
    public static final int STATUS_WaitingReady = 2;
    public static final int STATUS_Ready = 3;
    public static final int STATUS_Speaking = 4;
    public static final int STATUS_Recognition = 5;
    private int status = STATUS_None;

    private SpeechRecognizer speechRecognizer;

    public RecognizerManager(){

    }

    public void start(Context pContext){
        Intent intent = new Intent();
        bindParams(intent);


        // api方式
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(pContext
                , new ComponentName(pContext, VoiceRecognitionService.class));
        speechRecognizer.setRecognitionListener(this);
        speechRecognizer.startListening(intent);
        status = STATUS_WaitingReady;

        // ui方式
//        intent.setAction("com.baidu.action.RECOGNIZE_SPEECH");
//        ((Activity)pContext).startActivityForResult(intent, 1);
    }

    public void stop() {
        if (speechRecognizer != null) {
            switch (status) {
                case STATUS_WaitingReady:
                    cancel();
                    status = STATUS_None;
                    break;
                case STATUS_Ready:
                    cancel();
                    status = STATUS_None;
                    break;
                case STATUS_Speaking:
                    stop();
                    status = STATUS_Recognition;
                    break;
                case STATUS_Recognition:
                    cancel();
                    status = STATUS_None;
                    break;
            }
        }
    }

    public void onDestroy(){
        stop();
        if(speechRecognizer!=null) {
            speechRecognizer.destroy();
        }
    }

    private void cancel() {
        speechRecognizer.cancel();
        status = STATUS_None;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        status = STATUS_Ready;
        Log.e(TAG, "准备就绪，可以开始说话");
    }

    @Override
    public void onBeginningOfSpeech() {
        status = STATUS_Speaking;
        Log.e(TAG, "检测到用户的已经开始说话");
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        status = STATUS_Recognition;
        Log.e(TAG, "检测到用户的已经停止说话");
    }

    @Override
    public void onError(int error) {
        status = STATUS_None;
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                break;
        }
        sb.append(":" + error);
        Log.e(TAG, "识别失败：" + sb.toString());
    }

    @Override
    public void onResults(Bundle results) {
        status = STATUS_None;
        //说的话
        ArrayList<String> tResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String tStr = Arrays.toString(tResults.toArray());
        Log.e(TAG, "识别成功：" + tStr);
        String tJson = results.getString("origin_result");
        Log.e(TAG, tJson);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        switch (eventType) {
            case EVENT_ERROR:
                String reason = params.get("reason") + "";
                Log.e(TAG, "EVENT_ERROR, " + reason);
                break;
            case VoiceRecognitionService.EVENT_ENGINE_SWITCH:
                int type = params.getInt("engine_type");
                Log.e(TAG, "*引擎切换至" + (type == 0 ? "在线" : "离线"));
                break;
        }
    }


    private void bindParams(Intent intent) {
        // 说话开始的提示音
        intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
        // 说话结束的提示音
        intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
        // 识别成功的提示音
        intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
        // 识别出错的提示音
        intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
        // 识别取消的提示音
        intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        // 音频源
//        intent.putExtra(Constant.EXTRA_INFILE, tmp);
        // 保存识别过程产生的录音文件
//        intent.putExtra(Constant.EXTRA_OUTFILE, "sdcard/outfile.pcm");
        // 离线识别的语法路径
//        intent.putExtra(Constant.EXTRA_GRAMMAR, "assets:///baidu_speech_grammar.bsg");
        // 采样率
//        intent.putExtra(Constant.EXTRA_SAMPLE, Integer.parseInt(tmp));
        // 语种
//        intent.putExtra(Constant.EXTRA_LANGUAGE, tmp);
        // 语义解析设置 disable enable
        intent.putExtra(Constant.EXTRA_NLU, "enable");
        // 语音活动检测 search	搜索模式，适合短句输入
        // input	输入模式，适合短信、微博内容等长句输入
//        intent.putExtra(Constant.EXTRA_VAD, tmp);
        // 垂直领域，2.1版本后离线功能请使用grammar参数
//        intent.putExtra(Constant.EXTRA_PROP, Integer.parseInt(tmp));
    }
}
