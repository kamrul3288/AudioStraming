package kamrulhasan3288.com.audiostraming.activities;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import kamrulhasan3288.com.audiostraming.R;
import kamrulhasan3288.com.audiostraming.adepter.SongListAdepter;
import kamrulhasan3288.com.audiostraming.data.Utils;
import kamrulhasan3288.com.audiostraming.model.SongList;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnInfoListener {


    /**
     * ----xml instance------
     **/
    private RecyclerView recyclerView;
    private ImageView playButton,previousMusicButton,forwardMusicButton;
    private TextView songTitle;
    private SeekBar seekBar;
    private TextView currentDurationText,totalDurationText;


    /**
     * ------class component--------
     **/
    private SongListAdepter adepter;
    private ArrayList<SongList> songLists = new ArrayList<>();

    private boolean playPause;
    private MediaPlayer mediaPlayer;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler();
    private Runnable runnable;

    private int currentDuration,totalDuration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("PlayList");

        /**
         * ----initialize xml view---------------
         * */
        progressDialog = new ProgressDialog(MainActivity.this);
        currentDurationText = findViewById(R.id.currentDuration);
        totalDurationText = findViewById(R.id.totalDuration);
        seekBar = findViewById(R.id.main_activity_seekBar);
        playButton = findViewById(R.id.playMusicButton);
        songTitle = findViewById(R.id.main_activity_songName);
        previousMusicButton = findViewById(R.id.previousMusicButton);
        forwardMusicButton = findViewById(R.id.nextMusicButton);

        /**
         *---animate song title-------------------
         **/
        songTitle.setSelected(true);
        songTitle.setSingleLine(true);

        for (int i = 0; i < Utils.SONG_TITLE.length; i++) {
            SongList list = new SongList(Utils.SONG_TITLE[i], Utils.SONG_URL[i]);
            songLists.add(list);
        }


        /**
         *---configure recycler view
         * --load the adepter
         **/
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adepter = new SongListAdepter(songLists, getApplicationContext());
        recyclerView.setAdapter(adepter);
        adepter.notifyDataSetChanged();


        /**
         *-----configure the media player-----
         **/
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnInfoListener(this);



        /**
         *---load for play music---
         * --recyclerView click listener----
         **/
        adepter.setOnItemClickListener(new SongListAdepter.onRecyclerViewItemClickListener() {
            @Override
            public void onItemClickListener(View view, String songName, String songUrl) {

                /**
                 * ----play the music
                 **/
               if(!playPause){
                    songTitle.setText("Now Playing.."+songName+" From Fusion Bd, Powered by The Dhaka Digital");
                    new Player().execute(songUrl);
                    playButton.setImageResource(R.mipmap.ic_pause);
                    playPause = true;

                }else {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.reset();
                        songTitle.setText("Now Playing.."+songName+" From Fusion Bd, Powered by The Dhaka Digital");
                        new Player().execute(songUrl);
                        playButton.setImageResource(R.mipmap.ic_pause);
                    }
                }
            }

        });

        /**
         *-----configure play pause button---
         **/
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playPause){
                    songTitle.setText("Now Playing.."+Utils.SONG_TITLE[0]+" From Fusion Bd, Powered by The Dhaka Digital");
                    new Player().execute(Utils.SONG_URL[0]);
                    playButton.setImageResource(R.mipmap.ic_pause);
                    playPause = true;

                }else {
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        playButton.setImageResource(R.mipmap.ic_play);

                    }else {
                        mediaPlayer.start();
                        playButton.setImageResource(R.mipmap.ic_pause);
                    }
                }
            }

        });

        /**
         * --configure seekbar
         **/
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         *----music player previous button event----------
         **/
        previousMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(currentDuration-5000);
                }
            }
        });


        /**
         *----music player forward button event----------
         **/
        forwardMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(currentDuration+5000);
                }
            }
        });



    }


    /**
     * Method which updates the SeekBar primary progress by current song playing position
     */
    private void primarySeekBarProgressUpdater() {
        currentDuration = mediaPlayer.getCurrentPosition();
        currentDurationText.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long)currentDuration),
                TimeUnit.MILLISECONDS.toSeconds((long)currentDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)currentDuration))));
        seekBar.setProgress(mediaPlayer.getCurrentPosition()); // This math construction give a percentage of "was playing"/"song length"

        if (mediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                public void run() {
                    primarySeekBarProgressUpdater();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }


    /**
     *---detect the media player is buffering or not---------------
     **/
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                showProgressBar();
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (progressDialog.isShowing()){
                    progressDialog.cancel();
                }
                break;
        }
        return false;
    }

    /**
     *--------download and play music ----------
     * ------- work on background thread---------
     **/
    private class Player extends AsyncTask<String,Void,Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {

            Boolean prepared;
            try {
                mediaPlayer.setDataSource(urls[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playPause = false;
                        playButton.setImageResource(R.mipmap.ic_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            }catch (Exception e){
                Log.v("Error",e.getMessage());
                prepared = false;
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (progressDialog.isShowing()){
                progressDialog.cancel();
            }
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration());

            /**
             *----set total duration on text view ---
             **/
            totalDuration = mediaPlayer.getDuration();
            totalDurationText.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long)totalDuration),
                    TimeUnit.MILLISECONDS.toSeconds((long)totalDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)totalDuration))));

            primarySeekBarProgressUpdater();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
        }
    }

    /**
     * ---configure the dialog--------------
     * */
    public void showProgressBar(){
        progressDialog.setMessage("Buffering...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    /**
     * ---when music player is unVisible from user----------
     * */
    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            playButton.setImageResource(R.mipmap.ic_play);
        }
    }

    /**
     * ----when music player is visible---------
     * */
    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && playPause) {
            mediaPlayer.start();
            playButton.setImageResource(R.mipmap.ic_pause);
        }
    }

    /**
     *----when music player exit------------------
     **/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        handler.removeCallbacks(runnable);
    }
}
