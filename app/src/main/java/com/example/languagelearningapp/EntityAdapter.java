package com.example.languagelearningapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EntityAdapter extends RecyclerView.Adapter<EntityAdapter.EntityViewHolder> {

    private final List<Entity> entities;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public EntityAdapter(List<Entity> entities, OnItemClickListener listener) {
        this.entities = entities;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EntityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entity, parent, false);
        return new EntityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntityViewHolder holder, int position) {
        Entity entity = entities.get(position);
        holder.bind(entity);
    }

    @Override
    public int getItemCount() {
        return entities.size();
    }

    class EntityViewHolder extends RecyclerView.ViewHolder {
        private final TextView property1TextView;
        private final TextView property2TextView;

        public EntityViewHolder(@NonNull View itemView) {
            super(itemView);
            property1TextView = itemView.findViewById(R.id.property1TextView);
            property2TextView = itemView.findViewById(R.id.property2TextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }

        public void bind(Entity entity) {
            if (entity != null) {
                property1TextView.setText(entity.getProperty1() != null ? entity.getProperty1() : "");
                property2TextView.setText(entity.getProperty2() != null ? entity.getProperty2() : "");
            }
        }
    }
}