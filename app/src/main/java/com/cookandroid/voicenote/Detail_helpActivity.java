package com.cookandroid.voicenote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Detail_helpActivity extends Activity {

    TextView txtText;
    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help);

        txtText = (TextView)findViewById(R.id.txtText);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        data = "화면 상단에는 검색, 음성명령 호출, 도움말 버튼이 있습니다\n"+
                "화면 하단버튼을 한번 클릭 시 메모 음성출력, 더블 클릭 시 메모 저장이 가능합니다\n"+
                "음성으로 글로쓰기, 메모 읽기, 삭제, 저장, 취소 명령어를 실행할 수 있습니다. \n" +
                "수정을 위한 음성 명령 키워드는 다시쓰기, 한 줄 지우기, 절반 지우기\n" +
                "한 자리, 두 자리, 세 자리, 단어, 띄어쓰기, 한 줄 띄우기 가 있습니다";
        txtText.setText(data);
    }

    public void mOnClose(View v){
        finish();
    }



}
