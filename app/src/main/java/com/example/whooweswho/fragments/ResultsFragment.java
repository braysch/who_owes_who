package com.example.whooweswho.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whooweswho.R;
import com.example.whooweswho.activities.JournalActivity;
import com.example.whooweswho.objects.Circuit;
import com.example.whooweswho.objects.CreditStatement;
import com.example.whooweswho.objects.DebitStatement;
import com.example.whooweswho.objects.Journal;
import com.example.whooweswho.objects.PaytrixEdge;
import com.example.whooweswho.objects.Result;
import com.example.whooweswho.objects.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ResultsFragment extends Fragment
{
    View v;
    Journal journal;
    String[] memberNames;
    ArrayList<Circuit> circuitList;
    ArrayList<Result> results;
    double minValue;
    int maxRec;
    Circuit optimalCircuit;
    double[][] paytrix;
    int totalnumReductions;

    public ResultsFragment() {super(R.layout.fragment_results);}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        v = inflater.inflate(R.layout.fragment_results, container, false);
        journal = ((JournalActivity)getActivity()).getJournal();

        results = new ArrayList<Result>();

        totalnumReductions = 0;

        memberNames = new String[journal.members.size()];
        for (int i=0; i<journal.numMembers; i++)
        {
            memberNames[i] = journal.members.get(i).member_name;
        }

        // paytrix = payment matrix
        paytrix = new double[journal.numMembers][journal.numMembers];

        // populate the paytrix
        for (int i=0; i < journal.transactionList.size(); i++)
        {
            Transaction transaction = journal.transactionList.get(i);
            int index = (Arrays.asList(memberNames).indexOf(transaction.provider));

            for (int j=0; j<journal.numMembers; j++)
            {
                if (transaction.clientsArray[j])
                {
                    paytrix[index][j] += transaction.d_costPerPerson;
                }
            }
        }

        displayPaytrix(paytrix, "INITIAL PAYTRIX:");

        // create negative transpose of paytrix
        double[][] t_paytrix = new double[journal.numMembers][journal.numMembers];
        for (int i=0; i< journal.numMembers; i++)
        {
            for (int j=0; j< journal.numMembers; j++)
            {
                t_paytrix[i][j] = -paytrix[j][i];
            }
        }

        displayPaytrix(t_paytrix, "NEGATIVE TRANSPOSED PAYTRIX:");

        // account for mutual payments by adding patrix and t_patrix
        for (int i=0; i< journal.numMembers; i++)
        {
            for (int j=0; j< journal.numMembers; j++)
            {
                double mutValue = paytrix[i][j] + t_paytrix[i][j];

                BigDecimal bd = new BigDecimal(mutValue).setScale(2, RoundingMode.HALF_UP);
                mutValue = bd.doubleValue(); // only way I can find to avoid double precision errors
                if (mutValue < 0) { mutValue = 0; } // remove negative values

                paytrix[i][j] = mutValue;
            }
        }

        displayPaytrix(paytrix, "MUTUAL PAYTRIX:");

        double[] initialFlow = getFlow();
        displayFlow(initialFlow);

        // account for circular payments
        for (int i=0; i<journal.numMembers; i++)
        {
            minValue = 0;
            Circuit circuit = new Circuit(); // object to hold circuit data
            circuitList = new ArrayList<Circuit>();
            System.out.println("\n\nCOLUMN "+i);

            findCircuits(paytrix, i, journal.numMembers, i, circuit); // updates circuitList with circuits

            System.out.println("\t"+circuitList.size()+" circuit(s) found\n");

            maxRec = 0;
            optimalCircuit = new Circuit();
            for (int j=0; j<circuitList.size(); j++)
            {
                displayCircuit(circuitList.get(j)); // display is necessary
            }
            System.out.println("\t\tThe optimal circuit removes "+optimalCircuit.numRec+" edges.");
            totalnumReductions += optimalCircuit.numRec;
            System.out.println("\t\tThe optimal circuit is reduced by "+optimalCircuit.minValue);
            simplifyPaytrix();
            displayPaytrix(paytrix, "UPDATED PAYTRIX:");
        }
        System.out.println(totalnumReductions+" edges removed from original matrix.");
        displayPaytrix(paytrix, "FINAL PAYTRIX:");

        double[] finalFlow = getFlow();
        displayFlow(finalFlow);

        System.out.print("\n\t\tReduced Matrix is ");
        if (Arrays.equals(initialFlow, finalFlow))
        {
            System.out.println("VALID");
        }
        else { System.out.println("INVALID"); }

        // we could go a step further and cut out the middlemen--
        // (A owes B̶ owes C, in cases where A and B owe the same amount,)
        // but we will avoid this in order to keep debtor and creditor relations consistent

        // LET'S MAKE SOME BLOODY RESULTS!!

            Result result;
            for (int j=0; j<paytrix.length; j++)
            {
                result = new Result(); // create a new receipt for each member
                result.memberName = journal.members.get(j).member_name;
                result.memberUsername = journal.members.get(j).member_username;
                for (int k=0; k< paytrix.length; k++)
                {
                    if (paytrix[j][k] > 0)
                    {
                        CreditStatement cs = new CreditStatement();
                        cs.debtor = journal.members.get(k).member_name;
                        cs.credit = paytrix[j][k];
                        result.creditStatements.add(cs);
                    }

                    if (paytrix[k][j] > 0)
                    {
                        DebitStatement ds = new DebitStatement();
                        ds.creditor = journal.members.get(k).member_name;
                        ds.debit = paytrix[k][j];
                        result.debitStatements.add(ds);
                    }

                }
                System.out.println("Adding "+result.memberName+"'s result to list");
                results.add(result);
            }

        System.out.println("RESULTS******************************************");
        displayResults();
        return v;
    }

    public void displayResults()
    {
        for (int i=0; i<results.size(); i++)
        {
            System.out.println(i+"/"+results.size());
            Result result = results.get(i);
            System.out.println("**********************");
            System.out.println("Results for "+result.memberName+" (@"+result.memberUsername+"):");
            System.out.println("**********************");
            System.out.println("DEBITS:");
                for (int j = 0; j < result.debitStatements.size(); j++)
                {
                    DebitStatement ds = result.debitStatements.get(j);
                    System.out.println("You owe " + ds.creditor + " " + ds.debit);
                }

            System.out.println("CREDITS:");
                for (int j = 0; j < result.creditStatements.size(); j++)
                {
                    CreditStatement cs = result.creditStatements.get(j);
                    System.out.println(cs.debtor + " owes you " + cs.credit);
                }
        }
    }

    public void displayFlow(double[] flow)
    {
        int sum = 0;
        System.out.print("\t\tFlow: ");
        for (int i=0; i<flow.length; i++)
        {
            sum += flow[i];
            System.out.print("["+flow[i]+"] ");
        }
        System.out.print(" = "+sum);
    }

    public double[] getFlow()
    {
        double[] flow = new double[paytrix.length];

        double debit;
        double credit;

        for (int i=0; i<paytrix.length; i++)
        {
            debit = 0;
            credit = 0;

            for (int j=0; j< paytrix.length; j++)
            {
                credit += paytrix[i][j];
                debit += paytrix[j][i];
            }
            flow[i] = credit-debit;

            BigDecimal bd = new BigDecimal(flow[i]).setScale(2, RoundingMode.HALF_UP);
            flow[i] = bd.doubleValue(); // only way I can find to avoid double precision errors

        }
        return flow;
    }

    public void simplifyPaytrix()
    {
        for (int i=0; i<optimalCircuit.circuitElements.size(); i++)
        {
            double redValue = paytrix[optimalCircuit.circuitElements.get(i).c][optimalCircuit.circuitElements.get(i).r] - optimalCircuit.minValue;

            BigDecimal bd = new BigDecimal(redValue).setScale(2, RoundingMode.HALF_UP);
            redValue = bd.doubleValue(); // only way I can find to avoid double precision errors

            paytrix[optimalCircuit.circuitElements.get(i).c][optimalCircuit.circuitElements.get(i).r] = redValue;
        }
    }

    public void findCircuits(double[][] paytrix, int i, int limit, int start, Circuit circuit)
    {
        if (limit == 0) { return;} // Safety feature to avoid infinite recursion
        for (int j=0; j< journal.numMembers; j++)
        {
            if (paytrix[j][i] != 0)
            {
                double value = paytrix[j][i];
                if ((value < circuit.minValue) || (circuit.minValue <= 0)) { circuit.minValue = value; }
                circuit.circuitElements.add(new PaytrixEdge(j, i, paytrix[j][i])); // add edge to list
                if (j == start && isValidCircuit(circuit))
                {
                    Circuit copy = copyCircuit(circuit);
                    circuitList.add(copy);
                    minValue = 0;
                    circuit.minValue = 0;
                    circuit.circuitElements.remove(circuit.circuitElements.size()-1); // remove elements from list
                    return;
                }
                findCircuits(paytrix, j, limit-1, start, circuit);
                circuit.circuitElements.remove(circuit.circuitElements.size()-1); // remove elements from list
            }
        }
    }

    public boolean isValidCircuit(Circuit circuit)
    {
        // check to make sure circuits are not counted multiple times
        ArrayList<PaytrixEdge> list = circuit.circuitElements;
        Set<PaytrixEdge> set = new HashSet<PaytrixEdge>(list);
        return set.size() >= list.size();
    }

    public Circuit copyCircuit(Circuit circuit)
    {
        Circuit copy = new Circuit();
        for (int i=0; i<circuit.circuitElements.size(); i++)
        {
            int column = circuit.circuitElements.get(i).c;
            int row = circuit.circuitElements.get(i).r;
            double value = circuit.circuitElements.get(i).value;
            copy.circuitElements.add(new PaytrixEdge(column,row,value));
            copy.minValue = circuit.minValue;
        }
        return copy;
    }

    public void displayCircuit(Circuit circuit)
    {
        int numRec = 0;
        ArrayList<PaytrixEdge> circuitElements = circuit.circuitElements;
        System.out.print("\n\tCircuit: ");
        for (int i=0; i<circuitElements.size(); i++)
        {
            PaytrixEdge edge = circuitElements.get(i);
            System.out.print("P["+edge.r+"]["+edge.c+"] ("+edge.value+") -> ");
            if (edge.value == circuit.minValue) { numRec++; }
        }
        circuit.numRec = numRec;
        if (circuit.numRec > maxRec)
        {
            maxRec = circuit.numRec;
            optimalCircuit = circuit;
        }
        System.out.println();
        System.out.println("\t\tMinValue="+circuit.minValue);
        System.out.println("\t\tNumRec="+circuit.numRec+"\n");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void displayPaytrix(double[][] paytrix, String title)
    {
        System.out.println(title);
        for (int i=0; i<journal.numMembers; i++)
        {
            System.out.println();
            for (int j=0; j< journal.numMembers; j++)
            {
                System.out.print(paytrix[i][j]+"   ");
            }
        }
        System.out.println();
    }

}
