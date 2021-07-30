package com.cookandroid.voicenote;
//STT
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
//TTS
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class Detail extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private static final int URI_INTENT_SCHEME = 0;
    List<Memo> memoList;

    SQLiteHelper dbHelper;

    public static Context mContext;
    private TextToSpeech tts;
    final int PERMISSON=1;
    Intent sttIntent, i;
    SpeechRecognizer mRecognizer;

    EditText et1;
    TextView et2, et3;
    TextView txtInMsg;
    Button button;
    Button logobutton;
    int click=0;
    long delay;

    public Detail() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        mContext=this;

        dbHelper = new SQLiteHelper(Detail.this);
        memoList = dbHelper.selectAll();

        button = (Button)findViewById(R.id.mainbutton);

        et1 = (EditText)findViewById(R.id.editText);
        et2 = (TextView)findViewById(R.id.textView2);
        et3 = (TextView)findViewById(R.id.textView3);
        Intent intent = getIntent();

        String text = intent.getExtras().getString("maintext");
        String subtext = intent.getExtras().getString("subtext");
        int no = intent.getExtras().getInt("no");

        et1.setText(text);
        et2.setText(subtext);
        et3.setText(String.valueOf(no));

        //오디오 권한 설정
        if(Build.VERSION.SDK_INT>=23){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSON);
        }

        //STT 객체 생성, 초기화
        sttIntent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        sttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");
        //TTS 객체 생성, 초기화
        tts= new TextToSpeech(this,this);

        //버튼 이벤트
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=et1.getText().toString();
                if( System.currentTimeMillis() > delay ) {
                    delay = System.currentTimeMillis() + 200;
                    speakOut();
                    return;
                }
                if(System.currentTimeMillis() <= delay) {
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String substr = sdf.format(date);

                    //입력받은 메모 리스트로 보내기
                    Intent intent1 = new Intent(getApplicationContext(), memolistActivity.class);
                    intent1.putExtra("main", str);
                    intent1.putExtra("sub", substr);
                    setResult(200, intent1);
                    speakOut();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            finish();
                        }
                    }, 5000);
                    finish();
                }
                else {
                    mRecognizer.startListening(i);
                }
            }

            protected void onDestroy(View view){

            }
        });

        //버튼 길게 누르면 수정
        button.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                click=1;
                funcVoiceOut("음성인식을 수정합니다.");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //롱클릭이랑 클릭이 겹치지 않도록
                        click=0;
                        mRecognizer= SpeechRecognizer.createSpeechRecognizer(mContext);
                        mRecognizer.setRecognitionListener(listener); //음성인식 리스너 등록
                        mRecognizer.startListening(sttIntent);
                        //intent값을 String으로 변경
                        String answer= sttIntent.toUri(URI_INTENT_SCHEME);
                        //modifyActivity(answer);
                    }
                }, 1000);  // 1 초 후에 실행

                return false;
            }

        });

        //로고 버튼: 리스트로 이동
        logobutton= findViewById(R.id.logobutton);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
            }
        });

    }

    private void speechStart(){
        //음성인식 객체
        mRecognizer= SpeechRecognizer.createSpeechRecognizer(mContext);
        mRecognizer.setRecognitionListener(listener); //음성인식 리스너 등록
        mRecognizer.startListening(sttIntent);
    }

    private RecognitionListener listener= new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Toast.makeText(getApplicationContext(), "음성인식을 수정합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간 초과";
                    break;
                default:
                    message = "";
                    break;
            }
            String guideStr = "수정 취소";
            Toast.makeText(getApplicationContext(), guideStr + message, Toast.LENGTH_SHORT).show();
            funcVoiceOut(guideStr);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String resultStr="";
            for (int i = 0; i < matches.size(); i++) {

                et1.append(matches.get(i));
                resultStr += matches.get(i);
            }
            if (resultStr.length() < 1) return;
            resultStr = resultStr.replace(" ", "");

            actionActivity(resultStr);
        }

        @Override
        public void onPartialResults(Bundle bundle) {
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
        }
        public void actionActivity(String resultStr){
            if(resultStr.indexOf("다시쓰기")>-1){
                et1.setText(null);
            }
            else if(resultStr.indexOf("절반지우기")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                int n = imsi.length();
                imsi = imsi.substring(n/2+5);
                imsi = reverseString(imsi);
                et1.setText(imsi);
            }
            else if(resultStr.indexOf("조금지우기")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                int n = imsi.length();
                imsi = imsi.substring(n/5);
                imsi = reverseString(imsi);
                et1.setText(imsi);
            }
            else if(resultStr.indexOf("단어지우기")>-1){
                String imsi = et1.getText().toString();
                int idx = imsi.indexOf(" ");
                String imsi1 = imsi.substring(0, idx);
                et1.setText(imsi1);
            }
            else if(resultStr.indexOf("삭제")>-1) {
                int a = Integer.parseInt(et3.getText().toString());
                dbHelper.deleteMemo(a);
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
            }
            else if(resultStr.indexOf("이동")>-1) {
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
            }
            speakOut();
        }
    };
    public static String reverseString(String s){
        return (new StringBuffer(s)).reverse().toString();
    }

    @Override
    public void onDestroy(){
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {
        if(status== TextToSpeech.SUCCESS){
            int result=tts.setLanguage(Locale.KOREA);

            if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","This Language is not supported");
            } else{
                button.setEnabled(true);
                speakOut();
            }
        }
        else{
            Log.e("TTS","Initilization Failed!");
        }
    }

    //입력받은 음성에서 조건에 따라 실행(부분 수정, 완전 수정 나눠야하는부분)
    /*public void modifyActivity(String resultStr){

        if(resultStr.indexOf("전체 수정") >-1){
            String guideStr ="전체 수정을 시작합니다.";
            Toast.makeText(getApplicationContext(), guideStr, Toast.LENGTH_SHORT).show();
            funcVoiceOut(guideStr);
            speechStart();
        }
    }*/

    //음성 문자열 함수에 직접 받아서 음성출력
    public void funcVoiceOut(String OutMsg){
        if(OutMsg.length()<1) return;
        if(!tts.isSpeaking()){
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    //메모장에 적힌 텍스트 받아서 음성으로 출력
    private void speakOut(){
        String text=et1.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}