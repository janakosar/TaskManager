package com.nandy.taskmanager.mvp.presenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.mvp.model.CreateBackupModel;
import com.nandy.taskmanager.mvp.model.DataImportModel;
import com.nandy.taskmanager.mvp.model.GoogleDriveConnectionModel;
import com.nandy.taskmanager.mvp.model.RestoreFromBackupModel;
import com.nandy.taskmanager.mvp.view.MainPreferencesView;

import java.io.File;
import java.io.IOException;

/**
 * Created by yana on 24.01.18.
 */

public class MainPreferencesPresenter  {

    private final MainPreferencesView mView;
    private CreateBackupModel mCreateBackupModel;
    private RestoreFromBackupModel mRestoreDataModel;
    private GoogleDriveConnectionModel mGoogleDriveConnectionModel;
    private DataImportModel mDataImportModel;

    public MainPreferencesPresenter(MainPreferencesView view) {
        mView = view;
    }

    public void start() {
        if (mCreateBackupModel.isGoogleClient()) {
            mCreateBackupModel.requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> mView.setRestoreBackupPreferenceEnabled(metadataBuffer.getCount() > 0));
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGoogleDriveConnectionModel.onActivityResult(requestCode, resultCode, data);
    }

    public void createBackup() {

        mGoogleDriveConnectionModel.signIn(driveResourceClient -> {
            mCreateBackupModel.setDriveResourceClient(driveResourceClient);
            mRestoreDataModel.setDriveResourceClient(driveResourceClient);

            mView.showProgressDialog(R.string.creating_backup);

            mCreateBackupModel.
                    requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> {

                        if (metadataBuffer.getCount() > 0) {
                            mCreateBackupModel.removeExistingBackupFiles(metadataBuffer);
                        }
                        updateOrCreateBackup();

                    })
                    .addOnFailureListener(this::onError);
        });

    }


    private void onError(Exception e) {
        e.printStackTrace();
        mView.cancelProgressDialog();
        mView.showMessage(e.getMessage());
    }

    private void updateOrCreateBackup() {
        mCreateBackupModel.createAndUploadBackup()
                .addOnSuccessListener(driveFile -> {
                    mView.cancelProgressDialog();
                    mView.showMessage(R.string.backup_created_successfully);
                    mView.setRestoreBackupPreferenceEnabled(true);
                })
                .addOnFailureListener(this::onError);

    }


    public void restoreFromBackup() {

        mGoogleDriveConnectionModel.signIn(driveResourceClient -> {
            mCreateBackupModel.setDriveResourceClient(driveResourceClient);
            mRestoreDataModel.setDriveResourceClient(driveResourceClient);

            mView.showProgressDialog(R.string.restoring_data);
            mRestoreDataModel.requestExistingBackupFiles()
                    .addOnSuccessListener(metadataBuffer -> {

                        Log.d("BACKUP_", "restore: " + metadataBuffer.getCount());
                        if (metadataBuffer.getCount() > 0) {
                            downloadAndRestoreBackup(metadataBuffer.get(0).getDriveId().asDriveFile());
                        } else {
                            mView.cancelProgressDialog();
                            mView.showMessage(R.string.no_backups_found);
                        }
                    })
                    .addOnFailureListener(this::onError);
        });

    }

    private void downloadAndRestoreBackup(DriveFile driveFile) {

        mRestoreDataModel.openFile(driveFile, new OpenFileCallback() {
            @Override
            public void onProgress(long l, long l1) {
                //nothing to do
            }

            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public void onContents(@NonNull DriveContents driveContents) {
                try {
                    mRestoreDataModel.restoreBackup(driveContents);
                    File dbBackupFile = mRestoreDataModel.getBackupDbFile();
                    mDataImportModel.importData(dbBackupFile);
                    dbBackupFile.delete();
                    mView.cancelProgressDialog();
                    mView.showMessage(R.string.data_restored);

                } catch (IOException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                MainPreferencesPresenter.this.onError(e);
            }
        });
    }

    public void setCreateBackupModel(CreateBackupModel mCreateBackupModel) {
        this.mCreateBackupModel = mCreateBackupModel;
    }

    public void setRestoreDataModel(RestoreFromBackupModel mRestoreDataModel) {
        this.mRestoreDataModel = mRestoreDataModel;
    }

    public void setGoogleDriveConnectionModel(GoogleDriveConnectionModel mGoogleDriveConnectionModel) {
        this.mGoogleDriveConnectionModel = mGoogleDriveConnectionModel;
    }

    public void setDataImportModel(DataImportModel mDataImportModel) {
        this.mDataImportModel = mDataImportModel;
    }
}