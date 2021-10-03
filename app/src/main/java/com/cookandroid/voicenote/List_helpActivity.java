package com.cookandroid.voicenote;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Locale;

public class List_helpActivity extends Activity {

    TextView txtText;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        txtText = (TextView)findViewById(R.id.txtText);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        data = "화면 상단에는 검색, 음성명령 호출, 도움말 버튼이 있습니다. \n" +
                "리스트 화면의 음성명령 키워드는 취소, 메모작성, 검색, 전체삭제 가 있습니다";
        txtText.setText(data);

    }

    public void mOnClose(View v){
        finish();
    }

}
