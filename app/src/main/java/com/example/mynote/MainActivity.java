package com.example.mynote;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



import java.util.List;

public class MainActivity extends AppCompatActivity {

 RecyclerView recyclerView;
 static List <notes> ListOfNotesInMainActivity = new ArrayList<>();
 notesAdapter  myAdapter = new notesAdapter(ListOfNotesInMainActivity);
    TextView noNotesView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_addNote =  findViewById(R.id.button);
        recyclerView = findViewById(R.id.recyclerView);


//----------------------------setAdapter-------------------------------------------------------
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(myLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
//------------------Load data from SharedPreferences------------------
        ListOfNotesInMainActivity= loadData();


//----------------------------how to reverse data-----------------------------------
        myAdapter = new notesAdapter(ListOfNotesInMainActivity); // Initialize adapter with loaded data
        recyclerView.setAdapter(myAdapter);


//----------------when i click the button of add note------------------
        btn_addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });






//---------------when i click on on of items of the recyclerview ----------------
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //-------the next two lines to test the click function------------
                // notes n = ListOfNotesInMainActivity.get(position);
                // Toast.makeText(getApplicationContext(),"selected item is :" +n.getTitle(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));





    }//end of on create




    protected void onDestroy() {
        super.onDestroy();
        saveData(); // Save data before destroying the activity
    }

    //--------------------function of deleting note and set the modification in shared preferences-----------------
    private void deleteNote(int position) {
        ListOfNotesInMainActivity.remove(position);
        myAdapter.notifyItemRemoved(position);
        saveData();

    }
    //-------------------function of updating note and set the modification in shared preferences----------------------------------------------
    private void updateNote(String note, int position) {
        notes n = ListOfNotesInMainActivity.get(position);
        // updating note text
        n.setTitle(note);
        ListOfNotesInMainActivity.set(position, n);
        myAdapter.notifyItemChanged(position);
        saveData();
    }

    //--------------------function of creating new note and add it to shared preferences-----------------
    private void createNote(String note) {

        if (note!=null) {
            ListOfNotesInMainActivity.add(0,new notes(note));
            // refreshing the list
            myAdapter.notifyDataSetChanged();
            saveData();
        }
    }
    //---------------function of loading notes and send it to ListOfNotesInMainActivity----------------
    public List<notes> loadData() {
        SharedPreferences sp = getSharedPreferences("SHARED_PREFS_FILE", MODE_PRIVATE);
        Gson gson = new Gson();
        // Get the saved notes JSON string from Shared Preferences
        String jsonString = sp.getString("notes", null);

        if (jsonString != null) {
            // Convert JSON string back to List of notes using Gson
            Type type = new TypeToken<ArrayList<notes>>() {}.getType();
            ArrayList<notes> loadedNotes= gson.fromJson(jsonString, type);

            return loadedNotes;
        } else {
            return new ArrayList<>();
        }
    }

//-------------------function of creating sharedpreferences and save notes in it---------------------------------------------------------
private void saveData() {
    SharedPreferences sp = getSharedPreferences("SHARED_PREFS_FILE", MODE_PRIVATE);
    SharedPreferences.Editor editor = sp.edit();

    // Convert the ListOfNotesInMainActivity to json using Gson
    Gson gson = new Gson();
    String json = gson.toJson(ListOfNotesInMainActivity);

    // Save the json string in SharedPreferences
    editor.putString("notes", json);
    editor.apply();
}

    //--------------------the dialog of deleting or editing notes---------------------------------------------------------
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if the answer is edit with index 0 in the array
                if (which == 0) {
                    showNoteDialog(true, ListOfNotesInMainActivity.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }//end of showActionDialog


    //------------------the dialog of update old note or create new note-------------------
    private void showNoteDialog(final boolean shouldUpdate, final notes note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.edittorOfNote);
        TextView dialogTitle = view.findViewById(R.id.DialogeTitle);
        dialogTitle.setText
                (!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getTitle());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(inputNote.getText().toString(), position);
                }
                else {
                    // create new note
                    createNote(inputNote.getText().toString());
                }
            }



        });
    }//end of showNoteAnalog




}//end of activity
