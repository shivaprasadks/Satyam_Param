package com.think.survey2016.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.think.survey2016.MainActivity;
import com.think.survey2016.PollActivity;
import com.think.survey2016.R;
import com.think.survey2016.model.QuestionModel;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by Super on 03-12-2016.
 */


public class AdapterQuestion extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<QuestionModel> data = Collections.emptyList();
    private QuestionModel current;
    int currentPos = 0;

    //constructor
    public AdapterQuestion(Context context, List<QuestionModel> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.content_question, parent, false);
       MyHolder holder = new MyHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        current = data.get(position);
        myHolder.p_name.setText(current.qid);
        //  myHolder.textSize.setText("Size: " + current.sizeName);
        //  myHolder.textType.setText("Category: " + current.catName);
        //  myHolder.p_price.setText("₹ " + current.price + "/-");
        //  myHolder.p_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        // load image into imageview using glide
        Glide.with(context).load(current.qimg)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(myHolder.q_image);

        myHolder.tv_total.setText("Votes : " + current.total);

        myHolder.y_count.setText(":" + current.op1);
        myHolder.n_count.setText(":" + current.op2);

        myHolder.btn_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mID = current.qid;
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("QID", mID);
                context.startActivity(i);
                Log.d("Click", "onClick " + mID );
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView p_name,y_count,n_count;
        ImageView q_image;
        private AdapterView.OnItemClickListener listener;
        private TextView tv_total;
        BootstrapButton btn_poll;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            p_name = (TextView) itemView.findViewById(R.id.tv_question);
            q_image = (ImageView) itemView.findViewById(R.id.qImg);
            tv_total = (TextView) itemView.findViewById(R.id.tv_total);
            btn_poll = (BootstrapButton) itemView.findViewById(R.id.poll);
            y_count = (TextView) itemView.findViewById(R.id.yes_count);
            n_count = (TextView) itemView.findViewById(R.id.no_count);
            itemView.setOnClickListener(this);
            //  p_price.setSelected(true);

        }

        @Override
        public void onClick(View view) {
            int a = getAdapterPosition();
            QuestionModel current = data.get(a);
            String mID = current.qid;

            Intent i = new Intent(context, MainActivity.class);
            i.putExtra("QID", mID);
            context.startActivity(i);
            ((Activity)context).finish();
            Log.d("Click", "onClick " + mID );

        }
    }


}
