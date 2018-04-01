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

        if (id == R.id.nav_collection) {
            context.startActivity(new Intent(context, NewTradeActivity.class));
        } else if (id == R.id.nav_wishlist) {
            context.startActivity(new Intent(context, WishlistActivity.class));
        } else if (id == R.id.nav_profile) {
            Intent i=new Intent(context,UserDetailActivity.class);
            i.putExtra("user", Handoff.currentUser);
            context.startActivity(i);
        } else if (id == R.id.nav_chat) {
            context.startActivity(new Intent(context, ChatListActivity.class));
        } else if (id == R.id.nav_users) {
            context.startActivity(new Intent(context, SearchUsersActivity.class));
        } else if (id == R.id.nav_trade) {
            context.startActivity(new Intent(context, TradeListActivity.class));
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
