package com.example.regi.zass.Widget;

import java.util.ArrayList;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.example.regi.zass.Model.Note;
import com.example.regi.zass.R;
import com.example.regi.zass.Utils.Constants;

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<Note> listItemList = new ArrayList<>();
    private Context context = null;

    public ListProvider(Context context, Intent intent) {
        this.context = context;

        Cursor cursor = context.getContentResolver().query(Constants.CONTENT_URI, null, null, null, null);
        try {
            while (cursor.moveToNext()) {
                Note note = new Note();
                note.setReadingTitle(cursor.getString(cursor.getColumnIndex(Constants.PROVIDER_READINGTITLE)));
                note.setDateCreated(cursor.getString(cursor.getColumnIndex(Constants.PROVIDER_DATECREATED)));
                note.setTags(cursor.getString(cursor.getColumnIndex(Constants.PROVIDER_TAGS)));
                listItemList.add(note);
            }
        } finally {
            cursor.close();
        }

    }

    @Override
    public int getCount() {
        return listItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget_row);
        Note note = listItemList.get(position);
        remoteView.setTextViewText(R.id.heading, note.getReadingTitle());
        remoteView.setTextViewText(R.id.content, note.getTags());
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {}

    @Override
    public void onDestroy() {}

}
