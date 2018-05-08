package com.frodoczat.flashchatnewfirebase;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    private DatabaseReference mDatabaseReference;
    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        FirebaseApp.initializeApp(this);
        setupDisplayName();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        mAdapter = new ChatListAdapter(this, mDatabaseReference, mDisplayName);
        mChatListView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();
        // ====================
        // Obsluga androidowego ( klawiaturowego ) wyslij
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.register_form_finished || id == EditorInfo.IME_NULL) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });
        //======================
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void setupDisplayName(){
        SharedPreferences prefs = getSharedPreferences(RegisterActivity.CHAT_PREFS, MODE_PRIVATE);
        mDisplayName = prefs.getString(RegisterActivity.DISPLAY_NAME_KEY, null);
        if (mDisplayName == null){
            mDisplayName = "Anonymous";
        }
        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDisplayName = user.getDisplayName();*/
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input, mDisplayName);
            mDatabaseReference.child("message").push().setValue(chat); // ZAPISUJE wiadomosci do bazydanych
            mInputText.setText(""); // reset pola po wyslaniu
        }
    }

    // Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart(){
        super.onStart();

       // mChatListView.setSelection(mAdapter.getCount()-1);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.cleanup();
    }

}
