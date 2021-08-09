//branch test
package com.cookandroid.voicenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.UiAutomation;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    EditText editText;
    //TextView et3;
    Button logobutton;
    Button mainbutton;

    Intent intent;

    private TextToSpeech tts;

    SpeechRecognizer mRecognizer;
    Intent i;



    final int PERMISSION = 1;

    long delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( Build.VERSION.SDK_INT >= 23 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION); }


        editText = (EditText) findViewById(R.id.editText);

        /*
        et3 = (TextView)findViewById(R.id.textView3);
        int no = intent.getExtras().getInt("no");
        et3.setText(String.valueOf(no));
        */
        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        tts= new TextToSpeech(this, this);


        //로고버튼: 음성인식
        logobutton= findViewById(R.id.logobutton);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
                setBackground("#ff1f4f");
            }
        });


        //화면 전환 후 자동 음성인식 실행
        autoStart();

        //메모 완료 후 저장버튼
        // 한번->음성출력
        // 두번->저장 후 뒤로가기
        // 기본 -> 초록 (#93db58), 음성인식 중 -> 빨강 (#ff1f4f), 음성재생 중 -> 파랑 (#56a8db)
        mainbutton= findViewById(R.id.mainbutton);
        mainbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //한번 클릭
                if( System.currentTimeMillis() > delay ) {
                    delay = System.currentTimeMillis() + 200;
                    speakOut();
                    return;
                }
                //더블 클릭
                if(System.currentTimeMillis() <= delay) {
                    saveMemo();
                }
                else {
                    mRecognizer.startListening(i);
                }
            }

            protected void onDestroy(View view){

            }
        });
    }
    private RecognitionListener listener = new RecognitionListener()
    {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(),"음성인식을 시작합니다.",Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBeginningOfSpeech() {}
    @Override
    public void onRmsChanged(float rmsdB) {}
    @Override
    public void onBufferReceived(byte[] buffer) {}
    @Override public void onEndOfSpeech() {}
    @Override public void onError(int error) {
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
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러 발생: " +
                    message,Toast.LENGTH_SHORT).show(); }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                String resultStr = "";

                for(int i = 0; i < matches.size() ; i++){
                    editText.setText(matches.get(i));
                    resultStr += matches.get(i);
                }
                if(resultStr.length()<1) return;
                resultStr = resultStr.replace(" ","");
                actionActivity(resultStr);

                //"저장", "취소"가 아닐 때만 반복
                if (resultStr.indexOf("저장")>-1){}
                else if(resultStr.indexOf("취소")>-1){}
                else autoStart();
            }
        @Override
        public void onPartialResults(Bundle partialResults) {}
        @Override
        public void onEvent(int eventType, Bundle params) {}

        public void actionActivity(String resultStr){
            if(resultStr.indexOf("다시쓰기")>-1){
                editText.setText(null);
            }
            if(resultStr.indexOf("다시쓰기")>-1){
                editText.setText(null);
            }
            else if(resultStr.indexOf("절반지우기")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                int n = imsi.length();
                imsi = imsi.substring(n/2+5);
                imsi = reverseString(imsi);
                editText.setText(imsi);
            }
            else if(resultStr.indexOf("한자리")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(5);
                imsi = reverseString(imsi);
                editText.setText(imsi);
            }
            else if(resultStr.indexOf("두자리")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(6);
                imsi = reverseString(imsi);
                editText.setText(imsi);
            }
            else if(resultStr.indexOf("세자리")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                imsi = imsi.substring(7);
                imsi = reverseString(imsi);
                editText.setText(imsi);
            }
            else if(resultStr.indexOf("단어")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                int idx = imsi.indexOf(" ");
                String imsi1 = imsi.substring(0, idx);
                imsi1 = imsi1.substring(2);
                imsi1 = reverseString(imsi1);
                editText.setText(imsi1);
            }
            else if(resultStr.indexOf("삭제")>-1) {
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
            }
            else if(resultStr.indexOf("저장")>-1) {
                String str=editText.getText().toString();
                //메모에 하나라도 썼을 경우에만 실행
                if(str.length()>0) {
                    //날짜
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
                }
            }
            else if(resultStr.indexOf("이동")>-1) {
                editText.setText(null);//이동은 잘되는데 원래 텍스트+이동 을 자꾸 다시 읽어서 아예 null처리
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
            }
            speakOut();

            if(resultStr.indexOf("저장")>-1){
                /*
                int a = Integer.parseInt(et3.getText().toString());
                 */
                saveMemo();
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
            }
        }
        else{
            Log.e("TTS","Initilization Failed!");
        }
    }

    private void speakOut(){
        setBackground("#56a8db");
        String text=editText.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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

    //메모 저장
    private void saveMemo(){
        //입력받은 메모 리스트로 보내기
        String str=editText.getText().toString();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String substr = sdf.format(date);

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

    public void setBackground(String color){
        mainbutton.setBackgroundColor(Color.parseColor(color));
    }
}
