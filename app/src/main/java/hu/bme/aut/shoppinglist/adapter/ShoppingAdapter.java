package hu.bme.aut.shoppinglist.adapter;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.shoppinglist.R;
import hu.bme.aut.shoppinglist.data.ShoppingItem;

public class ShoppingAdapter
        extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {

    private final List<ShoppingItem> items;

    private ShoppingItemClickListener listener;

    public ShoppingAdapter(ShoppingItemClickListener listener) {
        this.listener = listener;
        items = new ArrayList<>();
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate( R.layout.item_shopping_list, parent, false);
        return new ShoppingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        ShoppingItem item = items.get(position);
        holder.nameTextView.setText(item.name);
        holder.descriptionTextView.setText(item.description);
        holder.categoryTextView.setText(item.category.name());
        holder.priceTextView.setText(item.estimatedPrice + " Ft");
        holder.iconImageView.setImageResource(getImageResource(item.category));
        holder.isBoughtCheckBox.setChecked(item.isBought);

        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ShoppingItemClickListener{
        void onItemChanged(ShoppingItem item);
        void onItemRemoved(ShoppingItem item);
    }

    class ShoppingViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
        TextView nameTextView;
        TextView descriptionTextView;
        TextView categoryTextView;
        TextView priceTextView;
        CheckBox isBoughtCheckBox;
        ImageButton removeButton;

        ShoppingItem item;

        ShoppingViewHolder(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.ShoppingItemIconImageView);
            nameTextView = itemView.findViewById(R.id.ShoppingItemNameTextView);
            descriptionTextView = itemView.findViewById(R.id.ShoppingItemDescriptionTextView);
            categoryTextView = itemView.findViewById(R.id.ShoppingItemCategoryTextView);
            priceTextView = itemView.findViewById(R.id.ShoppingItemPriceTextView);
            isBoughtCheckBox = itemView.findViewById(R.id.ShoppingItemIsBoughtCheckBox);
            removeButton = itemView.findViewById(R.id.ShoppingItemRemoveButton);

            isBoughtCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                    if(item != null){
                        item.isBought = isChecked;
                        listener.onItemChanged(item);
                    }
                }
            });

            removeButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemRemoved( item );
                    deleteItem(item);
                }
            });
        }

    }

    public void deleteItem(ShoppingItem item){
        int index = items.indexOf(item);
        items.remove( item );
        notifyItemRemoved(index);
    }

    private @DrawableRes
    int getImageResource(ShoppingItem.Category category) {
        @DrawableRes int ret;
        switch (category) {
            case BOOK:
                ret = R.drawable.open_book;
                break;
            case ELECTRONIC:
                ret = R.drawable.lightning;
                break;
            case FOOD:
                ret = R.drawable.groceries;
                break;
            default:
                ret = 0;
        }
        return ret;
    }

    public void addItem(ShoppingItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void update(List<ShoppingItem> shoppingItems) {
        items.clear();
        items.addAll(shoppingItems);
        notifyDataSetChanged();
    }
}
