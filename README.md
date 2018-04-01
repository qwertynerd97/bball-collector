# bball-collector

Because of a misscommunication in the inital develoment cycle, trades_messages is actually the master branch.  Please clone, fetch, and branch off of it instead of master.

When making a new activity, follow these four steps:
 - Create a "Basic Activity" using Android's auto creator
 - COPY the xml from activity_user_detail into the new activity_(activityName) xml file
 - Replace <include layout="@layout/content_user_detail" /> with <include layout="@layout/(activityName)_detail
 - Paste the following code into the bottom of the Activity's onCreate Method:
	```
	DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        DrawerListener listen = new DrawerListener(this, drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(listen);
	```
- If toolbar becomes highlighted in red, paste in this code above the previous code
	```
	Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
	```
