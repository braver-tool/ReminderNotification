/*
 *
 *  * Created by https://github.com/braver-tool on 11/09/20, 03:30 PM
 *  * Copyright (c) 2022 . All rights reserved.
 *  * Last modified 05/04/22, 11:00 AM
 *
 */

package com.android.mysimplecalendar.adapter;

import static com.android.mysimplecalendar.utils.AppUtils.ACTION_EDIT_REMINDER;
import static com.android.mysimplecalendar.utils.AppUtils.ACTION_OK_BUTTON_FROM_ALERT_POPUP;
import static com.android.mysimplecalendar.utils.AppUtils.DELETE_REMINDER_ALERT;
import static com.android.mysimplecalendar.utils.AppUtils.FAVORITE_REMINDER_ALERT;
import static com.android.mysimplecalendar.utils.AppUtils.NOT_EDITABLE_REMINDER_ALERT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mysimplecalendar.R;
import com.android.mysimplecalendar.listener.DataListener;
import com.android.mysimplecalendar.localdb.MCNotification;
import com.android.mysimplecalendar.localdb.NotificationRepository;
import com.android.mysimplecalendar.utils.AppUtils;

import java.util.List;


public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<MCNotification> notificationModelList;
    private final DataListener dataListener;
    private int deletedPos = -1;
    private int favPos = -1;
    private final NotificationRepository notificationRepository;


    public RemindersAdapter(Context mContext, NotificationRepository repository, DataListener listener, List<MCNotification> notificationModels) {
        this.mContext = mContext;
        this.notificationModelList = notificationModels;
        this.dataListener = listener;
        this.notificationRepository = repository;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary  calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_my_reminders, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MCNotification notificationModel = notificationModelList.get(position);
        if (notificationModel != null) {
            holder.myReminderTimeTextView.setText(notificationModel.getReminderTime());
            holder.reminderTitle.setText(notificationModel.getReminderTitle());
            holder.aboutReminderTextView.setText(notificationModel.getReminderDetails());
            holder.editReminderImageView.setImageResource(!notificationModel.isEditable() ? R.drawable.ic_edit_red : R.drawable.ic_edit_grey);
            holder.addToFavReminderImageView.setImageResource(notificationModel.isFavorite() ? R.drawable.ic_favorite_red : R.drawable.ic_favorite_border_red);
            holder.deleteReminderImageView.setImageResource(notificationModel.isDeletable() ? R.drawable.ic_delete_red : R.drawable.ic_delete_grey);
            holder.editReminderImageView.setTag(position);
            holder.addToFavReminderImageView.setTag(position);
            holder.deleteReminderImageView.setTag(position);
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DataListener {
        private final TextView myReminderTimeTextView;
        private final TextView reminderTitle;
        private final TextView aboutReminderTextView;
        private final ImageView editReminderImageView;
        private final ImageView addToFavReminderImageView;
        private final ImageView deleteReminderImageView;


        public MyViewHolder(View v) {
            super(v);
            myReminderTimeTextView = v.findViewById(R.id.myReminderTimeTextView);
            reminderTitle = v.findViewById(R.id.reminderTitle);
            aboutReminderTextView = v.findViewById(R.id.aboutReminderTextView);
            editReminderImageView = v.findViewById(R.id.editReminderImageView);
            deleteReminderImageView = v.findViewById(R.id.deleteReminderImageView);
            addToFavReminderImageView = v.findViewById(R.id.addToFavReminderImageView);
            editReminderImageView.setOnClickListener(this);
            deleteReminderImageView.setOnClickListener(this);
            addToFavReminderImageView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.editReminderImageView) {
                int editPos = (int) v.getTag();
                boolean isEditable = notificationModelList.get(editPos).isEditable();
                if (isEditable) {
                    dataListener.sendData(ACTION_EDIT_REMINDER, notificationModelList.get(editPos).getID());
                } else {
                    AppUtils.showAlertMsgDialog(NOT_EDITABLE_REMINDER_ALERT, mContext);
                }
            } else if (id == R.id.deleteReminderImageView) {
                deletedPos = (int) v.getTag();
                favPos = -1;
                AppUtils.showAlertDialogWidTwoWidget(mContext, this, DELETE_REMINDER_ALERT);
            } else if (id == R.id.addToFavReminderImageView) {
                favPos = (int) v.getTag();
                boolean isFavorite = notificationModelList.get(favPos).isFavorite();
                if (!isFavorite) {
                    deletedPos = -1;
                    AppUtils.showAlertDialogWidTwoWidget(mContext, this, FAVORITE_REMINDER_ALERT);
                } else {
                    updateFavInLocal(false);
                    favViewChange();
                }
            }
        }

        @Override
        public void sendData(int action, Object data) {
            if (data != null) {
                if (action == ACTION_OK_BUTTON_FROM_ALERT_POPUP) {
                    if (deletedPos != -1 && favPos == -1) {
                        notificationModelList.remove(deletedPos);
                        notifyDataSetChanged();
                        notificationRepository.deleteNotifications(notificationModelList.get(deletedPos).getID());
                    } else {
                        favViewChange();
                        updateFavInLocal(true);
                    }
                }
            }
        }

        private void favViewChange() {
            boolean isFavorite = notificationModelList.get(favPos).isFavorite();
            notificationModelList.get(favPos).setFavorite(!isFavorite);
            notifyDataSetChanged();
        }

        private void updateFavInLocal(boolean isFav) {
            notificationRepository.updateIsFavParamNotifications(notificationModelList.get(favPos).getID(), isFav);
        }
    }
}