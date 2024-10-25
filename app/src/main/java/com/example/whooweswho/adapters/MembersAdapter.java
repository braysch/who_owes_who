package com.example.whooweswho.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whooweswho.R;
import com.example.whooweswho.objects.Member;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder>
{

    private ArrayList<Member> members;

    public MembersAdapter(ArrayList<Member> members)
    {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_member_name.setText(members.get(position).member_name);
        holder.tv_member_username.setText("@"+members.get(position).member_username);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tv_member_name;
        private TextView tv_member_username;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tv_member_name = (TextView) itemView.findViewById(R.id.member_name);
            tv_member_username = (TextView) itemView.findViewById(R.id.member_username);
        }
    }
}