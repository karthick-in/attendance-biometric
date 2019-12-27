package com.attendance;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class Viewattendance extends Activity {
	Spinner empspinner;
	TextView selectemptv,nametv;
	SharedPreferences sp;
	EditText dateed,todateed;
	Button datebtn,todatebtn;
	Calendar cal;
	int yr,month,datevalue,tempid;
	SQLiteDatabase db;
	int id,roleid;
	ListView lv;
	String name,empid,getfromdate,gettodate;
	Menu thismenu;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	java.util.Date date1,date2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewattendance);
		lv=(ListView)findViewById(R.id.listView1);
		empspinner=(Spinner)findViewById(R.id.spinneremp);
		selectemptv=(TextView)findViewById(R.id.textViewselctemployee);
		nametv=(TextView)findViewById(R.id.textView2);
		dateed=(EditText)findViewById(R.id.editTextdate);
		todateed=(EditText)findViewById(R.id.editTexttodate);
		datebtn=(Button)findViewById(R.id.datebtn);
		todatebtn=(Button)findViewById(R.id.todatebtn);
		db = openOrCreateDatabase("dbattendance.db",MODE_PRIVATE, null);
		sp=getSharedPreferences("mypref", Context.MODE_PRIVATE);		
		name=sp.getString("name", "");
		empid=sp.getString("empid", "");
		id=sp.getInt("id",0);
		tempid=id;
		roleid=sp.getInt("roleid", 0);
		dateed.setEnabled(false);
		dateed.setFocusable(false);
		todateed.setEnabled(false);
		todateed.setFocusable(false);
		if (empid == "" && name == "" )
		{
			Intent logintent=new Intent(this,Loginact.class);
			startActivity(logintent);
		}
		else
		{			
			if(roleid==3)
			{	//employee
				empspinner.setVisibility(View.INVISIBLE);				
				selectemptv.setVisibility(View.INVISIBLE);
				tempid=id;
			}
			//Toast.makeText(getBaseContext(), "roleid = "+roleid+"\ntempid = "+tempid, Toast.LENGTH_LONG).show();
			setupLog();	
			cal=Calendar.getInstance();
			datevalue=cal.get(Calendar.DATE);
			month=cal.get(Calendar.MONTH);
			yr=cal.get(Calendar.YEAR);
			updateFromDate();
			month=cal.get(Calendar.MONTH);
			updateToDate();
			loadSpinner();
			setListview();
			nametv.setText("Welcome "+name);
			//Toast.makeText(getBaseContext(), "Welcome "+name, Toast.LENGTH_LONG).show();			
			datebtn.setOnClickListener(new OnClickListener() {
				
				public void onClick(View arg0) {
					showDialog(1);			
				}
			});
			todatebtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					showDialog(2);
				}
			});
			empspinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					try
					{
						String current_name=empspinner.getSelectedItem().toString();
						tempid=getempidfromName(current_name);
						setListview();
					}
					catch (Exception e) {
						// TODO: handle exception
						//e.printStackTrace();
						//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub					
				}				
			});			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.optionmenu, menu);
		thismenu=menu;
		thismenu.findItem(R.id.item1).setVisible(false);
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
		case R.id.totins:
			displayTotalIns();
			return true;
		case R.id.resetpass:
			Intent resetpassin=new Intent(this,Resetpassword.class);
			startActivity(resetpassin);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	public void loadSpinner()
	{	
		try
		{			
		String qry="select * from employeedetails";
		String[] emparray={null};
		int i=0;
		if(roleid==2){			
			qry="select * from employeedetails where reportingid="+id;			
		}
		//Toast.makeText(this, "qry = "+qry, Toast.LENGTH_LONG).show();
		Cursor c=db.rawQuery(qry, null);
		int count=c.getCount();		
		if(count>0){
			if(roleid==2){
				count++;
				emparray=new String[count];
				emparray[i]=name;
				i++;
			}
			else if(roleid==1){
				emparray=new String[count];
			}				
			c.moveToFirst();
			do{
				emparray[i]=c.getString(c.getColumnIndex("name"));
				i++;
			}
			while(c.moveToNext());
			//Toast.makeText(this, "empid is  "+empid, Toast.LENGTH_LONG).show();			
			ArrayAdapter<String> ad=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,emparray);
			empspinner.setAdapter(ad);
		}
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();		
		}		
	}
	
	public void updateFromDate()
	{
		month++;
		dateed.setText(yr+"-"+month+"-"+datevalue);
	}
	
	public void updateToDate()
	{
		month++;
		todateed.setText(yr+"-"+month+"-"+datevalue);
	}
	
	public void setupLog()
	{
		try
		{			
			db.execSQL("insert into logtable(empid,logdate,logtime,logdetail) values("+id+",CURRENT_DATE,CURRENT_TIME,'IN')");
			Thread.sleep(3000);
			db.execSQL("insert into logtable(empid,logdate,logtime,logdetail) values("+id+",CURRENT_DATE,CURRENT_TIME,'OUT')");
		}
		catch (Exception e) {
			//e.printStackTrace();
			//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();		
		}
	}	
	
	public void setListview()
	{		
		try
		{
			getfromdate=dateed.getText().toString();
			gettodate=todateed.getText().toString();			
	    	date1=sdf.parse(getfromdate);
	    	date2=sdf.parse(gettodate);
	    	if(date1.before(date2) || date1.equals(date2)){
			String sql="select * from logtable where empid="+tempid+" and logdate BETWEEN '"+sdf.format(date1)+"' AND '"+sdf.format(date2)+"'";	    	
			Cursor c=db.rawQuery(sql, null);
			int cnt=c.getCount();
			String[] logarray=new String[cnt];
			String[] logdetail=new String[cnt];
			if(cnt>0)
			{
				c.moveToFirst();
				int i=0;
				do
				{
					logarray[i]=c.getString(c.getColumnIndex("logtime"));
					logdetail[i]=c.getString(c.getColumnIndex("logdetail"));
					i++;
				}while(c.moveToNext());
				List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
		        for(int j=0;j<cnt;j++){
		            HashMap<String, String> hm = new HashMap<String,String>();
		            hm.put("time",logarray[j]);
		            hm.put("logd","Detail : " + logdetail[j]);
		            aList.add(hm);
		            String[] from = {"time","logd" };		                      
		            int[] to = {R.id.txt,R.id.cur};
		            SimpleAdapter adapter = new SimpleAdapter(this, aList, R.layout.listview_layout, from, to);
		            lv.setAdapter(adapter);
		        }
		    }
			else
			{
				Toast.makeText(getBaseContext(), "No entries found for the selected range of date!", Toast.LENGTH_LONG).show();
				lv.setAdapter(null);
			}
	    	}
	    	else{	    					
	    		Toast.makeText(getBaseContext(), "From and to date should not be selected in invalid range!\nDate does not changed!", Toast.LENGTH_LONG).show();
	    		datevalue=cal.get(Calendar.DATE);
				month=cal.get(Calendar.MONTH);
				yr=cal.get(Calendar.YEAR);
				updateFromDate();
				month=cal.get(Calendar.MONTH);
				updateToDate();	
				setListview();
	    	}
		}
		catch (Exception e) {			
			//e.printStackTrace();
			//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id==1)	//from date id
		{
			return new DatePickerDialog(this, ds, yr, month, datevalue);
		}
		else if(id==2){	//to date id
			return new DatePickerDialog(this, ds2, yr, month, datevalue);
		}
		else
		{
			return super.onCreateDialog(id);
		}
	}
	
	//from date listener
	OnDateSetListener ds=new OnDateSetListener() {
		
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			yr=arg1;
			month=arg2;
			datevalue=arg3;
			Boolean fromdatecheck=checkDateValue(yr,month,datevalue);
			if(fromdatecheck){
				updateFromDate();
				setListview();
			}
			else{
				Toast.makeText(getBaseContext(), "Please select the valid date...", Toast.LENGTH_LONG).show();
			}	
		}
	};
	
	//to date listener
	OnDateSetListener ds2=new OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
			// TODO Auto-generated method stub
			yr=arg1;
			month=arg2;
			datevalue=arg3;
			Boolean todatecheck=checkDateValue(yr,month,datevalue);
			if(todatecheck){
				updateToDate();
				setListview();
			}
			else{
				Toast.makeText(getBaseContext(), "Please select the valid date...", Toast.LENGTH_LONG).show();
			}
		}
	};
	
	public Boolean checkDateValue(int y,int m,int d){
		try {
			m=m++;
			String mydateString=y+"-"+m+"-"+d;			
			Date mydate=sdf.parse(mydateString);
			Calendar cal1=Calendar.getInstance();
			int d1=cal1.get(Calendar.DATE);
			int m1=cal1.get(Calendar.MONTH);
			int y1=cal1.get(Calendar.YEAR);
			m1=m1++;
			String curdatevalue=y1+"-"+m1+"-"+d1;
			Date currentdate=sdf.parse(curdatevalue);			
			if(mydate.equals(currentdate)){
				return true;
			}
			if(mydate.before(currentdate)){				
				return true;
			}
			else{
				return false;
			}			
		} catch (ParseException e) {			
			//e.printStackTrace();
			//Toast.makeText(getBaseContext(), "im from check date method catch\n"+e.getMessage(), Toast.LENGTH_LONG).show();
		}
		return null;		
	}
	
	public int getempidfromName(String cur_empname)
	{
		int idvalue=0;				
		Cursor c=db.rawQuery("select * from employeedetails where name='"+cur_empname+"'", null);
		int tot=c.getCount();
		if(tot>0)
		{
			c.moveToFirst();
			idvalue=c.getInt(c.getColumnIndex("id"));
			c.close();
			return idvalue;
		}
		else{
			return 0;
		}		
	}
	
	public void displayTotalIns()
	{
		try{	
			getfromdate=dateed.getText().toString();
			gettodate=todateed.getText().toString();			
	    	date1=sdf.parse(getfromdate);
	    	date2=sdf.parse(gettodate);
			String sql1="select * from logtable where empid="+tempid+" and logdetail='IN' and logdate BETWEEN '"+sdf.format(date1)+"' AND '"+sdf.format(date2)+"'";	    	
			String sql2="select * from logtable where empid="+tempid+" and logdetail='OUT' and logdate BETWEEN '"+sdf.format(date1)+"' AND '"+sdf.format(date2)+"'";			
			Cursor inc=db.rawQuery(sql1, null);
			Cursor outc=db.rawQuery(sql2, null);			
			int incount=inc.getCount();
			int outcount=outc.getCount();
			if(incount>0){
				String[] inarr=new String[incount];
				String[] outarr=new String[outcount];				
				int j=0;				
				while(inc.moveToNext()){					
					inarr[j]=inc.getString(inc.getColumnIndex("logtime"));					
					outc.moveToNext();
					outarr[j]=outc.getString(outc.getColumnIndex("logtime"));
					j++;
				}				
				long in=0;
				for(int z=0;z<incount;z++){
					String tempin=inarr[z];
					String tempout=outarr[z];
					long time1=getmilliSeconds(tempout);
					long time2=getmilliSeconds(tempin);
					//in holds millisecond
					in+=time1-time2;														
				}					
				String inresult=getTimefromms(in);
				Toast.makeText(this, "Total time spent IN : "+inresult, Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(this, "No entries found to calculate INs", Toast.LENGTH_LONG).show(); 
			}
		}
		catch (Exception e) {			
			//e.printStackTrace();
			//Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public long getmilliSeconds(String timecame){
		String[] data = timecame.split(":");

        int hours  = Integer.parseInt(data[0]);
        int minutes = Integer.parseInt(data[1]);
        int seconds = Integer.parseInt(data[2]);
        
        //CONERTING HH:MM:SS STRING TO MILLISECONDS
        
        int time = seconds + 60 * minutes + 3600 * hours;
        String res= ""+TimeUnit.MILLISECONDS.convert(time, TimeUnit.SECONDS);
        long millisec=Integer.parseInt(res);
		return millisec;		
	}
	
	public String getTimefromms(long millisecondcame){
		
		 long second = (millisecondcame / 1000) % 60;
	     long minute = (millisecondcame / (1000 * 60)) % 60;
	     long hour = (millisecondcame / (1000 * 60 * 60)) % 24;
	     String timestr=String.format("%02d:%02d:%02d", hour,minute,second);
	     //String timestr = ""+hour+":"+minute+":"+second;
		 return timestr;
	}

}
