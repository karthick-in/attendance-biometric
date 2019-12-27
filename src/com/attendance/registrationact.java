package com.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

public class registrationact extends Activity{
	Spinner sprole;
	SharedPreferences sp;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);
		sp=getSharedPreferences("mypref", Context.MODE_PRIVATE);
		String name=sp.getString("name", "");
		String empid=sp.getString("empid", "");
		if (empid == null && name == null )
		{
			Intent logintent=new Intent(this,Loginact.class);
			startActivity(logintent);
		}
		sprole=(Spinner)findViewById(R.id.spinnerrole);
		
	}
	
	//button function on submit data
	public void submit_data(View view)
	{
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.optionmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.item1:
			Intent attin=new Intent(this,Viewattendance.class);
			startActivity(attin);
			return true;
		case R.id.item2:			
			return true;
		case R.id.item3:
			Editor ed=sp.edit();
			ed.clear();
			ed.commit();
			//raise confirm box here before logout
			Intent logintent=new Intent(this,Loginact.class);
			startActivity(logintent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}

}
