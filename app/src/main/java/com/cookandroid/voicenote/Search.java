package com.cookandroid.voicenote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    ArrayList<Memo> memoArrayList, filteredList;
    searchadapter searchAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EditText searchET;
    SQLiteHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        memoArrayList = new ArrayList<>();
        dbHelper = new SQLiteHelper(Search.this);
        memoArrayList = dbHelper.selectAll();


        //어뎁터 연결
        recyclerView = findViewById(R.id.recyclerview);
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
    }

}