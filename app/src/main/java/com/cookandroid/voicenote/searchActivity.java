package com.cookandroid.voicenote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class searchActivity extends AppCompatActivity
{
    private static int buttonOn;

    Intent i;
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;

    ArrayList<Memo> memoArrayList, filteredList;
    searchadapter searchAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText searchET;
    SQLiteHelper dbHelper;
    StringBuilder sp;


    public searchActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        //화면 세로고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);


        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        memoArrayList = new ArrayList<>();
        dbHelper = new SQLiteHelper(searchActivity.this);
        memoArrayList = dbHelper.selectAll();

        sp = new StringBuilder();


        //어뎁터 연결
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setVisibility(View.INVISIBLE);
        searchET = findViewById(R.id.searchMemo);//검색창의 글

        filteredList = new ArrayList<>();

        searchAdapter = new searchadapter(memoArrayList, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchAdapter);

        searchAdapter.notifyDataSetChanged();

        buttonOn=0;

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                //검색 목록 다 읽어온 후 sp 초기화
                int len= sp.length();
                sp.delete(0,len);

                String searchText = searchET.getText().toString();
                searchFilter(searchText);

                //StringBuilder sp = new StringBuilder();
                int l = filteredList.size();

                int k=0;
                for (k= 0; k < l;k++) {
                    StringBuilder sb = new StringBuilder(filteredList.get(k).getMaintext());
                    sp.append(sb);
                    if(k+1<l) {
                        sp.append("\nnext\n");
                    }
                    else {
                        sp.append("\n목록 끝\n\n가장 최근 메모를 불러오시겠습니까?");
                    }
                }
                funcVoiceOut(sp.toString());
            }
        });

        searchET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonOn=0;
                mRecognizer.startListening(i);
            }
        });

        searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    StringBuilder sp = new StringBuilder();
                    for (int k = 0; k < filteredList.size(); k++) {
                        StringBuilder sb = new StringBuilder(filteredList.get(k).getMaintext());
                        sp.append(sb);
                    }
                    funcVoiceOut(sp.toString());
                    searchET.setText(null);
                    return true;
                }
                return false;
            }
        });
        autoStart();

    }


    public void searchFilter(String searchText) {
        filteredList.clear();

        for (int i = 0; i < memoArrayList.size(); i++) {
            if (memoArrayList.get(i).getMaintext().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(memoArrayList.get(i));
            }
        }

        searchAdapter.filterList(filteredList);
        recyclerView.setVisibility(View.VISIBLE);

        autoStart();
    }


    private void autoStart(){
        //검색 음성인식 시작
        if(buttonOn!=1) {
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    searchET.performClick();
                }
            }, 3500);
        }
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            String msg = "검색의 음성인식을 시작합니다.";
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "시간 초과";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "시간 초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러 발생: " +
                    message, Toast.LENGTH_SHORT).show();
            funcVoiceOut("에러 발생 " + message);

        }

        @Override
        public void onResults(Bundle bundle) {

            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String resultStr = "";

            for(int i = 0; i < matches.size() ; i++){
                resultStr += matches.get(i);
            }

            //취소 시 검색음성인식 정지
            if (resultStr.indexOf("취소")>-1){
                buttonOn=1;

                funcVoiceOut("메모검색이 취소되었습니다");
                Intent intent = new Intent(getApplicationContext(), memolistActivity.class);
                startActivityForResult(intent, 101);
            }
            else if(resultStr.indexOf("완료")>-1){
                //음성인식 정지
                buttonOn=1;
                funcVoiceOut("메모검색이 완료되었습니다");
            }
            else if(resultStr.indexOf("네")>-1){
                //음성인식 정지
                funcVoiceOut("메모를 수정합니다");
                buttonOn=1;

                //가장 최신 메모 수정 불러오기
                int l=filteredList.size();
                recyclerView.findViewHolderForAdapterPosition(l-1).itemView.performClick();

            }
            else if(resultStr.indexOf("아니요")>-1){
                //음성인식 정지
                buttonOn=1;
                funcVoiceOut("메모검색이 완료되었습니다");
            }
            else {
                //검색된 메모 출력
                buttonOn=1;
                for(int i = 0; i < matches.size() ; i++){
                    searchET.setText(matches.get(i));
                }
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonOn=0;
                        autoStart();
                    }
                }, 5500);

            }

        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public class searchadapter extends RecyclerView.Adapter<searchadapter.ViewHolder> {
        ArrayList<Memo> MemoArrayList;
        Activity activity;

        public searchadapter(ArrayList<Memo> memoItemArrayList, Activity activity) {
            this.MemoArrayList = memoItemArrayList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            Memo memo = MemoArrayList.get(position);
            holder.maintext.setText(memo.getMaintext());
            holder.subtext.setText(memo.getSubtext());
            holder.maintext.setTag(memo.getSeq());

        }

        @Override
        public int getItemCount() {
            return MemoArrayList.size();
        }

        public void filterList(ArrayList<Memo> filteredList) {
            MemoArrayList = filteredList;
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView maintext;
            private TextView subtext;
            int click = 0;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                maintext = itemView.findViewById(R.id.item_maintext);
                subtext = itemView.findViewById(R.id.item_subtext);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (click == 0) {
                            funcVoiceOut("메모를 수정합니다");
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                Intent intent = new Intent(getApplicationContext(), Detail.class);

                                intent.putExtra("maintext", maintext.getText());
                                intent.putExtra("subtext", subtext.getText());
                                intent.putExtra("no", (int) maintext.getTag());
                                //intent.putExtra("pos",getAdapterPosition());

                                startActivity(intent);
                            }
                        }
                    }
                });

            }

        }

    }

    public void funcVoiceOut(String OutMsg) {
        if (OutMsg.length() < 1) return;

        if (!tts.isSpeaking()) {
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

}
