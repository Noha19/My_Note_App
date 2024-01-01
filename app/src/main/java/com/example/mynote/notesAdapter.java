package com.example.mynote;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class notesAdapter extends RecyclerView.Adapter<notesAdapter.MyViewHolder> {
    List<notes> listOfNotesInNotesAdapter = new ArrayList<>();


    //---------------notesAdapter's constractor--------------
    public notesAdapter(List<notes> notesList){
    this.listOfNotesInNotesAdapter =notesList;
    }//end of constractor


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textNote;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textNote=itemView.findViewById(R.id.note);
        }//end of constractor
    }//end of MyViewHolder



    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.note_list_row,parent,false);

        return new MyViewHolder(itemView);
    }//end of onCreateViewHolder



    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {
    notes note = listOfNotesInNotesAdapter.get(position);
    holder.textNote.setText(note.getTitle());
    }//endOfonBindViewHolder

    @Override
    public int getItemCount() {
         return listOfNotesInNotesAdapter.size();
    }//end of getItemCount


}//end adapter

