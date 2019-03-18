package hu.bme.aut.shoppinglist;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import hu.bme.aut.shoppinglist.adapter.ShoppingAdapter;
import hu.bme.aut.shoppinglist.data.ShoppingItem;
import hu.bme.aut.shoppinglist.data.ShoppingListDatabase;
import hu.bme.aut.shoppinglist.fragments.NewShoppingItemDialogFragment;

public class MainActivity extends AppCompatActivity
        implements NewShoppingItemDialogFragment.NewShoppingItemDialogListener,
        ShoppingAdapter.ShoppingItemClickListener {
    private RecyclerView recyclerView;
    private ShoppingAdapter adapter;

    private ShoppingListDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewShoppingItemDialogFragment().show(getSupportFragmentManager(), NewShoppingItemDialogFragment.TAG);
            }
        });

        database = Room.databaseBuilder(
                getApplicationContext(),
                ShoppingListDatabase.class,
                "shopping-list"
        ).build();

        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.MainRecyclerView);
        adapter = new ShoppingAdapter(this);
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<ShoppingItem>>() {

            @Override
            protected List<ShoppingItem> doInBackground(Void... voids) {
                return database.shoppingItemDao().getAll();
            }

            @Override
            protected void onPostExecute(List<ShoppingItem> shoppingItems) {
                adapter.update(shoppingItems);
            }
        }.execute();
    }

    @Override
    public void onItemChanged(final ShoppingItem item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.shoppingItemDao().update(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                Log.d("MainActivity", "ShoppingItem update was successful");
            }
        }.execute();
    }

    @Override
    public void onItemRemoved(final ShoppingItem item) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                database.shoppingItemDao().deleteItem(item);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean isSuccessful) {
                adapter.deleteItem(item);
                Log.d("MainActivity", "ShoppingItem delete was successful");
            }
        }.execute();
    }

    @Override
    public void onShoppingItemCreated(final ShoppingItem newItem) {
        new AsyncTask<Void, Void, ShoppingItem>() {

            @Override
            protected ShoppingItem doInBackground(Void... voids) {
                database.shoppingItemDao().insertAll(newItem);
                return newItem;
            }

            @Override
            protected void onPostExecute(ShoppingItem shoppingItem) {
                adapter.addItem(shoppingItem);
            }
        }.execute();
    }

}
