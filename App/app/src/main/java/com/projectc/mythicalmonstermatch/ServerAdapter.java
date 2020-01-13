package com.projectc.mythicalmonstermatch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ServerViewHolder> {

    private ArrayList<ServerItem> serverList;
    private LayoutInflater layoutInflater;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public ServerAdapter(Context context, ArrayList<ServerItem> serverList){
        this.context = context;
        this.serverList = serverList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ServerAdapter.ServerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.server_item, viewGroup, false);
        return new ServerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerAdapter.ServerViewHolder serverViewHolder, int i) {
        String servername = serverList.get(i).getServername();
        int playercount = serverList.get(i).getPlayerCount();
        int startState = serverList.get(i).getStartState();
        serverViewHolder.servername.setText(servername);
        serverViewHolder.usercount.setText(playercount + "/5");
        if(playercount >= 5 && startState == 0){
            serverViewHolder.usercount.setTextColor(context.getColor(R.color.full));
        } else if(startState == 1){
            serverViewHolder.usercount.setTextColor(context.getColor(R.color.started));
        }
    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }

    public class ServerViewHolder extends RecyclerView.ViewHolder {
        TextView servername;
        TextView usercount;
        ServerViewHolder(View itemView) {
            super(itemView);
            servername = itemView.findViewById(R.id.servername);
            usercount = itemView.findViewById(R.id.usercount);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }


    }
}
