package org.o7planning.myapp;



import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class PlaySongActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private String songPath;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        // Lấy đường dẫn bài hát từ intent
        songPath = getIntent().getStringExtra("SONG_PATH");

        // Hiển thị tên bài hát
        TextView songNameText = findViewById(R.id.song_name_text);
        songNameText.setText(songPath);

        // Nút điều khiển phát nhạc
        Button playButton = findViewById(R.id.play_button);
        Button pauseButton = findViewById(R.id.pause_button);
        Button stopButton = findViewById(R.id.stop_button);

        // Phát nhạc
        playButton.setOnClickListener(v -> {
            if (!isPlaying) {
                playMp3(songPath);
            }
        });

        // Tạm dừng nhạc
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            }
        });

        // Dừng nhạc
        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                isPlaying = false;
            }
        });
    }

    private void playMp3(String uriString) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(uriString));
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}

