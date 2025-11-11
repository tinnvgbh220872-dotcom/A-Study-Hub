package com.example.final_project.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.final_project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> fileList;
    private ArrayList<String> linkList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_activity_customer);

        listView = findViewById(R.id.listView);
        fileList = new ArrayList<>();
        linkList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fileList.clear();
                linkList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String filename = snapshot.child("filename").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String publishedDate = snapshot.child("publishedDate").getValue(String.class);
                    String status = snapshot.child("status").getValue(String.class);
                    String downloadUrl = snapshot.child("downloadUrl").getValue(String.class);

                    if (filename != null && email != null && publishedDate != null && status != null) {
                        fileList.add(
                                "📄 File: " + filename +
                                        "\n👤 User: " + email +
                                        "\n🕒 Date: " + publishedDate +
                                        "\n📌 Status: " + status +
                                        (downloadUrl != null ? "\n🔗 Tap to open" : "")
                        );
                        linkList.add(downloadUrl != null ? downloadUrl : "");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String link = linkList.get(position);
            if (link != null && !link.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(link));
                startActivity(intent);
            }
        });
    }
}
