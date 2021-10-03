package com.cookandroid.voicenote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.RecognizerIntent;

import com.cookandroid.voicenote.R;

import java.util.Locale;

public class LoadingActivity extends Activity{

    Intent i;
    private TextToSpeech tts;
    public SharedPreferences pref;
    int num=0; //처음 시작인지 구별

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //화면 세로고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lodingpage);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        //TTS 객체 생성, 초기화
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        isFirst();
    }

    private void isFirst(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pref= getSharedPreferences("isFirst", MODE_PRIVATE);
                boolean first= pref.getBoolean("isFirst", true);

                if(first==true) {
                    //앱 최초 실행시만 음성 출력
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit();

                    funcVoiceOut("안녕하세요\n"+
                            "보이스노트를 찾아주셔서 감사합니다\n"+
                            "어플이 실행되면 자동으로 음성인식이 시작됩니다\n" +
                            "띠링소리가 들리면 음성 명령어를 실행해주세요\n" +
                            "음성 명령어 설명을 원하시면 각 화면의 도움말 명령어를 이용해주세요\n"+
                            "메모 작성 시 한줄씩 작성 후 메모읽기 명령어를 이용하면서 수정하는 것을 추천드립니다");

                    //첫 실행시에는 로딩 화면 조금 더 길게
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startLoading();
                        }
                    },24000);
                }
                else {
                    //이후 실행 시에는 로딩 화면 시간 짧게
                    startLoading();
                }
            }
        },1000);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(), memolistActivity.class);
                startActivity(intent);
                finish();
            }
        },1000);

    }

    //음성 문자열 함수에 직접 받아서 음성출력
    public void funcVoiceOut(String OutMsg) {
        if (OutMsg.length() < 1) return;

        if(!tts.isSpeaking()){
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}