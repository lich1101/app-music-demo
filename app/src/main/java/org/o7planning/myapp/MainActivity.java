package org.o7planning.myapp;



import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addButton, deleteButton, playButton, pauseButton, stopButton;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private List<String> mp3List;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private String currentMp3Path;
    private String selectedMp3Uri;  // Lưu URI của bài hát được chọn
    private int selectedPosition = -1;  // Lưu vị trí của bài hát được chọn

    private static final int PICK_MP3_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.add_button);
        deleteButton = findViewById(R.id.delete_button);
        playButton = findViewById(R.id.play_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);

        dbHelper = new DatabaseHelper(this);
        loadMp3List();

        // Thêm file MP3
        addButton.setOnClickListener(v -> openFileChooser());

        // Xử lý chọn bài hát trong danh sách
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedMp3Uri = dbHelper.getAllMp3Files().get(position);  // Lưu URI của bài hát
            selectedPosition = position;  // Lưu vị trí của bài hát
            Toast.makeText(MainActivity.this, "Đã chọn bài hát: " + mp3List.get(position), Toast.LENGTH_SHORT).show();
        });

        // Xóa bài hát được chọn
        deleteButton.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                dbHelper.deleteMp3File(selectedPosition);  // Xóa bài hát được chọn
                loadMp3List();
                selectedPosition = -1;  // Reset lựa chọn
                selectedMp3Uri = null;
                Toast.makeText(MainActivity.this, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Hãy chọn một bài hát để xóa", Toast.LENGTH_SHORT).show();
            }
        });

        // Phát nhạc của bài hát được chọn
        playButton.setOnClickListener(v -> {
            if (selectedMp3Uri != null && !isPlaying) {
                playMp3(selectedMp3Uri);  // Phát bài hát được chọn
            } else {
                Toast.makeText(MainActivity.this, "Hãy chọn một bài hát để phát", Toast.LENGTH_SHORT).show();
            }
        });

        // Tạm dừng nhạc
        pauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            }
        });

        // Dừng phát nhạc
        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                isPlaying = false;
            }
        });
    }

    // Mở trình chọn file để chọn file MP3
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/mpeg");  // Chỉ chọn file MP3
        startActivityForResult(intent, PICK_MP3_FILE);
    }

    // Xử lý file được chọn
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MP3_FILE && resultCode == RESULT_OK && data != null) {
            Uri selectedMp3Uri = data.getData();
            String fileName = getFileName(selectedMp3Uri);
            dbHelper.addMp3File(fileName, selectedMp3Uri.toString());
            loadMp3List();
        }
    }

    // Lấy tên file từ URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Load danh sách file MP3 từ cơ sở dữ liệu
    private void loadMp3List() {
        mp3List = dbHelper.getAllMp3Files();  // Lấy danh sách bài hát từ cơ sở dữ liệu
        SongAdapter adapter = new SongAdapter(this, mp3List, dbHelper);  // Sử dụng custom adapter
        listView.setAdapter(adapter);
    }

    // Phát file MP3
    private void playMp3(String uriString) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, Uri.parse(uriString));  // Phát từ URI
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            currentMp3Path = uriString;
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
