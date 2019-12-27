package com.attendance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Loginact extends Activity {
	EditText username, password;
	Button login;
	SharedPreferences sp;
	SQLiteDatabase db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		username = (EditText) findViewById(R.id.usernametb);
		password = (EditText) findViewById(R.id.passwordtb);
		login = (Button) findViewById(R.id.loginbtn);
		db = openOrCreateDatabase("dbattendance.db",
				MODE_PRIVATE, null);
		username.setText("");
		password.setText("");
		initializeDb();
		

	}

	public void login_click(View view) {
		//checking if the username edittext is empty
		if (TextUtils.isEmpty(username.getText())) {

			username.setError("Username is required!");

		}
		//checking if the password edittext is empty 
		else if (TextUtils.isEmpty(password.getText())) {
			password.setError("Password is required!");
		}
		else {
			String uname = username.getText().toString();
			String pwd = password.getText().toString();
			try {
				Cursor c = db.rawQuery(
						"select * from employeedetails where employeeid='"
								+ uname + "' AND password='" + pwd + "'", null);
				int count = c.getCount();
				
				if (count > 0) {
					c.moveToFirst();
					String name=c.getString(c.getColumnIndex("name"));
					String empid=c.getString(c.getColumnIndex("employeeid"));
					int role_id=c.getInt(c.getColumnIndex("roleid"));
					int id=c.getInt(c.getColumnIndex("id"));
					sp=getSharedPreferences("mypref", Context.MODE_PRIVATE);
					Editor ed=sp.edit();
					
					ed.putString("empid", empid);
					ed.putString("name", name);
					ed.putInt("roleid",role_id);
					ed.putInt("id", id);
					
					ed.commit();
					Toast
					.makeText(
							this,
							"Logging in",
							Toast.LENGTH_LONG).show();
					username.setText("");
					password.setText("");
					c.close();
					db.close();
					Intent nextin=new Intent(this,Viewattendance.class);
					startActivity(nextin);
					
				} else {
					Toast
							.makeText(
									this,
									"The username or password you entered is incorrect",
									Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, "error = " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}
	
	public void initializeDb()
	{
		createRoletable();
		createLogtable();
		try {
			
			db.execSQL("create table if not exists employeedetails(id integer primary key AUTOINCREMENT,employeeid varchar(100) UNIQUE,name varchar(200),roleid int,password varchar(200),email varchar(200) UNIQUE,dateofjoining date,reportingid int,designation text,FOREIGN KEY(roleid) REFERENCES roletable(roleid))");
			db.execSQL("insert into employeedetails (employeeid,name,roleid,password,email,dateofjoining,designation) values('admin','jai',1,'1234','jai@gmail.com',CURRENT_DATE,'project manager')");			
			db.execSQL("insert into employeedetails (employeeid,name,roleid,password,email,dateofjoining,reportingid,designation) values('nd01','ganesh',3,'1234','ganesh@gmail.com',CURRENT_DATE,3,'tester')");
			db.execSQL("insert into employeedetails (employeeid,name,roleid,password,email,dateofjoining,designation) values('nd02','jagan',2,'1234','jagan@gmail.com',CURRENT_DATE,'developer')");
			db.close();
		} catch (Exception e) {
			//e.printStackTrace();
			//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}	
	
	}
	
	public void createRoletable()
	{
		try
		{
			db.execSQL("create table if not exists roletable(roleid integer primary key AUTOINCREMENT,rolename varchar(100) UNIQUE)");
			db.execSQL("insert into roletable (rolename) values('admin')");
			db.execSQL("insert into roletable (rolename) values('teamleader')");
			db.execSQL("insert into roletable (rolename) values('employee')");
			
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();		
		}
		
	}
	
	public void createLogtable()
	{
		try
		{
			db.execSQL("create table if not exists logtable(logid integer primary key AUTOINCREMENT,empid text,logdate date,logtime time,logdetail text,FOREIGN KEY(empid) REFERENCES employeedetails(id))");
			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();		
		
		}
	}
}