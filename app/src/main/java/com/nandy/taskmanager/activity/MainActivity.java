package com.nandy.taskmanager.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.nandy.taskmanager.Constants;
import com.nandy.taskmanager.R;
import com.nandy.taskmanager.SimpleOnPageChangedListener;
import com.nandy.taskmanager.adapter.TabsPagerAdapter;
import com.nandy.taskmanager.mvp.model.CreateTaskModel;
import com.nandy.taskmanager.mvp.model.DummyDataModel;
import com.nandy.taskmanager.mvp.model.TaskRecordsModel;
import com.nandy.taskmanager.mvp.presenter.MainPresenter;
import com.nandy.taskmanager.ui.fragment.StatisticsFragment;
import com.nandy.taskmanager.ui.fragment.TasksFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.container)
    ViewPager mViewPager;
    @BindView(R.id.btn_create_task)
    FloatingActionButton mFab;

    private TabsPagerAdapter mTabsPagerAdapter;
    private Snackbar mExitSnackbar;

    private MainPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.toolbar));

        mPresenter = new MainPresenter();
        mPresenter.setDummyDataMode(new DummyDataModel(getApplicationContext()));
        mPresenter.setRecordsModel(new TaskRecordsModel(getApplicationContext()));

        mTabsPagerAdapter = new TabsPagerAdapter(
                getSupportFragmentManager(),
                new Fragment[]{TasksFragment.newInstance(getApplicationContext()), StatisticsFragment.newInstance(getApplicationContext())});

        mViewPager.setAdapter(mTabsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.addOnPageChangeListener(new SimpleOnPageChangedListener() {
            @Override
            public void onPageChanged(int position) {
                if (position == 0) {
                    mFab.show();
                }else {
                    mFab.hide();
                }
            }
        });
    }

    @OnClick(R.id.btn_create_task)
    void onCreateTaskClick() {
        Intent intent = new Intent(getApplicationContext(), CreateTaskActivity.class);
        intent.putExtra("mode", CreateTaskModel.MODE_CREATE);
        startActivityForResult(intent, Constants.REQUEST_CREATE_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_backup:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            case R.id.action_create_task:
                onCreateTaskClick();
                return true;

            case R.id.action_fill_list:
                mPresenter.generateDummyData();
                return true;

            case R.id.action_clear_all:
                onClearAllOptionSelected();
                return true;

            case R.id.action_exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void onClearAllOptionSelected() {

        new AlertDialog.Builder(this)
                .setMessage(R.string.clear_all_data)
                .setPositiveButton(R.string.clear_all, (dialogInterface, i) -> mPresenter.clearAllTasks())
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {

        if (mExitSnackbar == null) {
            mExitSnackbar = createExitSnackBar();
            mExitSnackbar.show();
        } else if (!mExitSnackbar.isShown()) {
            mExitSnackbar.show();
        } else {
            super.onBackPressed();
        }
    }

    private Snackbar createExitSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), R.string.exit_app_message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryColor));
        TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondaryTextColor));

        return snackbar;
    }

}