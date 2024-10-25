package com.example.whooweswho.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whooweswho.R;
import com.example.whooweswho.activities.JournalActivity;

public class InfoFragment extends Fragment
{
    View v;

    public InfoFragment() {super(R.layout.fragment_info);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_info, container, false);

        String str_journal_name = ((JournalActivity)getActivity()).getJournal().journalName;
        TextView journal_name = v.findViewById(R.id.info_journal_name);
        journal_name.setText(str_journal_name);

        int numMembers = ((JournalActivity)getActivity()).getJournal().numMembers;
        TextView num_members = v.findViewById(R.id.num_member_info);
        num_members.setText("Number of party members: "+String.valueOf(numMembers));


        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
}
