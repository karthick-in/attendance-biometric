package com.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Resetpassword extends Activity{
EditText newet,confirmet;
Button resetbtn;
SQLiteDatabase db;
SharedPreferences sp;
String name,empid;
int id,roleid;
Menu thismenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpassword);
		newet=(EditText)findViewById(R.id.newpasset);
		confirmet=(EditText)findViewById(R.id.confirmpasset);
		resetbtn=(Button)findViewById(R.id.resetpassbtn);
		db=openOrCreateDatabase("dbattendance.db",MODE_PRIVATE, null);
		sp=getSharedPreferences("mypref", Context.MODE_PRIVATE);
		roleid=sp.getInt("roleid", 0);
		name=sp.getString("name", "");
		empid=sp.getString("empid", "");
		id=sp.getInt("id",0);
		
		resetbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(newet.getText())) {
					newet.setError("new password is required!");
				}				
				else if (TextUtils.isEmpty(confirmet.getText())) {
					confirmet.setError("confirm password is required!");
				}
				else {					
					String newpwd=newet.getText().toString();
					String confirmpwd=confirmet.getText().toString();
					if(newpwd.equals(confirmpwd)){
						String sql="update employeedetails set password='"+confirmpwd+"' where employeeid='"+empid+"'";
						db.execSQL(sql);
						Toast.makeText(getBaseContext(), "Password changed successfully.\nLogging out...", Toast.LENGTH_LONG).show();
						Editor ed=sp.edit();
						ed.clear();
						ed.commit();
						Intent logintent=new Intent(Resetpassword.this,Loginact.class);
						startActivity(logintent);
					}
					else{
						confirmet.setError("Password mismatched!");
						Toast.makeText(getBaseContext(), "Password mismatched, check the fields!", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.optionmenu, menu);
		thismenu=menu;
		thismenu.findItem(R.id.totins).setVisible(false);
		thismenu.findItem(R.id.resetpass).setVisible(false);
		if(roleid==3 || roleid==2){
		thismenu.findItem(R.id.item2).setVisible(false);
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.item1:	
			Intent attin=new Intent(this,Viewattendance.class);
			startActivity(attin);
			return true;
		case R.id.item2:
			Intent regin=new Intent(this,registrationact.class);
			startActivity(regin);
			return true;
		case R.id.item3:
			Editor ed=sp.edit();
			ed.clear();
			ed.commit();
			Intent logintent=new Intent(this,Loginact.class);
			startActivity(logintent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
}
