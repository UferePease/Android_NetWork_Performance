package com.android.netperf_new;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.netperf_new.adapter.LogItemRecyclerAdapter;
import com.android.netperf_new.model.LogItem;
import com.android.netperf_new.utils.Config;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Handler;

public class YouTubeActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{
    public Activity activity;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;

    public LogItemRecyclerAdapter logAdapter;
    public RecyclerView logRecyclerView;

    EditText urlEntryView;

    public static ArrayList<LogItem> items;

    private YouTubePlayer player;

    public long videoInterval = 300000;

    public boolean isRunning;

    public Button loadButton;
    public Button resumeButton;
    public Button stopButton;
    public Button resetButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);

        activity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        urlEntryView = (EditText) findViewById(R.id.urlentryview);


        items = new ArrayList<LogItem>();

        logRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        logRecyclerView.setHasFixedSize(true);

        logAdapter = new LogItemRecyclerAdapter(this, items);
        //attach the adapter to the recyclerview to populate items
        logRecyclerView.setAdapter(logAdapter);

        //set layout manager to position the items
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        logRecyclerView.setLayoutManager(layoutManager);

        loadButton = (Button) findViewById(R.id.loadBtn);
        stopButton = (Button) findViewById(R.id.stopBtn);
        resumeButton = (Button) findViewById(R.id.resumeBtn);
        resetButton = (Button) findViewById(R.id.resetBtn);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                int skipToSecs = Integer.valueOf(loadButton.getText().toString());
//                player.seekToMillis(skipToSecs * 1000);

                loadPlayVideo(urlEntryView.getText().toString());
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeRecording();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {

        this.player = player;

        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {
//            player.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void loadPlayVideo(String videoUrl){
        if (!videoUrl.equals("")){
            if (videoUrl.contains("=")){
                isRunning = true;

                String[] splitArray = videoUrl.split("=");
                player.cueVideo(splitArray[1]);

            }
        }else {

            AlertDialog alertDialog = new AlertDialog.Builder(YouTubeActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("No URL has been entered. You must enter a valid YouTube video URL");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    private void stopRecording(){
        if (isRunning){
            isRunning = false;
            player.pause();
            stopButton.setEnabled(false);
            resumeButton.setEnabled(true);
            Toast.makeText(this, "Video looping has been stopped", Toast.LENGTH_SHORT).show();
        }

    }

    private void resumeRecording(){
        if (!isRunning){
            isRunning = true;
            player.play();
            resumeButton.setEnabled(false);
            stopButton.setEnabled(true);
            Toast.makeText(this, "Video looping has resumed", Toast.LENGTH_SHORT).show();
        }
    }

    public void resetAll(){

        AlertDialog.Builder builder = new AlertDialog.Builder(YouTubeActivity.this);
        builder.setTitle("WARNING!!!");
        builder.setMessage("You are about to reset. All logs will be erased. \nDo you want to continue?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                items = new ArrayList<LogItem>();

                logAdapter = new LogItemRecyclerAdapter(activity, items);
                //attach the adapter to the recyclerview to populate items
                logRecyclerView.setAdapter(logAdapter);

                if (!urlEntryView.getText().toString().equals("")){
                    if (urlEntryView.getText().toString().contains("=")){
                        isRunning = true;

                        String[] splitArray = urlEntryView.getText().toString().split("=");
                        player.cueVideo(splitArray[1]);

                    }
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();



    }

    public void notifyToStartVideo(){
        loadPlayVideo(urlEntryView.getText().toString());
    }





    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            showMessage("Playing");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            showMessage("Stopped");
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        long startTime;
        long doneTime;
        long delay;
        String startTimeEntry;




        @Override
        public void onLoading() {
            showMessage("loading");
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            showMessage("loaded");
            player.play();
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            startTime = System.currentTimeMillis();


            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Calendar calobj = Calendar.getInstance();

            startTimeEntry = df.format(calobj.getTime()).substring(8);

            //newLogItem.setLogTime(startTimeEntry);
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
            doneTime = System.currentTimeMillis();
            showMessage("ended");
            // Called when the video reaches its end.

            delay = doneTime - startTime;

            items.add(new LogItem(startTimeEntry, (delay / 1000) + "secs"));

            //update the recyclerview
//            logAdapter.notifyItemInserted(items.size() - 1);

            logAdapter = new LogItemRecyclerAdapter(activity, items);
            //attach the adapter to the recyclerview to populate items
            logRecyclerView.setAdapter(logAdapter);

//            VideoCycleTask waitTask = new VideoCycleTask();
//            new Thread(waitTask).start();

            long restartingIn = (startTime + videoInterval) - doneTime;

            //wait for somtime before reloading the video
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isRunning){
                        notifyToStartVideo();
                    }
                }
            }, restartingIn);

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }
}
