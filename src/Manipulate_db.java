/*
 *  Class Manipulate_db is responsible for communicating with the database
 * 
 *  @author  Kesong Xie
 * 	@version 1.0
 * 	@since   2014-12-4 
 **/



import java.sql.*;
import java.util.Date;
import java.text.SimpleDateFormat;
public class Manipulate_db {
	private String folderPrefix = "/Users/KesongXie/Desktop/java/Plane Seating Manager with GUI/src/media/";
	private Connection con;
	private Statement st;
	private PreparedStatement pst;
	private ResultSet rs;
	private SimpleDateFormat d_f =  new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	private final int totalRows = 13;
	private final int totalCols = 5;

	public Manipulate_db() throws SQLException{
		try{
			con = DriverManager.getConnection("jdbc:mysql://localhost:8889/airplane","root","root");
			st = con.createStatement();
			
		}catch(Exception ex){
			System.out.println("Error: "+ex.getMessage());	
		}	
	}
	
	public String getFolderPrefix(){
		return folderPrefix;
	}
	
	public int getTotalRows(){
		return totalRows;
	}
	
	
	public int getTotalCols(){
		return totalCols;
	}
	
	
	/**
	 * check whether the username and password match or not
	 * @param u:String, p:char
	 * @return boolean
	 * */
	
	public boolean validateIdentity(String u, char[] p)throws SQLException{
		String pswS = "";
		for(int i = 0; i< p.length; i++){
			pswS+=p[i];
		}
		
		String query = "SELECT `id` FROM `user_info` WHERE username = '" + u + "' AND password='" + pswS + "' LIMIT 1" ;
		ResultSet result = st.executeQuery(query);
		if(!result.next()){
			return false;
		}
		else{
			return true;
		}
	
	
	}
	
	
	
	/**
	 * check whether the username has been used or not for the sign up form
	 * @param u:String
	 * @return boolean
	 * */
	
	public boolean checkUserExisted(String u)throws SQLException{
		String query = "SELECT `id` FROM `user_info` WHERE username = '"+ u + "'"  ;
		ResultSet result =  st.executeQuery(query);
		if(!result.next()){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	/**
	 * register new username and setup the password
	 * @param u:String p:String
	 * @return boolean
	 * */
	
	public boolean registerUser(String u, String p)throws SQLException{
		boolean returnValue=false;
		pst = con.prepareStatement("INSERT INTO user_info (username,password) VALUES(?,?) ");
		pst.setString( 1, u);
		pst.setString( 2, p);
		if(pst.executeUpdate()==1){
			returnValue = true;
		}
		else{
			returnValue = false;
		}
		return returnValue;
	}
	
	
	
	/**
	 * check whether the any seat is available or not
	 * 
	 * @return boolean
	 * */
	public boolean checkSeatFull()throws SQLException{
		int count = 0; 
		String query =  "SELECT id FROM purchaseHistory";
			ResultSet result =  st.executeQuery(query);
			while(result.next()){
				count++;
			}
			if(count  == (totalRows * totalCols)){
				return true;	
			}		
			else{
				return false;
			}
	}
	
	/**
	 * check specific seat is availabe or not
	 * @param t:String
	 * @return boolean
	 * */
	
	public boolean checkSeatAvailable(String t)throws SQLException{
	    String query =  "SELECT id FROM purchaseHistory WHERE ticket = '" + t+ "'";
		ResultSet result =  st.executeQuery(query);
		if(!result.next()){
			return true;
		}
		else{
			return false;
		}
		
	}
	/**
	 * purchase the ticket for specific user
	 * @param usr:String t:String
	 * @return boolean
	 * */
	
	public boolean purchaseTicket(String usr, String t)throws SQLException{
		boolean returnValue=false;
		Date cur =new Date();
		if(checkSeatAvailable(t)){
			pst = con.prepareStatement("INSERT INTO purchaseHistory (username,ticket,time) VALUES(?,?,?) ");
			pst.setString( 1, usr);
			pst.setString( 2, t);
			pst.setString(3, d_f.format(cur));
			if(pst.executeUpdate()==1){
				returnValue = true;
			}
			else{
				returnValue = false;
			}
			return returnValue;
		}
		else{
			return returnValue;
		}
	}
	
	
	
	/**
	 * insert the user into the waiting list
	 * @param usr:String 
	 * @return boolean
	 * */
	public boolean insertWaitingList(String usr)throws SQLException{
		boolean returnValue=false;
		pst = con.prepareStatement("INSERT INTO waitingList (username) VALUES(?) ");
		pst.setString(1, usr);
		if(pst.executeUpdate()==1){
			returnValue = true;
		}
		else{
			returnValue = false;
		}
		return returnValue;
	}
	
	
	/**
	 * check whether the user is in the waiting list or not
	 * @param usr:String
	 * @return boolean
	 * */
	public boolean checkInWatingListOrNot(String usr)throws SQLException{
		String query = "SELECT id FROM waitingList WHERE username='" +usr + "'";
		ResultSet result = st.executeQuery(query);
		if(!result.next()){
			return false;
		}
		else{
			return true;
		}
	}
	
	/**
	 * get the purchased recrod for the specific user
	 * @param usr:String
	 * @return boolean
	 * */
	
	public String[] returnUserPurchaseRecord(String usr)throws SQLException{
		String query = "SELECT ticket,time FROM purchaseHistory WHERE username='" + usr + "'ORDER BY id DESC";
		ResultSet result = st.executeQuery(query);
		int recordInt = 0;
		String[] record =new String[78];
		while(result.next()){
			record[recordInt] = result.getString("ticket") +"|"+result.getString("time");
			recordInt++;
		}
		
		
		//get rid of the uninitialized element.
		String[] recordTrim = new String[recordInt];
		for (int i = 0; i < recordInt ; i++){
			recordTrim[i] = record[i];
		}
		return recordTrim;
	
	}
	
	
	/**
	 * cancel the reservation for the user
	 * @param usr:String tk:String
	 * @return boolean
	 * */
	public boolean cancelReservation(String usr, String tk)throws SQLException{
		boolean updateWaitinglist = false; 
		if(checkSeatFull()){
			updateWaitinglist = true;
		}
		
		String query = "DELETE FROM purchaseHistory WHERE username='" + usr + "' AND ticket ='"+ tk +"' LIMIT 1";
		int deleteRow = st.executeUpdate(query);
		if(deleteRow == 1){
			if(updateWaitinglist){
				purchaseTicketFromWaitingList(tk);
			}
			return true;
		}
		else{
			return false;
		}
		
		
	}
	
	/**
	 * remove the user from the waiting list
	 * @param usr:String
	 * @return boolean
	 * */
	public boolean removeUsrFromWaitingList(String usr)throws SQLException{
		String query = "DELETE FROM waitingList WHERE username='" + usr + "' LIMIT 1";
		int deleteRow = st.executeUpdate(query);
		if(deleteRow == 1){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	/**
	 * automatically purchase ticket for the user in the waiting list once any ticket is available
	 * @param tk : String
	 * @return boolean
	 * */
	
	public void purchaseTicketFromWaitingList(String tk)throws SQLException{
		String query = "SELECT username FROM waitingList ORDER BY id DESC LIMIT 1"; 
		ResultSet result =con.createStatement().executeQuery(query); //the result set is closed after the previous delete statement, need to create a new statement
		while(result.next()){
			String user = result.getString("username");
			purchaseTicket(user,tk);
			insertNotification(user,tk);
			removeUsrFromWaitingList(user);
		}
		
	}
	
	
	/**
	 * once the user is automatically assigned a seat, insert a notification for the user for the available seat
	 * @param usr:String tk:String
	 * @return boolean
	 * */
	public boolean insertNotification(String usr, String tk)throws SQLException{
		boolean returnValue = false;
		String view = "n";
		pst = con.prepareStatement("INSERT INTO seat_notification (username,ticket,view) VALUES(?,?,?) ");
		pst.setString( 1, usr);
		pst.setString( 2, tk);
		pst.setString( 3, view);
		
		if(pst.executeUpdate()==1){
			returnValue = true;
		}
		else{
			returnValue = false;
		}
		return returnValue;
	}

	
	/**
	 * check whether the user has any notification or not
	 * @param usr:String 
	 * @return boolean
	 * */
	
	public boolean hasNotification(String usr)throws SQLException{	
		String query = "SELECT username FROM seat_notification WHERE username='"+usr +"' AND view !='y' ORDER BY id DESC LIMIT 1"; 
		ResultSet result =st.executeQuery(query); 
		while(result.next()){
			return true;
		}
		return false;
	}
	
	/**
	 * get the auto assigned ticket for the user
	 * @param u:String p:String
	 * @return boolean
	 * */
	public String getAutoAssignedTicket(String usr)throws SQLException{	
		
		String query = "SELECT ticket FROM seat_notification WHERE username='"+usr +"' AND view !='y' ORDER BY id DESC LIMIT 1"; 
		ResultSet result =st.executeQuery(query);
		//has to use result.next(), otherwise, the "before start of result set" exception is thrown 
		while(result.next()){
			return result.getString("ticket");
		}
		return "";
	}
	
	/**
	 * update the notifaction once is checked by the user, and stop sending notification
	 * @param u:String p:String
	 * @return boolean
	 * */
	
	public void updateNotificaiton(String usr)throws SQLException{
		String query = "UPDATE seat_notification SET view = 'y' WHERE username='" + usr +"'";
		st.executeUpdate(query);	
	}
	
	

}
