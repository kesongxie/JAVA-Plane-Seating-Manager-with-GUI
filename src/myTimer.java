/*
 *  Class myTimer is responsible for controlling any behavior pertinent to the time, for example, hide the 
 *  notification banner automatically after 5 seconds, or the auto-counting clock
 * 
 *  @author  Kesong Xie
 * 	@version 1.0
 * 	@since   2014-12-4 
 **/


import java.text.SimpleDateFormat;
import java.util.TimerTask;
import java.util.Date;
import java.text.SimpleDateFormat;

import javax.swing.*;

public class myTimer  extends TimerTask{
	private String taskDetail;  // HIDE_NOTI_BANNER or ANIMATE_CLOCK
	private String cur;
	private String period;
	private JPanel targetToHide;
	private JPanel[] setToVisible;
	private JPanel clock;
	private JLabel timeString = new JLabel();
	private JLabel background = new JLabel();
	private SimpleDateFormat d_f =  new SimpleDateFormat ("hh:mm:ss a E ',' MM-dd-yyyy");
	private Manipulate_db m_p; 
	
	
	/**
	 * constructor for initializing the necessary information for the showing and hiding components for notification banner
	 * @param t:String tgToHide:JPanel setV:JPanel[]
	 * 
	 * 
	 * */
	public myTimer(String t,JPanel tgToHide, JPanel[] setV){
		try{
			m_p = new Manipulate_db();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Error Detected, the system cannot be initilized successfully");
		}
		taskDetail = t;	
		targetToHide = tgToHide;
		setToVisible = setV;

	}
	
	
	
	/**
	 * constructor for initializing the necessary information for the clock
	 * @param t:String clockNeedToBeUpdated bg: JLabel
	 * 
	 * 
	 * */
	public myTimer(String t,JLabel clockNeedToBeUpdated, JLabel bg){
		try{
			m_p = new Manipulate_db();
		}catch(Exception ex){
			JOptionPane.showMessageDialog(null,"Error Detected, the system cannot be initilized successfully");
		}
		taskDetail = t;	
		timeString = clockNeedToBeUpdated;
		background = bg;
	}
	
	
	/**
	 * define the corresponding behavior the task need to be handled when the Date.schedule method is called
	 * @return void
	 * */
	
	public void run(){
		if(taskDetail.equals("HIDE_NOTI_BANNER")){
			targetToHide.setVisible(false);
			for (int i=0; i<setToVisible.length; i++){
				setToVisible[i].setVisible(true);
				targetToHide.removeAll();
			}
		}
		else if(taskDetail.equals("ANIMATE_CLOCK")){
			initializeTimeAndPeriod();
			timeString.setText("Good " + period + " " + cur);
			String imgUrl=m_p.getFolderPrefix();
			switch(period){
				case "Morning": imgUrl+= "Morning_sky.jpg";break;
				case "Afternoon": imgUrl+= "af_sky.jpg";break;
				default : imgUrl+= "night_sky.jpg";break;
			}
			ImageIcon image = new ImageIcon(imgUrl);
			background.setIcon(image);
		}
	 } 
	
	
	
	/**
	 * initialize the period, morning, afternoon, or evening, for the background, and greeting
	 * @return void
	 * */
	
    public  void initializeTimeAndPeriod(){
    	cur = d_f.format(new Date());
    	if(cur.substring(9,11).equals("AM")){
    		period = "Morning";
    	}
    	else{
    		if(Integer.parseInt(cur.substring(0, 2)) == 12 || Integer.parseInt(cur.substring(0, 2)) <=6 ){
    			period = "Afternoon";
    		}
    		else{
    			period = "Evening";
    		}
    	}
    }
}
