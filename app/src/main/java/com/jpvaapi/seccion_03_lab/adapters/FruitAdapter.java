package com.jpvaapi.seccion_03_lab.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.jpvaapi.seccion_03_lab.R;
import com.jpvaapi.seccion_03_lab.models.Fruit;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Crated by Juan P on  28/01/20.
 */

public class FruitAdapter extends RecyclerView.Adapter<FruitAdapter.ViewHolder> {

    private List<Fruit> fruits;
    private int layout;
    private Activity activity;
    private OnItemClickListener listener;

    //We pass the activity instead of the context, since we will need to inflate in the context menu
    public FruitAdapter(List<Fruit> fruits, int layout, Activity activity, OnItemClickListener listener) {
        this.fruits = fruits;
        this.layout = layout;
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(fruits.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return fruits.size();
    }

    //We implement the OnCreateContextMenu Listener and OnMenuItemClickListener
    //interfaces to make use of the context menu in RecyclerView, and overwrite the methods
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView textViewName;
        public TextView textViewDescription;
        public TextView textViewQuantity;
        public ImageView imageViewBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            imageViewBackground = itemView.findViewById(R.id.imageViewBackground);
            //We add to the view the listener for the context menu, instead of doing it in
            //the activity using the registerForContextMenu method
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(final Fruit fruit, final OnItemClickListener listener) {

            this.textViewName.setText(fruit.getName());
            this.textViewDescription.setText(fruit.getDescription());
            this.textViewQuantity.setText(fruit.getQuantity() + "");
            //Logic applied to limit the quantity in each fruit element
            if (fruit.getQuantity() == Fruit.LIMIT_QUANTITY) {
                textViewQuantity.setTextColor(ContextCompat.getColor(activity, R.color.colorAlert));
                textViewQuantity.setTypeface(null, Typeface.BOLD);
            } else {
                textViewQuantity.setTextColor(ContextCompat.getColor(activity, R.color.defaultTextColor));
                textViewQuantity.setTypeface(null, Typeface.NORMAL);
            }
            //We load the image with Picasso
            Picasso.with(activity).load(fruit.getImgBackground()).fit().into(this.imageViewBackground);
            //We add the click listener for each fruit element
            this.imageViewBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(fruit, getAdapterPosition());
                }
            });
        }

        //We overwrite onCreateContextMenu, inside the ViewHolder,
        //instead of doing it in the activity
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            //We collect the position with the getAdapterPosition method
            Fruit fruitSelected = fruits.get(this.getAdapterPosition());
            //We establish title and icon for each element, looking at its properties
            contextMenu.setHeaderTitle(fruitSelected.getName());
            contextMenu.setHeaderIcon(fruitSelected.getImgIcon());
            //We inflate the menu
            MenuInflater inflater = activity.getMenuInflater();
            inflater.inflate(R.menu.context_menu_fruit, contextMenu);
            //Finally, we add one by one, the listener onMenuItemClick to
            //control the actions in the contextMenu, we used to handle it
            //with the onContextItemSelected method in the activity
            for (int i = 0; i < contextMenu.size(); i++)
                contextMenu.getItem(i).setOnMenuItemClickListener(this);
        }

        //Overwrite onMenuItemClick, inside the ViewHolder,
        //instead of doing it the activity under the name onContextSelected
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //We don´t get our info object
            //because the position can be rescued from getAdapterPosition
            switch (menuItem.getItemId()) {
                case R.id.delete_fruit:
                    //Note that as we are inside the adapter, we can access
                    //to his own methods such as notifyItemRemoved or notifyItemChanged
                    fruits.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    return true;
                case R.id.reset_fruit_quantity:
                    fruits.get(getAdapterPosition()).resetQuantity();
                    notifyItemChanged(getAdapterPosition());
                    return true;
                default:
                    return false;
            }

        }
    }

    public interface OnItemClickListener {
        void onItemClick(Fruit fruit, int position);
    }
}
