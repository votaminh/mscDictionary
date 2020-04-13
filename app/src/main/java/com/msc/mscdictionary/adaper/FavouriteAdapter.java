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
import com.msc.mscdictionary.util.AppUtil;
import com.msc.mscdictionary.util.Constant;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {
    List<Word> wordList;
    Context context;
    AdapterCallback callback;
    RemoveCallback removeCallback;

    public FavouriteAdapter(List<Word> list){
        this.wordList = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, new CardView(context), false);
        return new ViewHolder(view);
    }

    public void setCallback(AdapterCallback callback){
        this.callback = callback;
    }

    public void setRemoveCallback(RemoveCallback removeCallback){
        this.removeCallback = removeCallback;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(holder == null){
            return;
        }
        Word word = wordList.get(position);
        holder.tvVoice.setText(word.getVoice());
        holder.tvMean.setText(word.getEnWord().substring(0, 1).toUpperCase() + word.getEnWord().substring(1).toLowerCase());

        holder.btnFavourite.setOnClickListener(v -> {
            removeFavourite(word, holder.btnFavourite);
            new Handler().postDelayed(() -> {
                wordList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, wordList.size());
                if(removeCallback != null){
                    removeCallback.removeItem(position);
                }
            }, Constant.DURATION_SCALE_FAVOURITE);
        });

        holder.btnSpeaker.setOnClickListener(v -> {
            AppUtil.checkDownloadOfflineAudio(word, context);
            MediaBuilder.playLink(context, word.getUrlSpeak(), new MediaBuilder.MediaCallback() {
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

                @Override
                public void fail(String error) {

                }
            });
        });
    }

    private void removeFavourite(Word word, ImageButton imageView) {
        animationUnLike(imageView);
        OffFavouriteDAO favouriteDAO = new OffFavouriteDAO(context);
        favouriteDAO.remove(word);
    }

    private void animationUnLike(View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(Constant.DURATION_SCALE_FAVOURITE);
        scaleAnimation.setInterpolator(new AnticipateInterpolator());
        view.startAnimation(scaleAnimation);

        new Handler().postDelayed(() -> ((ImageButton)view).setImageResource(R.drawable.ic_favourite), Constant.DURATION_SCALE_FAVOURITE);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public void setData(List<Word> wordList) {
        this.wordList = wordList;
        notifyDataSetChanged();
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

    public interface RemoveCallback{
        void removeItem(int i);
    }
}
