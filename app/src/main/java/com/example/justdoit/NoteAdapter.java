package com.example.justdoit;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {

    private OnItemClickListener listener;
    private OnItemCheckClickListener checkListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static DateFormat df = new SimpleDateFormat("EEE d MMM yy HH:mm", Locale.ENGLISH);

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())  &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getPriority() == newItem.getPriority() &&
                    oldItem.getDueAt().equals(newItem.getDueAt()) &&
                    oldItem.isCompleted() == newItem.isCompleted() &&
                    oldItem.isReminder() == newItem.isReminder() &&
                    oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getAttachment().equals(newItem.getAttachment());
        }
    };

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        df.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        int[] colors = {R.color.priority_1, R.color.priority_2, R.color.priority_3, R.color.priority_4, R.color.priority_5};

        Note currentNote = getItem(position);
        holder.textViewTitle.setText(currentNote.getTitle());
        holder.textViewCategory.setText(currentNote.getCategory());
        holder.textViewDueAt.setText(df.format(currentNote.getDueAt()));
        holder.checkBoxCompleted.setChecked(currentNote.isCompleted());
        holder.viewPriority.setBackgroundResource(colors[currentNote.getPriority() - 1]);
        holder.attachmentInd.setVisibility(currentNote.getAttachment().equals("") ? View.INVISIBLE : View.VISIBLE);
        holder.notificationInd.setVisibility(currentNote.isReminder() ? View.VISIBLE : View.INVISIBLE);

        Date dueAt = currentNote.getDueAt();
        long now = System.currentTimeMillis() - 1000;
        if (dueAt.getTime() <= now) {
            holder.taskCard.setBackgroundResource(R.color.colorHighlight);
            holder.textViewDueAt.setTextColor(Color.RED);
            holder.textViewDueAt.setAlpha(1f);
        } else {
            holder.taskCard.setBackgroundResource(0);
            holder.textViewDueAt.setTextColor(Color.BLACK);
            holder.textViewDueAt.setAlpha(0.54f);
        }

        if (currentNote.isCompleted()) {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewCategory.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewDueAt.setPaintFlags(holder.textViewTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewTitle.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewCategory.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewDueAt.setPaintFlags(holder.textViewTitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        private LinearLayout taskCard;
        private TextView textViewTitle;
        private TextView textViewCategory;
        private TextView textViewDueAt;
        private CheckBox checkBoxCompleted;
        private View viewPriority;
        private ImageView attachmentInd;
        private ImageView notificationInd;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            taskCard = itemView.findViewById(R.id.task_card);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewCategory = itemView.findViewById(R.id.text_view_category);
            textViewDueAt = itemView.findViewById(R.id.text_view_duedate);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            viewPriority = itemView.findViewById(R.id.view_priority);
            attachmentInd = itemView.findViewById(R.id.attachment_ind);
            notificationInd = itemView.findViewById(R.id.notification_ind);

            checkBoxCompleted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (checkListener != null && position != RecyclerView.NO_POSITION) {
                        checkListener.onCheckClick(getItem(position));
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemCheckClickListener {
        void onCheckClick(Note note);
    }

    public void setOnCheckClickListener(OnItemCheckClickListener listener) {
        this.checkListener = listener;
    }
}

