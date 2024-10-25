package com.example.whooweswho.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whooweswho.R;
import com.example.whooweswho.activities.JournalActivity;
import com.example.whooweswho.adapters.MembersAdapter;
import com.example.whooweswho.objects.Journal;
import com.example.whooweswho.objects.Member;
import com.example.whooweswho.objects.Transaction;

import java.util.ArrayList;
import java.util.Arrays;

public class MembersFragment<RecyclerView> extends Fragment
{
    View v;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    Journal journal;
    //ArrayList<Member> members = new ArrayList<Member>();

    public MembersFragment()
    {
        super(R.layout.fragment_members);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_members, container, false);
        journal = ((JournalActivity)getActivity()).getJournal();

        if (journal.numMembers == 0)
        {
            // add names for testing purposes
            addMember("Annika");
            addMember("Bradan");
            addMember("Chad");
            addMember("Daniel");
            addMember("Emily");
        }

        // Add new member
        Button add_member_btn = v.findViewById(R.id.add_member);
        add_member_btn.setOnClickListener(view -> {

            final AlertDialog alert = new AlertDialog.Builder(getContext())
                    .setView(v)
                    .setTitle("New Member")
                    .setPositiveButton("Add", null) //Set to null. We override the onclick
                    .setNegativeButton("Cancel", null)
                    .create();

            // Set an EditText view to get user input
            final EditText input = new EditText(getContext());
            input.setHint("Name");
            input.setMaxLines(1);
            input.setSingleLine();
            input.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(28) {} });
            FrameLayout dialog_container = new FrameLayout(getActivity());
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(32,32,32,32);
            input.setLayoutParams(params);
            dialog_container.addView(input);
            alert.setTitle("New Member");
            //alert.setMessage("Provide a name for your journal");
            alert.setView(dialog_container);

            alert.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button button = ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (input.getText().toString().matches(""))
                            {
                                Toast.makeText(getContext(),"Name cannot be blank",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                boolean duplicate = false;
                                for (int i=0; i<((JournalActivity)getActivity()).getJournal().members.size(); i++)
                                {
                                    if (input.getText().toString().matches(((JournalActivity)getActivity()).getJournal().members.get(i).member_name))
                                    {
                                        Toast.makeText(getContext(),"Duplicate names not allowed",Toast.LENGTH_SHORT).show();
                                        duplicate = true;
                                    }
                                }

                                if (!duplicate) {
                                    addMember(input.getText().toString());

                                    for (int i=0; i<((JournalActivity)getActivity()).getJournal().transactionList.size(); i++)
                                    {
                                        Transaction transaction = ((JournalActivity)getActivity()).getJournal().transactionList.get(i);
                                        Boolean[] temp = new Boolean[((JournalActivity)getActivity()).getJournal().numMembers];
                                        Arrays.fill(temp, false);
                                        System.arraycopy(transaction.clientsArray,0,temp,0,transaction.clientsArray.length);
                                        transaction.clientsArray = temp;
                                    }

                                    alert.dismiss();

                                    MembersAdapter membersAdapter = new MembersAdapter(((JournalActivity)getActivity()).getJournal().members);
                                    recyclerView = (androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.members_recycler_view);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    recyclerView.setAdapter(membersAdapter);

                                }
                            }
                        }
                    });
                }
            });

            alert.show();

        });

        MembersAdapter membersAdapter = new MembersAdapter(((JournalActivity)getActivity()).getJournal().members);
        recyclerView = (androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.members_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(membersAdapter);

        return v;
    }

    public void addMember(String name)
    {
        Member member = new Member(name);
        journal.members.add(member);
        journal.numMembers += 1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
}
