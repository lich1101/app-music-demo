package org.o7planning.myapp;



import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private List<String> songList;
    private DatabaseHelper dbHelper;

    public SongAdapter(Context context, List<String> songList, DatabaseHelper dbHelper) {
        this.context = context;
        this.songList = songList;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_song, parent, false);
        }

        // Lấy tên bài hát
        TextView songName = convertView.findViewById(R.id.song_name);
        songName.setText(songList.get(position));

        // Nút xóa bài hát
        Button deleteButton = convertView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            // Xóa bài hát khỏi cơ sở dữ liệu
            dbHelper.deleteMp3File(position);
            // Cập nhật lại danh sách
            songList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Đã xóa bài hát", Toast.LENGTH_SHORT).show();
        });

        // Xử lý khi nhấn vào tên bài hát để chuyển sang màn hình phát nhạc
        songName.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaySongActivity.class);
            intent.putExtra("SONG_PATH", dbHelper.getAllMp3Files().get(position));  // Truyền URI của bài hát qua intent
            context.startActivity(intent);
        });

        return convertView;
    }
}

