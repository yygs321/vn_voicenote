package com.cookandroid.voicenote;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class searchActivity extends AppCompatActivity
{
    Intent i;
    SpeechRecognizer mRecognizer;
    TextToSpeech tts;

    ArrayList<Memo> memoArrayList, filteredList;
    searchadapter searchAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText searchET;
    SQLiteHelper dbHelper;

    public searchActivity() {
    }
    protected void onCreate(Bundle savedInstanceState) {
        //화면 세로고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);

        memoArrayList = new ArrayList<>();
        dbHelper = new SQLiteHelper(searchActivity.this);
        memoArrayList = dbHelper.selectAll();


        //어뎁터 연결
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setVisibility(View.INVISIBLE);
        searchET = findViewById(R.id.searchMemo);//검색창의 글

        filteredList=new ArrayList<>();

        searchAdapter = new searchadapter(memoArrayList, this);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchAdapter);

        searchAdapter.notifyDataSetChanged();

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String searchText = searchET.getText().toString();
                searchFilter(searchText);

            }
        });

        searchET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
            }
        });
        autoStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
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

        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!= TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        mRecognizer.startListening(i);

    }

    private void autoStart(){
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchET.performClick();
            }}, 3500);

    }

    private RecognitionListener listener = new RecognitionListener()
    {
        @Override
        public void onReadyForSpeech(Bundle params) {
            String msg="검색의 음성인식을 시작합니다.";
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
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
                searchET.setText(matches.get(i));
            }
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    public class searchadapter extends RecyclerView.Adapter<searchadapter.ViewHolder>{
        ArrayList<Memo> MemoArrayList;
        Activity activity;

        public searchadapter(ArrayList<Memo> memoItemArrayList, Activity activity) {
            this.MemoArrayList = memoItemArrayList;
            this.activity = activity;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
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

        public void  filterList(ArrayList<Memo> filteredList) {
            MemoArrayList = filteredList;
            notifyDataSetChanged();
        }


        public class ViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;
            int click=0;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                maintext = itemView.findViewById(R.id.item_maintext);
                subtext = itemView.findViewById(R.id.item_subtext);

                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(click==0) {
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

        if(!tts.isSpeaking()){
            tts.speak(OutMsg, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

}
