package com.msc.mscdictionary.adaper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.msc.mscdictionary.R;
import com.msc.mscdictionary.callback.AdapterCallback;
import com.msc.mscdictionary.database.OffFavouriteDAO;
import com.msc.mscdictionary.media.MediaBuilder;
import com.msc.mscdictionary.model.Word;
import com.msc.mscdictionary.util.Constant;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    List<Word> wordList;
    Context context;
    AdapterCallback callback;
    OffFavouriteDAO favouriteDAO;

    public HistoryAdapter(List<Word> list){
        this.wordList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        favouriteDAO = new OffFavouriteDAO(context);
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, new CardView(context), false);
        return new ViewHolder(view);
    }

    public void setCallback(AdapterCallback callback){
        this.callback = callback;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.tvVoice.setText(word.getVoice());
        holder.tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());

        if(favouriteDAO.checkHas(word)){
            holder.btnFavourite.setImageResource(R.drawable.ic_favourite_select);
        }else {
            holder.btnFavourite.setImageResource(R.drawable.ic_favourite);
        }

        holder.btnFavourite.setOnClickListener(v -> {
            if(favouriteDAO.checkHas(word)){
                removeFavourite(word, holder.btnFavourite);
            }else {
                addFavourite(word, holder.btnFavourite);
            }
        });

        holder.btnSpeaker.setOnClickListener(v -> {
            MediaBuilder.playLink(word.getUrlSpeak(), new MediaBuilder.MediaCallback() {
                @Override
                public void start() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.btnSpeaker.setVisibility(View.INVISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void end() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        holder.btnSpeaker.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    });
                }
            });
        });
    }

    private void addFavourite(Word word, ImageButton btnFavourite) {
        favouriteDAO.add(word);
        animationLike(btnFavourite);
    }

    private void removeFavourite(Word word, ImageButton imageView) {
        animationUnLike(imageView);
        OffFavouriteDAO favouriteDAO = new OffFavouriteDAO(context);
        favouriteDAO.remove(word);
    }

    private void animationLike(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(Constant.DURATION_SCALE_FAVOURITE);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        view.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> ((ImageButton)view).setImageResource(R.drawable.ic_favourite_select), 500);
    }

    private void animationUnLike(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(Constant.DURATION_SCALE_FAVOURITE);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        view.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> ((ImageButton)view).setImageResource(R.drawable.ic_favourite), 500);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMean;
        TextView tvVoice;
        ImageButton btnSpeaker;
        ProgressBar progressBar;
        ImageButton btnFavourite;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMean = itemView.findViewById(R.id.tvMean);
            tvVoice = itemView.findViewById(R.id.tvVoice);
            btnSpeaker = itemView.findViewById(R.id.tvAudio);
            progressBar = itemView.findViewById(R.id.progressVoice);
            btnFavourite = itemView.findViewById(R.id.btnFavourite);

            itemView.setOnClickListener(v -> {
                if(callback != null){
                    callback.itemClick(getLayoutPosition());
                }
            });
        }
    }
}
