package com.example.final_project.AI;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecycler;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ChatAdapter adapter;
    private List<MessageModel> messageList = new ArrayList<>();
    private FirebaseFunctions mFunctions;

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbox);

        chatRecycler = findViewById(R.id.chatRecycler);
        inputMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.btnSend);

        mFunctions = FirebaseFunctions.getInstance("us-central1");

        adapter = new ChatAdapter(messageList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        chatRecycler.setLayoutManager(manager);
        chatRecycler.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String msg = inputMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                addMessage(msg, true); // User message
                inputMessage.setText("");
                sendMessageToGemini(msg);
            }
        });
    }

    private void addMessage(String msg, boolean isUser) {
        runOnUiThread(() -> {
            messageList.add(new MessageModel(msg, isUser));
            adapter.notifyItemInserted(messageList.size() - 1);
            chatRecycler.scrollToPosition(messageList.size() - 1);
        });
    }

    private void sendMessageToGemini(String userMessage) {
        Map<String, Object> data = new HashMap<>();
        data.put("prompt", userMessage);

        mFunctions
                .getHttpsCallable("mySecureFunction")
                .call(data)
                .addOnCompleteListener(new OnCompleteListener<HttpsCallableResult>() {
                    @Override
                    public void onComplete(@NonNull Task<HttpsCallableResult> task) {
                        if (task.isSuccessful()) {
                            try {
                                Object resultData = task.getResult().getData();
                                String reply;

                                if (resultData instanceof Map) {
                                    Map<String, Object> map = (Map<String, Object>) resultData;
                                    reply = (String) map.get("reply");
                                } else {
                                    reply = "Unknown response format from server.";
                                }

                                addMessage(reply, false);

                            } catch (Exception e) {
                                Log.e(TAG, "Parsing server response failed", e);
                                addMessage("Error parsing server response.", false);
                            }
                        } else {
                            Exception e = task.getException();
                            String errorMessage;
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                errorMessage = "Function Error: " + ffe.getCode().name() + " - " + ffe.getMessage();
                            } else {
                                errorMessage = "Request Failed: " + (e != null ? e.getMessage() : "Unknown network error");
                            }
                            Log.e(TAG, "Firebase Function call failed", e);
                            addMessage(errorMessage, false);
                        }
                    }
                });
    }
}
