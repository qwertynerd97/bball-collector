package com.example.app.baseballmessenger;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

/**
 * Created by elli on 3/25/18.
 */

public class DrawerListener implements NavigationView.OnNavigationItemSelectedListener  {
    private Context context;
    private DrawerLayout drawer;

    public DrawerListener(Context context, DrawerLayout drawer){
        this.context = context;
        this.drawer = drawer;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i = null;

        if (id == R.id.nav_collection) {
            i = new Intent(context, CardListActivity.class);
            i.putExtra("wishlist", false);
        } else if (id == R.id.nav_wishlist) {
            i = new Intent(context, CardListActivity.class);
            i.putExtra("wishlist", true);
        } else if (id == R.id.nav_profile) {
            i = new Intent(context, UserDetailActivity.class);
            i.putExtra("user", Handoff.currentUser);
        } else if (id == R.id.nav_chat) {
            i = new Intent(context, ChatListActivity.class);
        } else if (id == R.id.nav_users) {
            i = new Intent(context, SearchUsersActivity.class);
        } else if (id == R.id.nav_trade) {
            i = new Intent(context, TradeListActivity.class);
        }

        if(i != null)
        {
            context.startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
