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
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    EditText editText;
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

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        tts= new TextToSpeech(this, this);

        //로고 버튼: 리스트로 이동
        /*logobutton = (Button) findViewById(R.id.logobutton);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
            }
        });*/

        logobutton= findViewById(R.id.logobutton);
        logobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
            }
        });

        //메모 완료 후 저장버튼
        // 한번->음성출력
        // 두번->저장 후 뒤로가기
        mainbutton= findViewById(R.id.mainbutton);
        mainbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str=editText.getText().toString();
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
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " +
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
        }
        @Override
        public void onPartialResults(Bundle partialResults) {}
        @Override
        public void onEvent(int eventType, Bundle params) {}

        public void actionActivity(String resultStr){
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
            else if(resultStr.indexOf("조금지우기")>-1){
                String imsi = editText.getText().toString();
                imsi = reverseString(imsi);
                int n = imsi.length();
                imsi = imsi.substring(n/5);
                imsi = reverseString(imsi);
                editText.setText(imsi);
            }
            else if(resultStr.indexOf("단어지우기")>-1){
                String imsi = editText.getText().toString();
                int idx = imsi.indexOf(" ");
                String imsi1 = imsi.substring(0, idx);
                editText.setText(imsi1);
            }
            else if(resultStr.indexOf("취소")>-1) {
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
            }
        }
        else{
            Log.e("TTS","Initilization Failed!");
        }
    }

    private void speakOut(){
        String text=editText.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
