package com.example.whooweswho.activities;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.whooweswho.R;
import com.example.whooweswho.fragments.InfoFragment;
import com.example.whooweswho.fragments.JournalsFragment;
import com.example.whooweswho.fragments.MembersFragment;
import com.example.whooweswho.fragments.ResultsFragment;
import com.example.whooweswho.fragments.TransactionsFragment;
import com.example.whooweswho.objects.Journal;
import com.example.whooweswho.objects.Member;
import com.example.whooweswho.objects.Transaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class JournalActivity extends AppCompatActivity {

    Journal journal = new Journal();

    public Journal getJournal()
    {
        return journal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        journal.members = new ArrayList<Member>();
        journal.numMembers = 0;
        journal.transactionList = new ArrayList<Transaction>();

        // Get journal name from dialog box
        Intent intent = getIntent();
        journal.journalName = intent.getStringExtra("JOURNAL_NAME");

        // Info is the default fragment
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.journal_fragment_container, InfoFragment.class, null)
                .commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // Navigate to different fragments when items are selected
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);

            if (menuItem.getItemId() == R.id.info_item)
            {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.journal_fragment_container, InfoFragment.class, null)
                        .commit();
            }

            if (menuItem.getItemId() == R.id.members_item)
            {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.journal_fragment_container, MembersFragment.class, null)
                        .commit();
            }

            if (menuItem.getItemId() == R.id.transactions_item)
            {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.journal_fragment_container, TransactionsFragment.class, null)
                        .commit();
            }

            if (menuItem.getItemId() == R.id.results_item)
            {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.journal_fragment_container, ResultsFragment.class, null)
                        .commit();
            }

            return true;
        });
    }
}
