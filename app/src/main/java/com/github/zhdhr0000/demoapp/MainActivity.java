package com.github.zhdhr0000.demoapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        recyclerView.setAdapter(new MyAdapter(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.VH> {

        Activity activity;

        public MyAdapter(Activity activity) {
            this.activity = activity;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(new TextView(activity));
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            holder.textView.setText("Position : " + position);
            holder.textView.setTextSize(20);
            holder.textView.setBackgroundColor(Color.RED);
        }

        @Override
        public int getItemCount() {
            return 300;
        }

        class VH extends RecyclerView.ViewHolder {
            TextView textView;

            public VH(View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
}
