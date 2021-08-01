package com.cookandroid.voicenote;

//STT
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class memolistActivity extends AppCompatActivity {
    final int PERMISSION = 1;
    Intent intent;

    SQLiteHelper dbHelper;


    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;

    Intent i;
    SpeechRecognizer mRecognizer;

    //어플 실행 후 음성인식 자동 실행
    EditText autoSystem;

    //recyclerView에 들어갈 전역변수 List
    List<Memo> memoList;

    Button button;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memolist);

        i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mRecognizer.setRecognitionListener(listener);


        if ( Build.VERSION.SDK_INT >= 23 ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION); }

        dbHelper = new SQLiteHelper(memolistActivity.this);
        memoList = dbHelper.selectAll();

        //recyclerView와 recyclerAdapter 연결
        recyclerView= findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter= new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);

        //작성하기 버튼
        button= findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(memolistActivity.this, MainActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        //로고버튼으로 음성인식 받기
        button3= findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecognizer.startListening(i);
            }
        });


        //자동 음성인식 실행
        autoStart();
    }

    private void autoStart(){
        //2초 후 자동 음성인식 실행
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button3.performClick();
            }
        },2000);
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
                    message,Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onResults(Bundle results) {

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            String resultStr = "";

            for(int i = 0; i < matches.size() ; i++){
                resultStr += matches.get(i);
            }
            if(resultStr.length()<1) return;
            resultStr = resultStr.replace(" ","");
            actionActivity(resultStr);

            //다른 화면 넘어가면 음성인식 실행 하지 않도록
            if (resultStr.indexOf("메모작성")>-1){}
            else if(resultStr.indexOf("메모수정")>-1){}
            else autoStart();

        }
        @Override
        public void onPartialResults(Bundle partialResults) {}
        @Override
        public void onEvent(int eventType, Bundle params) {}

        public void actionActivity(String resultStr){
            if(resultStr.indexOf("메모작성")>-1){
                Intent intent = new Intent(memolistActivity.this, MainActivity.class);
                startActivityForResult(intent, 0);
                //메모작성 후 음성인식 반복 정지
            }
            else if(resultStr.indexOf("메모수정")>-1){
                Toast.makeText(getApplicationContext(),"메모 검색 명령어 인식",Toast.LENGTH_SHORT).show();
            }
            else if(resultStr.indexOf("전체삭제")>-1){
                Toast.makeText(getApplicationContext(),"전체 삭제 명령어 인식",Toast.LENGTH_LONG).show();

                dbHelper.deleteAll();
                //삭제 후 음성인식 재 실행
                autoStart();
            }

        }
    };


    public static String reverseString(String s){
        return (new StringBuffer(s)).reverse().toString();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==200){
            //MainActivity에서 입력받은 메모데이터값 받아오기
            String strMain= data.getStringExtra("main");
            String strSub= data.getStringExtra("sub");

            //Gradle Scripts에(앱단위 그래들)에 recyclerView 추가
            Memo memo= new Memo(strMain, strSub,0);
            recyclerAdapter.addItem(memo);
            recyclerAdapter.notifyDataSetChanged();

            dbHelper.insertMemo(memo);

        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {
        private List<Memo> listdata;

        public RecyclerAdapter(List<Memo> listdata) {
            this.listdata = listdata;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
            return new ItemViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            //데이터 레이아웃에 어떻게 넣어줄지를 결정
            Memo memo = listdata.get(i);

            //remove기능위해 seq가져 오기
            itemViewHolder.maintext.setTag(memo.getSeq());

            itemViewHolder.maintext.setText(memo.getMaintext());
            itemViewHolder.subtext.setText(memo.getSubtext());
        }

        void addItem(Memo memo) {
            listdata.add(memo);
        }

        void removeItem(int position) {
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            private TextView maintext;
            private TextView subtext;
            int click=0;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);


                maintext = itemView.findViewById(R.id.item_maintext);
                subtext = itemView.findViewById(R.id.item_subtext);

                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //LongClick이랑 중복 안될때만 실행
                        if(click==0) {
                            int pos = getAdapterPosition();
                            if (pos != RecyclerView.NO_POSITION) {
                                Intent intent = new Intent(getApplicationContext(), Detail.class);

                                intent.putExtra("maintext", maintext.getText());
                                intent.putExtra("subtext", subtext.getText());
                                intent.putExtra("no", (int)maintext.getTag());
                                //intent.putExtra("pos",getAdapterPosition());

                                startActivity(intent);
                            }
                        }
                    }
                });

                //길게 눌러서 삭제
                itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View view) {
                        //LongClick이랑 one Click이랑 겹치지 않게 하기 위함
                        click=1;

                        AlertDialog.Builder builder= new AlertDialog.Builder(memolistActivity.this);
                        builder.setMessage("정말로 삭제하시겠습니까?");
                        builder.setTitle(("삭제알림창"));
                        builder.setCancelable(false);

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();

                                //다시 one Click가능하게 바꿔줌
                                click=0;
                            }
                        });

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int position=getAdapterPosition();
                                int seq= (int)maintext.getTag();

                                if(position!= RecyclerView.NO_POSITION) {
                                    dbHelper.deleteMemo(seq);
                                    removeItem(position);
                                    notifyDataSetChanged();
                                }
                                //다시 one Click가능하게 바꿔줌
                                click=0;
                            }
                        });
                        AlertDialog alert= builder.create();
                        alert.setTitle("삭제 알림창");
                        alert.show();
                        return false;
                    }
                });
            }

        }
    }
}
