package com.nandy.taskmanager.mvp.contract;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;

import com.theartofdev.edmodo.cropper.CropImage;

/**
 * Created by yana on 24.01.18.
 */

public interface CreateTaskContract {

    interface Presenter extends org.kaerdan.presenterretainer.Presenter<View> {

        void saveChanges(String title, String description);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void clearLocation();

        void setStartDate();

        void setStartTime();

        boolean onDurationSelected(int optionId);

        boolean onRepeatPeriodSelected(int optionId);

        void saveInstanceState(Bundle outState, String title, String description);

        void restoreInstanceState(Bundle savedInstanceState);
    }

    interface View extends org.kaerdan.presenterretainer.Presenter.View {

        void setTitleError(@StringRes int textResId);

        void setCommentError(@StringRes int textResId);

        void clearLocation();

        void displayStartDate(String date);

        void displayStartTime(String time);

        void setStartTimeVisible(boolean visible);

        void setDuration(int duration, @StringRes int textResId);

        void setRepeatPeriod(@StringRes int textResId);

        void displayLocation(String location);

        void displayImage(String imagePath);

        void setDescription(String description);

        void setTitle(String title);

        void startCropActivity(CropImage.ActivityBuilder activityBuilder);

        void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, int year, int month, int day);

        void showTimePickerDialog(TimePickerDialog.OnTimeSetListener onTimeSetListener, int hour, int minute);

        void showMessage(@StringRes int textResId);

        void showMessage(String message);

        void finishWithResult(int resultCode, Intent data);
    }
}