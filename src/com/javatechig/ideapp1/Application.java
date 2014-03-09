package com.javatechig.ideapp1;

import com.parse.Parse;
import com.parse.PushService;

public class Application extends android.app.Application {

  public Application() {
  }

  @Override
  public void onCreate() {
    super.onCreate();

	// Initialize the Parse SDK.
    Parse.initialize(this, "50ba7jTsCIT6hpI7fm4IIgpkcpnhTJCYKl869qpM",
			"25Il4cGq15wXyOQ51a23F7dS9SbweFLBALvO5GPS"); 

	// Specify an Activity to handle all pushes by default.
	PushService.setDefaultPushCallback(this, FeedListActivity.class);
  }
}