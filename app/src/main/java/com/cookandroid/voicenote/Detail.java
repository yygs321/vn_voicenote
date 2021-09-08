package com.cookandroid.voicenote;
//STT
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class Detail extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private static final int URI_INTENT_SCHEME = 0;
    List<Memo> memoList;
//test
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
        //화면 세로고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        autoStart();

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
                //더블 클릭시 저장
                if(System.currentTimeMillis() <= delay) {
                    //저장시 메모리스트에서 음성인식되지 않도록
                    memolistActivity.ButtonOff();

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
                    funcVoiceOut("수정이 완료되었습니다");
                }
                else {
                    mRecognizer.startListening(i);
                }
            }

            protected void onDestroy(View view){

            }
        });


        //로고 버튼: 음성인식
        logobutton= findViewById(R.id.logobutton);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setBackground("#ff1f4f");
                        mRecognizer.startListening(i);
                    }
                },2000);
            }
        });

    }

    //자동 음성인식
    private void autoStart(){
        //2초 후 자동 음성인식 실행
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                logobutton.performClick();
                setBackground("#ff1f4f");
            }
        },2000);
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
            Toast.makeText(getApplicationContext(), "음성인식을 실행합니다.", Toast.LENGTH_SHORT).show();
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
                    message = "시간 초과";
                    break;
                default:
                    message = "시간 초과";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러 발생:" + message, Toast.LENGTH_SHORT).show();
            funcVoiceOut("에러 발생 "+message);
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

            //저장, 이동, 삭제가 아닐 때만 반복
            if (resultStr.indexOf("저장")>-1){
                memolistActivity.ButtonOff();
            }
            else if(resultStr.indexOf("취소")>-1){
            }
            else if(resultStr.indexOf("삭제")>-1){
            }
            else if(resultStr.indexOf("메모읽기")>-1){}
            else if(resultStr.indexOf("글로쓰기")>-1){
                setBackground("#93db58");
            }
            else if(resultStr.indexOf("검색")>-1){
                setBackground("#93db58");
            }
            else if(resultStr.indexOf("도움말")>-1){
                setBackground("#93db58");
            }
            else autoStart();


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
            else if(resultStr.indexOf("한자리")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(5);
                imsi = reverseString(imsi);
                et1.setText(imsi);
            }
            else if(resultStr.indexOf("두자리")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(6);
                imsi = reverseString(imsi);
                et1.setText(imsi);
            }
            else if(resultStr.indexOf("세자리")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(7);
                imsi = reverseString(imsi);
                et1.setText(imsi);
            }
            else if(resultStr.indexOf("단어")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                int idx = imsi.indexOf(" ");
                //제일뒷단어+띄어쓰기 지운 나머지 내용만 작성
                String imsi1 = imsi.substring(idx+1, imsi.length());
                imsi1 = reverseString(imsi1);
                et1.setText(imsi1);
            }
            else if(resultStr.indexOf("띄어쓰기")>-1){
                String str=et1.getText().toString();
                str = reverseString(str);
                str = str.substring(4);
                str = reverseString(str);
                et1.setText(str+" ");
            }
            else if(resultStr.indexOf("한줄띄우기")>-1){
                String str=et1.getText().toString();
                str = reverseString(str);
                str = str.substring(6);
                str = reverseString(str);
                et1.setText(str+"\n");
            }
            else if(resultStr.indexOf("한줄지우기")>-1){
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                int idx = imsi.indexOf("\n");
                //한줄+엔터 지운 나머지 내용만 작성
                String imsi1 = imsi.substring(idx+1, imsi.length());
                imsi1 = reverseString(imsi1);
                et1.setText(imsi1);
            }
            else if(resultStr.indexOf("메모읽기")>-1) {
                String str=et1.getText().toString();
                str = reverseString(str);
                str = str.substring(5);
                str = reverseString(str);
                et1.setText(str);

                speakOut();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoStart();
                    }
                }, 4000);
            }
            else if(resultStr.indexOf("삭제")>-1) {
                int a = Integer.parseInt(et3.getText().toString());
                dbHelper.deleteMemo(a);
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
                funcVoiceOut("메모가 삭제되었습니다");
            }
            else if(resultStr.indexOf("저장")>-1) {
                int a = Integer.parseInt(et3.getText().toString());
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(2);
                imsi = reverseString(imsi);
                et1.setText(imsi);

                dbHelper.updateMemo(a, imsi);
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                speakOut(); //수정된 메모 읽어주기

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(intent, 101);
                        funcVoiceOut("수정이 완료되었습니다");
                    }
                },3000);

            }
            else if(resultStr.indexOf("취소")>-1) {
                et1.setText(null);//이동은 잘되는데 원래 텍스트+이동 을 자꾸 다시 읽어서 아예 null처리
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
                funcVoiceOut("메모수정이 취소되었습니다");
            }
            else if(resultStr.indexOf("글로쓰기")>-1) {
                String str=et1.getText().toString();
                str = reverseString(str);
                str = str.substring(5);
                str = reverseString(str);
                et1.setText(str);

                funcVoiceOut("음성인식이 취소되었습니다");
            }
            else if(resultStr.indexOf("검색")>-1){
                setBackground("#93db58");
                int a = Integer.parseInt(et3.getText().toString());
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(2);
                imsi = reverseString(imsi);
                et1.setText(imsi);

                funcVoiceOut("검색 화면으로 이동합니다");
                Intent intent = new Intent(Detail.this, searchActivity.class);
                startActivityForResult(intent, 0);
            }
            else if(resultStr.indexOf("도움말")>-1){
                setBackground("#93db58");
                int a = Integer.parseInt(et3.getText().toString());
                String imsi = et1.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(3);
                imsi = reverseString(imsi);
                et1.setText(imsi);

                Intent intent = new Intent(Detail.this, Detail_helpActivity.class);
                startActivityForResult(intent, 1);
            }
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

    //음성 문자열 함수에 직접 받아서 음성출력
    public void funcVoiceOut(String OutMsg){
        if(OutMsg.length()<1) return;
        if(!tts.isSpeaking()){
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    //메모장에 적힌 텍스트 받아서 음성으로 출력
    private void speakOut(){
        setBackground("#56a8db");
        String text=et1.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void setBackground(String color){
        button.setBackgroundColor(Color.parseColor(color));
    }

    public void mOnPopupClick(View v){
        Intent intent = new Intent(this, Detail_helpActivity.class);
        startActivityForResult(intent, 1);
        tts.speak("화면 상단에는 검색, 음성명령 호출, 도움말 버튼이 있습니다. 메모작성 화면의 음성명령 키워드는 글로 쓰기, 다시 쓰기, 절반 지우기, " +
                        "한 자리, 두 자리, 세 자리, 단어 삭, 띄어쓰기, 한 줄 띄우기, 메모 읽기, 삭제, 저장, 취소 가 있습니다",
                TextToSpeech.QUEUE_FLUSH,null);
    }

    public void mOnSearchClick(View v){
        Intent intent = new Intent(this, searchActivity.class);
        startActivityForResult(intent, 1);
    }
}