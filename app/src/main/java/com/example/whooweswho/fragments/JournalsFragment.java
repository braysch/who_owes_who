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

import com.example.whooweswho.R;
import com.example.whooweswho.activities.JournalActivity;

public class JournalsFragment extends Fragment
{
    View v;
    public JournalsFragment() {super(R.layout.fragment_journals);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_journals, container, false);

        // Create a new journal
        Button new_journal_btn = v.findViewById(R.id.new_journal_btn);
        new_journal_btn.setOnClickListener(view -> {

            final AlertDialog alert = new AlertDialog.Builder(getContext())
                    .setView(v)
                    .setTitle("New Journal")
                    .setPositiveButton("Create Journal", null) //Set to null. We override the onclick
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
            alert.setTitle("New Journal");
            alert.setMessage("Provide a name for your journal");
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
                                Toast.makeText(getContext(),"Journal name cannot be blank",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Intent intent = new Intent(getActivity(), JournalActivity.class);
                                intent.putExtra("JOURNAL_NAME", input.getText().toString());
                                startActivity(intent);
                                alert.dismiss();
                            }

                        }
                    });
                }
            });

            alert.show();

        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

}
