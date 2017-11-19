package kamrulhasan3288.com.audiostraming.adepter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kamrulhasan3288.com.audiostraming.R;
import kamrulhasan3288.com.audiostraming.model.SongList;

/**
 * Created by kamrulhasan on 11/18/17.
 */

public class SongListAdepter extends RecyclerView.Adapter<SongListAdepter.SongViewHolder>{


    private onRecyclerViewItemClickListener mItemClickListener;
    private ArrayList<SongList> songLists = new ArrayList<>();
    private Context context;

    public SongListAdepter(ArrayList<SongList> songLists, Context context) {
        this.songLists = songLists;
        this.context = context;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        final SongList list = songLists.get(position);
        holder.songTitle.setText(list.getSongTitle());
        holder.lauout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null){
                    mItemClickListener.onItemClickListener(v,list.getSongTitle(),list.getSongUrl());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songLists.size();
    }

    public static class  SongViewHolder extends RecyclerView.ViewHolder{

        private TextView songTitle;
        private LinearLayout lauout;

        public SongViewHolder(View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.list_songName);
            lauout = itemView.findViewById(R.id.parent_layout_view);
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface onRecyclerViewItemClickListener {
        void onItemClickListener(View view, String songName,String songUrl);
    }
}
