import javax.swing.*;

import java.util.Timer;
import java.sql.*;
import java.awt.event.*; //for event listener
import java.awt.*; // for color
import java.util.Date;
import java.text.SimpleDateFormat;

public class RenderHomePage {
	private  int menuNum;
	private String activeMenu; 
	private String usr;
	private String cur;
	private String period; //contains Morning, Afternoon, or Evening 
	private SimpleDateFormat d_f =  new SimpleDateFormat ("hh:mm:ss a E ',' MM-dd-yyyy");
	private Data_Process d_p;
    private Manipulate_db m_p;
    private final int WINDOW_WIDTH = 1200;
	private final int WINDOW_HEIGHT = 800;
	private final int MIN_WIDTH = 900;
	private final int MIN_HEIGHT = 600;
	private final int MAX_WIDTH = 1200;
	private final int MAX_HEIGHT = 800;
	private final int LOGIN_BG_WIDTH = 1200; 
	private final int LOGIN_BG_HEIGHT = 700; 
	private final String yellowSeatUrl = "yellow_seat_icon.gif";
	private final String blueSeatUrl = "blue_seat_icon.gif";
	private final String brownSeatUrl = "brown_seat_icon.gif";
	private final String graySeatUrl = "gray_seat_icon.gif";
   
   
   
    //GUI Objects
    private JFrame homeFrame;
    private JPanel headerPanel;
    private JLayeredPane centerPanel;
    private JPanel rightSideBarPanel;
    private JPanel right_p[];
    private JPanel mid_p;
    private JPanel mid_p_h;
    private JPanel mid_p_b;
    private JPanel mid_p_f;
    private JPanel curActiveCancelPanel;
    private JPanel notificationPanel;
    private JLabel notificaitonLabel;
    private JPanel greetLeft;
    private JPanel greetRight;
    private JLabel greetLeft_1;
    private JLabel greetRight_1;
    private JLabel greetRight_2;
    private JLabel greetRight_3;
    private JLabel bgLabel = new JLabel();
    private JLabel mid_p_h_l;
    private JLabel mid_p_f_l_1;
    private JLabel mid_p_f_l_2;
    private JLabel mid_p_f_l_3; 
    private JLabel mid_p_f_l_4;
    private JPanel noti_panel;
 
    public RenderHomePage(String u,String active)throws SQLException{
    	initializeTimeAndPeriod();
    	activeMenu = active;
    	usr = u;
		homeFrame = new JFrame();
		d_p = new Data_Process();
		m_p = new Manipulate_db();
		homeFrame.setTitle("Welcome to The Ariline Ticket Purchase System");
		homeFrame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    	homeFrame.setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
    	homeFrame.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
    	homeFrame.setLayout(new BorderLayout());
    	homeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
    	buildPanel(active);	
    	homeFrame.setVisible(true);
    }
    
    /**
   	 * get the notification panel
   	 * @return JPanel
   	 * */
    
    private void initializeTimeAndPeriod(){
    	cur = d_f.format(new Date());
    	if(cur.substring(9, 11).equals("AM")){
    		period = "Morning";
    	}
    	else{
    		
    		if( Integer.parseInt(cur.substring(0, 2)) == 12 || Integer.parseInt(cur.substring(0, 2)) <=6 ){
    			period = "Afternoon";
    			
    		}
    		else{
    			period = "Evening";
    		}
    	}
    }
    
    
    /**
   	 * build the entire panel based on which one is active
   	 * @param avtive: String
   	 * @return void
   	 * */
    public void buildPanel(String active)throws SQLException{
    	
    	initializeMenuPanel("New Reservation","Manage Tickets","Log out");
    	headerPanel = new JPanel();
    	headerPanel.setBackground(Color.WHITE);
    	headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16)) ;
    	headerPanel.setLayout(new BorderLayout(10,10));	
    	centerPanel = new JLayeredPane();  //need for specifying the z dimension
    	centerPanel.setBackground(Color.WHITE);
    	
    	mid_p = new JPanel();
    	mid_p_h = new JPanel();
    	mid_p_b = new JPanel();
    	mid_p_f = new JPanel();
    
    	String imgUrl=m_p.getFolderPrefix();
		switch(period){
			case "Morning": imgUrl+= "Morning_sky.jpg";break;
			case "Afternoon": imgUrl+= "af_sky.jpg";break;
			default : imgUrl+= "night_sky.jpg";break;
		}
		ImageIcon image = new ImageIcon(imgUrl);
		//JLabel bgLabel = new JLabel(image);
		bgLabel.setIcon(image);
		greetLeft = new JPanel();
		greetRight = new JPanel();
		greetRight.setBackground(Color.WHITE);
		centerPanel.add(bgLabel,new Integer(20));
		bgLabel.setBounds(0,0,LOGIN_BG_WIDTH,LOGIN_BG_HEIGHT);
		
		greetLeft_1 =  new JLabel("Good " + period + " " + cur);
    	greetLeft_1.setFont(new Font("Serif", Font.BOLD, 14));
    	
    	
    	greetLeft.add(greetLeft_1);
    	greetLeft.setBackground(Color.WHITE);
    	
    	myTimer ts = new myTimer("ANIMATE_CLOCK",greetLeft_1,bgLabel);
    	Timer t = new Timer();
    	t.schedule(ts, 0, 1000);
    	
    	
    	greetRight_1= new JLabel( new ImageIcon(m_p.getFolderPrefix()+"home_icon.png"));
    	if(m_p.hasNotification(usr)){
    		greetRight_2 = new JLabel(new ImageIcon(m_p.getFolderPrefix()+"email_noti_icon.png"));
    	}
    	else{
    		greetRight_2 = new JLabel(new ImageIcon(m_p.getFolderPrefix()+"email_icon.png"));

    	}
    	
    	greetRight_2.addMouseListener(new notiIconMouseEvent());
    	
    	greetRight_3 = new JLabel(usr);
    	greetRight_3.setFont(new Font("Serif", Font.BOLD, 14));
    	
    	greetRight.add(greetRight_1);
    	greetRight.add(greetRight_2);
    	greetRight.add(greetRight_3);

    	//add top greets to the header panel
    	headerPanel.add(greetLeft,BorderLayout.WEST);
    	headerPanel.add(greetRight,BorderLayout.EAST);
    	noti_panel = returnNotificationPanel();
    	noti_panel.setVisible(false);
    	
    	centerPanel.add(noti_panel,new Integer(50));
    	
    	centerPanel.add(returnActive(active),new Integer(50));
    	centerPanel.add(rightSideBarPanel,new Integer(50));
    	homeFrame.add(headerPanel,BorderLayout.NORTH);
    	homeFrame.add(centerPanel,BorderLayout.CENTER);
    }
    
    
    /**
   	 * initialize the menu panel
   	 * @return void
   	 * */
    
    private void initializeMenuPanel(String...strings){
    	rightSideBarPanel = new JPanel();
    	rightSideBarPanel.setBackground(Color.BLACK);
    	rightSideBarPanel.setLayout(null);  //for absolute positioning
    	rightSideBarPanel.setBounds(0,0,160,600);
    	Insets insets = rightSideBarPanel.getInsets();
    	 menuNum = strings.length;
    	 right_p = new JPanel[menuNum];
    	for(int i = 0; i<menuNum ; i++){
    		
    		right_p[i] = new JPanel();
    		JLabel temp =new JLabel(strings[i]);
    		
    		temp.setFont(new Font("Serif",Font.BOLD, 14));
    		
    		temp.setName(strings[i]);
    		right_p[i].setBounds(0 + insets.left, (25 + 35*i) + insets.top,150,30);
    		right_p[i].setBackground(Color.BLACK);
    		//Add menu labels to those panel
    		if(activeMenu.equals(strings[i])){
    			temp.setForeground(Color.RED);
    		}
    		else{
    			temp.setForeground(Color.WHITE);
    		}
    		right_p[i].add(temp);
    		temp.addMouseListener(new menuMouseEvent());
    	}
    	for(int i=0; i<menuNum; i++){
    		rightSideBarPanel.add(right_p[i]);
    	}
    	
    }
    
    /**
 	 * get the notification panel
 	 * @return JPanel
 	 * */
    
    private JPanel returnNotificationPanel(){
    	JPanel noti_p = new JPanel();
    	JPanel notification_h = new JPanel();
    	notification_h.setLayout(new BorderLayout());
    	
    	JLabel noti_h_1 = new JLabel("Notification",new ImageIcon(m_p.getFolderPrefix()+"white_message_icon.png"),SwingConstants.LEFT );
    	noti_h_1.setForeground(Color.WHITE);
    	noti_h_1.setFont(new Font("Serif",Font.BOLD,14));
    	JLabel noti_h_2=new JLabel();
    	
    	notification_h.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
    	notification_h.add(noti_h_1,BorderLayout.WEST);
    	notification_h.setBackground(new Color(156,5,20));
    	
    	JPanel notification_b = new JPanel();
    	notification_b.setBorder(BorderFactory.createEmptyBorder(10,5,10,5));
    	notification_b.setLayout(new FlowLayout(FlowLayout.LEFT));
    	notification_b.setBackground(Color.WHITE);
    	try{
    		if(m_p.hasNotification(usr)){
    			String newAssignedTk = m_p.getAutoAssignedTicket(usr);
    			JLabel notification_b_title = new JLabel("New Tikcet Available");
    	    	notification_b_title.setFont(new Font("Serif",Font.BOLD,15));
    	    	JLabel notification_b_body_1 = new JLabel("You have been automatically assigned to the");
    	    	JLabel notification_b_body_2 = new JLabel("seat " + newAssignedTk + ", view ticket detail at \"Manage Tickets\"");
    	    	notification_b.add(notification_b_title);
    	    	notification_b.add(notification_b_body_1);
    	    	notification_b.add(notification_b_body_2);
    		}
    		else{
    			JLabel noNewNoti = new JLabel("No new notification available");
    			notification_b.add(noNewNoti);
    		}
    	}catch(Exception ex){
    		JLabel noNewNoti = new JLabel("No new notification available");
			notification_b.add(noNewNoti);
    	}
    	
    	
    	
    	
    	

    	noti_p.setLayout(new BorderLayout());
    	
    	noti_p.add(notification_h,BorderLayout.NORTH);
    	noti_p.add(notification_b,BorderLayout.CENTER);
    	noti_p.setBackground(Color.WHITE);
    	noti_p.setBounds(880, 0, 320, 130);  
    	return noti_p;
    }
    
    
    /**
	 * get the new reservation panel
	 * @return JPanel
	 * */
    public JPanel returnNewReservationPanel(){
    	mid_p_f.setLayout(new FlowLayout(FlowLayout.LEFT));
    	mid_p_h_l =new JLabel("Airline Seating Plan");
    	mid_p_h_l.setForeground(Color.WHITE);
    	mid_p_h_l.setFont(new Font("Serif",Font.BOLD,14));
    	
		String firstClassIconUrl= m_p.getFolderPrefix()+yellowSeatUrl;
    	mid_p_f_l_1= new JLabel("First Class",new ImageIcon(firstClassIconUrl),SwingConstants.LEFT);
    	mid_p_f_l_1.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
    	
    	String BusinessClassIconUrl= m_p.getFolderPrefix()+blueSeatUrl;
    	mid_p_f_l_2= new JLabel("Business Class",new ImageIcon(BusinessClassIconUrl),SwingConstants.LEFT);
    	mid_p_f_l_2.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

    	String EconomicClassIconUrl=  m_p.getFolderPrefix()+brownSeatUrl;
    	mid_p_f_l_3= new JLabel("Economic Class",new ImageIcon(EconomicClassIconUrl),SwingConstants.LEFT);
    	mid_p_f_l_3.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

    	String OccupiedIconUrl= m_p.getFolderPrefix()+graySeatUrl;
    	mid_p_f_l_4= new JLabel("Occupied",new ImageIcon(OccupiedIconUrl),SwingConstants.LEFT);
    	
    	
    	mid_p.setBackground(Color.LIGHT_GRAY);
    	mid_p.setLayout(new BorderLayout());
    	mid_p.setBounds(200, 30, 600, 480);    	
    	mid_p_h.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
    	mid_p_h.setBackground(new Color(156,5,20));
    	mid_p_h.add(mid_p_h_l);
    
    	
    	mid_p_b.setBackground(Color.WHITE);
    	mid_p_b.setLayout(new GridLayout(14,7));
    	
    	mid_p_f.add(mid_p_f_l_1);
    	mid_p_f.add(mid_p_f_l_2);
    	mid_p_f.add(mid_p_f_l_3);
    	mid_p_f.add(mid_p_f_l_4);
    	mid_p_f.setBorder(BorderFactory.createEmptyBorder(6,16,6,16));
    	mid_p_f.setBackground(new Color(245,245,245));

    	mid_p.add(mid_p_h,BorderLayout.NORTH);
    	mid_p.add(mid_p_b,BorderLayout.CENTER);
    	mid_p.add(mid_p_f,BorderLayout.SOUTH);
    	
    	String seatImageUrl ;
    	
    	
		char cols='A'; 
		JLabel tempL;
		JPanel gridArrayPanel[][]=new JPanel[14][7];
		for(int i=0 ; i<=m_p.getTotalRows(); i++){
    		for(int j=0; j<=m_p.getTotalCols(); j++){
    			if(i<=3){
    				seatImageUrl=  m_p.getFolderPrefix()+yellowSeatUrl;
    			}
    			else if( i>=4 && i<=9){
    				seatImageUrl=  m_p.getFolderPrefix()+blueSeatUrl;
    			}
    			else{
    				seatImageUrl=  m_p.getFolderPrefix()+brownSeatUrl;
    					
    			}

    			gridArrayPanel[i][j] = new JPanel();
    			gridArrayPanel[i][j].setBackground(Color.WHITE);
    			
    			if(i == 0){
    				if(j != 0){
    					tempL = new JLabel(""+cols);
    					tempL.setForeground(Color.BLACK);
    					tempL.setFont(new Font("Serif", Font.BOLD, 14));
    					//add header label, A-F
    					gridArrayPanel[i][j].add(tempL);
    					cols++;
    				}
    				else{
    					tempL = new JLabel("");
    					tempL.setForeground(Color.BLACK);
    					gridArrayPanel[i][j].add(tempL);
    				}
    			}
    			else{
    				if(j == 0){
    					tempL = new JLabel("Row "+i);
    					tempL.setFont(new Font("Serif", Font.BOLD, 14));
    					tempL.setForeground(Color.BLACK);
    					gridArrayPanel[i][j].add(tempL);
    				}
    				else{
    					String t_n = i+""+d_p.convertColumnNumToChar(j);	
    					try{
    						if(!m_p.checkSeatAvailable(t_n)){
    							seatImageUrl= m_p.getFolderPrefix() + graySeatUrl;
    						}
    					}catch(Exception ex){
    						JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
    					}
    					
    					tempL = new JLabel(new ImageIcon(seatImageUrl));
    					tempL.addMouseListener(new seatMouseEvent());
    					tempL.setName(i+""+d_p.convertColumnNumToChar(j));
    					gridArrayPanel[i][j].add(tempL);

    				}
    			}
    		}
    	}
    	for(int i=0 ; i<=m_p.getTotalRows(); i++){
    		for(int j=0; j<=m_p.getTotalCols(); j++){
    			mid_p_b.add(gridArrayPanel[i][j]);
    		}
    	}
    	return mid_p;
    	
    }
    
    
    /**
	 * return manage ticket panel
	 * @return JPanel
	 * */
    
    public JPanel returnManageMyTicket()throws SQLException{
    	mid_p_h_l =new JLabel("Manage My Tickets");
    	mid_p_h_l.setForeground(Color.WHITE);
    	mid_p_h_l.setFont(new Font("Serif",Font.BOLD,14));
    	
    	String[] record = m_p.returnUserPurchaseRecord(usr);
    	String tmp;
    	int recordSize = record.length;
    	int temp_delimeter;
    	JPanel[] ticketWrapper = new JPanel[recordSize];
    	if(recordSize>=1){
    	for(int i = 0; i< recordSize; i++){
    		ticketWrapper[i] = new JPanel();
    		temp_delimeter = record[i].indexOf("|", 0);
    		
    		
    		String tk = record[i].substring(0, temp_delimeter); //13B, 2A
    		ticketWrapper[i].setName(tk); //give the JPanel a name, so we can refer it later 
    		ticketWrapper[i].setLayout(new BorderLayout());
    		ticketWrapper[i].setBounds(10,10,230,60);
    		ticketWrapper[i].setBackground(Color.WHITE);
    		
    		JPanel t_b =new JPanel(); //body
    		t_b.setLayout(new BorderLayout());
    		t_b.setBounds(5,5,200,40);
    		
    		// display ticket type info
    		JPanel tikcetTypeW = new JPanel();
    		tikcetTypeW.setBackground(new Color(245,245,245));
    		tikcetTypeW.setLayout(new FlowLayout(FlowLayout.LEFT));
    		JLabel tikectType = new JLabel("Ticket Type: ");
    		JLabel ticketTypeText = new JLabel(parseTicketToClass(tk));
    		
    		tikectType.setFont(new Font("Serif", Font.BOLD,14));
    		tikcetTypeW.add(tikectType);
    		tikcetTypeW.add(ticketTypeText);

    		
    		// display seat position info
    		JPanel seatPositionW = new JPanel();
    		seatPositionW.setBackground(new Color(245,245,245));
    		seatPositionW.setLayout(new FlowLayout(FlowLayout.LEFT));
    		JLabel seatPosition = new JLabel("Seat Position: ");
    		JLabel seatPositionText = new JLabel("Row " + tk.substring(0,tk.length()-1) +", \tColumn " + tk.substring(tk.length()-1) );
    		seatPosition.setFont(new Font("Serif", Font.BOLD,14));
    		seatPositionW.add(seatPosition);
    		seatPositionW.add(seatPositionText);
    		
    		//display purchase time info
    		JPanel purchaseW = new JPanel();
    		purchaseW.setBackground(new Color(245,245,245));

    		purchaseW.setLayout(new FlowLayout(FlowLayout.LEFT));
    		JLabel purchase = new JLabel("Purchased at: ");
    		tmp = record[i].substring(temp_delimeter+1,record[i].substring(temp_delimeter+1).length()-1);
    		JLabel purchaseText = new JLabel(tmp);
    		purchase.setFont(new Font("Serif", Font.BOLD,14));
    		purchaseW.add(purchase);
    		purchaseW.add(purchaseText);
    		
    		//add tikcetType, seatPosition and purchaseTime to the body
    		
    		t_b.add(tikcetTypeW,BorderLayout.NORTH);
    		t_b.add(seatPositionW,BorderLayout.CENTER);
    		t_b.add(purchaseW,BorderLayout.SOUTH);
    		t_b.setBackground(new Color(245,245,245));

    		t_b.setBorder(BorderFactory.createEmptyBorder(6,20,6,20));
    		ticketWrapper[i].add(t_b,BorderLayout.CENTER);
    		ticketWrapper[i].addMouseListener(new ticketManageMouseEvent());
    		}
    		for(int i = 0; i<recordSize; i++){
    			mid_p_b.add(ticketWrapper[i]);
    		}
    	
    	}
    	else{
    		JLabel t = new JLabel("No Purchased Record Available");
    		mid_p_b.add(t);
    	}
    	
    	
    	mid_p_h.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
		mid_p_h.setBackground(new Color(156,5,20));
		mid_p_h.add(mid_p_h_l);
		mid_p.setBackground(Color.WHITE);
		mid_p.setLayout(new BorderLayout());
		mid_p.setBounds(200, 30, 600, 480);
		mid_p_b.setLayout(new FlowLayout(FlowLayout.LEFT));
		mid_p_b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    	mid_p_b.setBackground(Color.WHITE);
    	mid_p.add(mid_p_h,BorderLayout.NORTH);
		mid_p.add(mid_p_b,BorderLayout.CENTER);
		return mid_p;
    	
    }
    
    
    /**
	 * determine the ticket class based on the ticket
	 * @param tk: String 
	 * @return String
	 * */
    
    public String parseTicketToClass(String tk){
    	int len = tk.length();
    	int row = Integer.parseInt(tk.substring(0,len-1));
    	if(row<=3){
    		return "First Class";
    	}
    	else if(row<=9){
    		return "Business Class";
    	}
    	else{
    		return "Economic Class";
    	}
    }
    
    
    /**
	 * determine the ticket class based on the ticket
	 * @param msg:String type:String
	 * @return void
	 * */
    public void triggerNotification(String msg, String type){
    	notificationPanel = new JPanel();
    	notificaitonLabel = new JLabel(msg);
    	if(type.equals("Error")){
    		notificationPanel.setBackground(Color.RED);
    	}
    	else if(type.equals("Success")){
    		notificationPanel.setBackground(Color.BLUE);
    	}
    	notificaitonLabel.setForeground(Color.WHITE);
    	notificationPanel.add(notificaitonLabel);
    	greetLeft.setVisible(false);
    	greetRight.setVisible(false);
    	notificationPanel.setVisible(true);
    	JPanel[] setV = new JPanel[2];
    	setV[0] = greetLeft;
    	setV[1] = greetRight;
    	headerPanel.add(notificationPanel,BorderLayout.CENTER);
    	myTimer ts = new myTimer("HIDE_NOTI_BANNER",notificationPanel,setV ); // Have to build your own TimerTask class, since the TimerTask is an abstract class
    	Timer t =new Timer();
    	t.schedule(ts, 5000); //the notification banner would hide after 3 seconds
    }
    
    
    
    /**
	 * determine the ticket class based on the ticket
	 * @param msg:String type:String
	 * @return void
	 * */
    public JPanel returnActive(String active)throws SQLException{
    	JPanel ac = new JPanel();
    	switch(active){
 		 		case "New Reservation":ac=returnNewReservationPanel();break;
 		 		default: ac=returnManageMyTicket();break;
 			}
 		 return ac;
 	}
    
    public class menuMouseEvent implements MouseListener{
 	   public void mouseClicked(MouseEvent e){
 		 String menu = ((JLabel)e.getComponent()).getName();
 		 try{
 		 switch(menu){
 		 		//initializeMenuPanel("New Reservation","Manage Tickets","My Calendar","Personal Info","News & Notification");
 		 		case "New Reservation":
 		 			homeFrame.dispose();
 		 			RenderHomePage reservationBody = new RenderHomePage(usr,"New Reservation");
 		 			break;
 		 			
 		 		case "Manage Tickets":
 		 			homeFrame.dispose();
 		 			RenderHomePage ticketBody = new RenderHomePage(usr,"Manage Tickets");
 		 			break;
 		 		case "My Waiting List":
 		 			//homeFrame.dispose();
 		 			break;
 		 		default: 
 		 			homeFrame.dispose();
		 			RenderLoginPage redr=new RenderLoginPage();
		 			break;
 		 	}
 		 }catch(Exception ex){
 			 JOptionPane.showMessageDialog(null, "Error: "+ex.getMessage()); 
 		 }
 		 
 		   
 		   
 	   }
 	   public void mousePressed(MouseEvent e){
 	   }
 	    public void mouseReleased(MouseEvent e){
 	
 	    }

 	    
 	    public void mouseEntered(MouseEvent e){
 	    	 Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
 	    	 e.getComponent().setCursor(cursor);
 	    	 e.getComponent().setForeground(Color.RED);
 	    }
 
 	    public void mouseExited(MouseEvent e){
 	    	Cursor cursor = Cursor.getDefaultCursor();
	    	 e.getComponent().setCursor(cursor);
	    	 if(!((JLabel)e.getComponent()).getText().equals(activeMenu)){
	    		 e.getComponent().setForeground(Color.WHITE);
	    	 }
 	    	

 	    	 
 	    }
 	 } 
     
    /**
   	 * class of the mouseEvent listener for seat icon
   	 * 
   	 * */ 
    
    public class seatMouseEvent implements MouseListener{
  	   public void mouseClicked(MouseEvent e){
  		 String targetTicket =  e.getComponent().getName();
		String seatImageUrl= m_p.getFolderPrefix() + graySeatUrl;
  		 try{
  			 if( m_p.checkSeatAvailable(targetTicket)){
  				if(m_p.purchaseTicket(usr,targetTicket)){
  					((JLabel) e.getComponent()).setIcon(new ImageIcon(seatImageUrl));
  					triggerNotification("You have successfully purchased the ticket of seat \"" + targetTicket + "\"","Success");
  				}
  				
  		 	}
  			else{
  				
  				if(!m_p.checkSeatFull()){
  					triggerNotification("The seat \"" + targetTicket + "\" has been occupied","Error");
  				}
  				else{
  					if(!m_p.checkInWatingListOrNot(usr)){
  						int dialogResult = JOptionPane.showConfirmDialog (null, "All the seats has been occupied. Do you want to be in the waiting list?","Reminder from Airline Purchased System",JOptionPane.YES_NO_OPTION);
  						if(dialogResult == JOptionPane.YES_OPTION){
  							if(m_p.insertWaitingList(usr)){
  								triggerNotification("You have been in the waiting list, and you will be notified when any seat is available","Success");
  							}
  							else{
  								triggerNotification("Error, failed in adding to waiting list, please try it later","Error");
  							}
  						}
  					}
  					else{
						triggerNotification("You have been in the waiting list, and you will be notified when any seat is available","Success");
  					}
  				}
  			}
  		 }catch(Exception ex){
	  			 JOptionPane.showMessageDialog(null, "Error: " +ex.getMessage() );

  		 }
  		 
  	   }
  	   public void mousePressed(MouseEvent e){

  	   }
  	    public void mouseReleased(MouseEvent e){
  	
  	    }

  	    
  	    public void mouseEntered(MouseEvent e){
  	    	 Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
  	    	 e.getComponent().setCursor(cursor);
  	    }
  
  	    public void mouseExited(MouseEvent e){
  	    	Cursor cursor = Cursor.getDefaultCursor();
 	    	 e.getComponent().setCursor(cursor);
  	    }
  	 } 
   
    /**
   	 * class of the mouseEvent listener for the ticket information container
   	 * 
   	 * */
    public class ticketManageMouseEvent implements MouseListener{
  	   public void mouseClicked(MouseEvent e){
  		   
  	   }
  	   public void mousePressed(MouseEvent e){
  	   }
  	    public void mouseReleased(MouseEvent e){
  	
  	    }

  	    
  	    public void mouseEntered(MouseEvent e){
  	    	JPanel t_f = new JPanel(); //footer, contains cancel reservation
  	    	t_f.addMouseListener(new cancelReservationMouseEvent());
  	    	t_f.setLayout(new FlowLayout(FlowLayout.CENTER));
    		t_f.setBackground(new Color(156,5,20));
    		JLabel cancelLabel = new JLabel("Cancel Reservation");
    		cancelLabel.setForeground(Color.WHITE);
    		cancelLabel.setFont(new Font("Serif",Font.BOLD,14));
    		t_f.add(cancelLabel);
    		curActiveCancelPanel = t_f;
    		((JPanel)e.getSource()).add(t_f,BorderLayout.SOUTH);
    		Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
    		t_f.setCursor(cursor);
    		curActiveCancelPanel.setName(e.getComponent().getName());
    		
  	    }
  
  	    public void mouseExited(MouseEvent e){
  	    }
  	 } 
      
    
    /**
   	 * class of the mouseEvent listener for cancel button
   	 * 
   	 * */
    
    public class cancelReservationMouseEvent implements MouseListener{
    	  public void mouseClicked(MouseEvent e){
    		  String tk = e.getComponent().getName();
    		  int dialogResult = JOptionPane.showConfirmDialog (null, "Are you sure to cancel the reservation for ticket \"" + tk + "\"","Cancel Reservation ",JOptionPane.YES_NO_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					try{
						if(m_p.cancelReservation(usr,tk)){
							e.getComponent().getParent().removeAll();
							triggerNotification("You have successfully canceled the reservation", "Success");
							
						}
						else{
							triggerNotification("Cannot cancel the reservation, please try it later", "Error");
						}
					}catch(Exception ex){
						triggerNotification("Cannot cancel the reservation, please try it later", "Error");
						System.out.println(ex.getMessage());

					}
				}
				
     	  }
     	  public void mousePressed(MouseEvent e){
     	  }
     	  public void mouseReleased(MouseEvent e){
     	  }
     	  public void mouseEntered(MouseEvent e){
     	  }
     	  public void mouseExited(MouseEvent e){
     	   	e.getComponent().getParent().remove(e.getComponent());
     	  }
   	 }
    
    
    /**
   	 * class of the mouseEvent listener for notification icon
   	 * 
   	 * */
    
    public class notiIconMouseEvent implements MouseListener{
  	   public void mouseClicked(MouseEvent e){  
  		 //update the notification and set the notification to "viewed"
  		 try{
  			noti_panel.setVisible(true);
  	  		m_p.updateNotificaiton(usr);
  	  		((JLabel)e.getComponent()).setIcon( new ImageIcon(m_p.getFolderPrefix()+"email_icon.png")); 
  		 }catch(Exception ex){
  			JOptionPane.showMessageDialog(null, "Error: "+ex.getMessage());
  		 }
  	   }
  	   public void mousePressed(MouseEvent e){
  	   }
  	    public void mouseReleased(MouseEvent e){
  	
  	    }

  	    public void mouseEntered(MouseEvent e){
  	    	 Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
  	    	 e.getComponent().setCursor(cursor);
  	    }
  
  	    public void mouseExited(MouseEvent e){
  	    	Cursor cursor = Cursor.getDefaultCursor();
 	    	 e.getComponent().setCursor(cursor);
 	    }
  	 } 
    
   
    
	
}
