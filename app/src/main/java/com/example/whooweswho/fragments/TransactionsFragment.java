package com.example.whooweswho.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whooweswho.R;
import com.example.whooweswho.activities.JournalActivity;
import com.example.whooweswho.objects.Journal;
import com.example.whooweswho.objects.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class TransactionsFragment extends Fragment
{
    View v;
    String[] memberNames;
    GridLayout gridLayout;
    Journal journal;

    public TransactionsFragment() {super(R.layout.fragment_transactions);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_transactions, container, false);

        journal = ((JournalActivity)getActivity()).getJournal();

        if (journal.transactionList.size() == 0)
        {
            createTransaction("Annika","service","4", new Boolean[] {false, true, false, false, false});
            createTransaction("Bradan","service","3", new Boolean[] {false, false, true, false, false});
            createTransaction("Bradan","service","5", new Boolean[] {false, false, false, true, false});
            createTransaction("Chad","service","2", new Boolean[] {true, false, false, false, false});
            createTransaction("Daniel","service","2", new Boolean[] {false, false, false, false, true});
            createTransaction("Emily","service","4", new Boolean[] {false, false, true, false, false});
        }

        memberNames = new String[((JournalActivity)getActivity()).getJournal().members.size()+1];
        memberNames[0] = "";
        for (int j=1; j<((JournalActivity)getActivity()).getJournal().members.size()+1; j++)
        {
            memberNames[j] = ((JournalActivity)getActivity()).getJournal().members.get(j-1).member_name;
        }

        gridLayout = new GridLayout(getContext());

        int numCol = 4 + memberNames.length - 1;
        gridLayout.setColumnCount(numCol);

        TextView textView;
        EditText editText;
        EditText costTest;

        textView = new TextView(getContext());
        textView.setText("Provider");
        gridLayout.addView(textView);

        textView = new TextView(getContext());
        textView.setText("Service");
        gridLayout.addView(textView);

        textView = new TextView(getContext());
        textView.setText("Cost");
        gridLayout.addView(textView);

        for (int i = 1; i < memberNames.length; i++)
        {
            textView = new TextView(getContext());
            textView.setText(memberNames[i]);
            textView.setRotation(-45);
            gridLayout.addView(textView);
        }

        textView = new TextView(getContext());
        textView.setText("Cost Per Person");
        gridLayout.addView(textView);

        for (int i=0; i<((JournalActivity)getActivity()).getJournal().transactionList.size(); i++)
        {
            createTransaction(((JournalActivity)getActivity()).getJournal().transactionList.get(i));
        }

        if (journal.transactionList.size() == 0)
        {
            createBlankTransaction();
        }

        Button new_transaction_btn = v.findViewById(R.id.new_transaction);
        new_transaction_btn.setOnClickListener(view -> {
            // Create a new transaction
            createBlankTransaction();
        });

        Button saveBtn = v.findViewById(R.id.save_button);
        saveBtn.setOnClickListener(view -> {
            // save the data to the transaction list
            for (int i=numCol; i<gridLayout.getChildCount(); i+=numCol) // for every cell
            {
                Transaction transactionData = journal.transactionList.get((i/numCol)-1);
                transactionData.provider = ((Spinner) gridLayout.getChildAt(i)).getSelectedItem().toString();
                transactionData.service = ((TextView) gridLayout.getChildAt(i+1)).getText().toString();
                transactionData.cost = ((TextView) gridLayout.getChildAt(i+2)).getText().toString();

                int numClients = 0;
                for (int k=0; k<journal.members.size(); k++)
                {
                    transactionData.clientsArray[k] = ((CheckBox) gridLayout.getChildAt(i+3+k)).isChecked();
                    if (transactionData.clientsArray[k]) { numClients += 1; }
                }
                transactionData.numClients = numClients;
                double costPerPerson = 0.00;
                try // check for blank cost or division by zero
                {
                    costPerPerson = Double.parseDouble(transactionData.cost) / numClients;
                    DecimalFormat format = new DecimalFormat("##.00");
                    transactionData.s_costPerPerson = format.format(costPerPerson);
                    costPerPerson = Double.parseDouble(transactionData.s_costPerPerson);
                    System.out.println("Cost Per Person = "+costPerPerson);
                    transactionData.d_costPerPerson = costPerPerson;
                }
                catch(Exception e) { transactionData.s_costPerPerson = "-.--"; }
                // update the cost per person view when you save
                TextView costPerPersonText = ((TextView) gridLayout.getChildAt(i+3+journal.members.size()));
                costPerPersonText.setText(transactionData.s_costPerPerson);

            }
        });

        FrameLayout frameLayout = v.findViewById(R.id.transaction_frame);
        frameLayout.removeView(gridLayout);
        frameLayout.addView(gridLayout);

        /*String value;
        if (gridLayout.getChildAt(0) instanceof TextView) {
            value = ((TextView) gridLayout.getChildAt(0)).getText().toString();
        }
        else { value = "Poop"; }
        System.out.println("*******************************"+(value));*/

        return v;
    }

    void createTransaction(String provider, String service, String cost, Boolean[] array)
    {
        Transaction transaction = new Transaction();
        transaction.provider = provider;
        transaction.service = service;
        transaction.cost = cost;
        transaction.clientsArray = array;
        transaction.s_costPerPerson = "$-.--";
        journal.transactionList.add(transaction);
    }

    void createTransaction(Transaction data)
    {
        // Create transaction view from data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, memberNames);

        Spinner providerDropdown = new Spinner(getContext());
        providerDropdown.setAdapter(adapter);
        providerDropdown.setAutofillHints("Provider");
        providerDropdown.setSelection(Arrays.asList(memberNames).indexOf(data.provider));
        gridLayout.addView(providerDropdown);

        EditText serviceEditText = new EditText(getContext());
        serviceEditText.setHint("Service");
        serviceEditText.setText(data.service);
        gridLayout.addView(serviceEditText);

        EditText costEditText = new EditText(getContext());
        costEditText.setHint("Cost");
        costEditText.setText(data.cost);
        costEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        gridLayout.addView(costEditText);

        for (int j=0; j<((JournalActivity)getActivity()).getJournal().members.size(); j++)
        {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setChecked(data.clientsArray[j]);
            gridLayout.addView(checkBox);
        }

        TextView costPerPerson = new TextView(getContext());
        costPerPerson.setText(data.s_costPerPerson);
        gridLayout.addView(costPerPerson);

    }

    void createBlankTransaction()
    {
        // Create a new transaction row
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, memberNames);
        Transaction transaction = new Transaction();
        transaction.service = "";
        transaction.cost = "";
        transaction.s_costPerPerson = "$-.--";
        transaction.clientsArray = new Boolean[((JournalActivity)getActivity()).getJournal().numMembers];

        ((JournalActivity)getActivity()).getJournal().transactionList.add(transaction);

        Spinner providerDropdown = new Spinner(getContext());
        providerDropdown.setAdapter(adapter);
        providerDropdown.setAutofillHints("Provider");
        gridLayout.addView(providerDropdown);

        EditText serviceEditText = new EditText(getContext());
        serviceEditText.setHint("Service");
        gridLayout.addView(serviceEditText);

        EditText costEditText = new EditText(getContext());
        costEditText.setHint("Cost");
        costEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        gridLayout.addView(costEditText);

        for (int j=1; j<memberNames.length; j++)
        {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setChecked(false);
            gridLayout.addView(checkBox);
        }

        TextView costPerPerson = new TextView(getContext());
        costPerPerson.setText(transaction.s_costPerPerson);
        gridLayout.addView(costPerPerson);

        // scroll to bottom when a new transaction is made
        ScrollView verticalScrollView = v.findViewById(R.id.vert_scroll_view);
        verticalScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

}
