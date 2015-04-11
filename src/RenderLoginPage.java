/** class for rendering the login page 
 *
 * */
import javax.swing.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.awt.event.*; //for event listener
import java.awt.*; // for color


public class RenderLoginPage extends JFrame {
	private SimpleDateFormat d_f =  new SimpleDateFormat ("hh:mm:ss a E ',' MM-dd-yyyy");
	private String cur;
	private String period; //contains Morning, Afternoon, or Evening
    private Manipulate_db m_p;
	private JFrame loginFrame;
	private JPanel headerWrapper;
	private JPanel left_headerWrapper;
	private JPanel right_headerWrapper;
	private JPanel registerForm;
	private JPanel registerForm_banner;
	private JLayeredPane bodyWrapper; 
	private JLabel registerForm_banner_label;

	private JLabel topBannerLabel;
	private JLabel usrLabel;
	private JLabel pswLabel;
	private JLabel rg_usrLabel;
	private JLabel rg_pswLabel;
	private JLabel rg_re_pswLabel;
	private JTextField usrField;
	private JPasswordField pswField;
	private JTextField rg_usrField;
	private JPasswordField rg_pswField;
	private JPasswordField rg_re_pswField;
	private JButton loginBtn;
	private JButton rigisterBtn;
	private Data_Process DataProcess; 
	private final int WINDOW_WIDTH = 1200;
	private final int WINDOW_HEIGHT = 800;
	private final int MIN_WIDTH = 900;
	private final int MIN_HEIGHT = 600;
	private final int MAX_WIDTH = 1200;
	private final int MAX_HEIGHT = 800;
	private final int LOGIN_BG_WIDTH = 1200; 
	private final int LOGIN_BG_HEIGHT = 700; 

	

	/**
	 * Constructor for initialize the layout
	 * 
	 * **/
	
	public RenderLoginPage()throws SQLException{
    	initializeTimeAndPeriod();
    	m_p = new Manipulate_db();
		DataProcess = new Data_Process();
		loginFrame = new JFrame();
		loginFrame.setLayout(new BorderLayout()); //use BorderLayout 
		loginFrame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
		loginFrame.setMaximumSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
		loginFrame.setTitle("Login to Airline ticket purchased system");
		loginFrame.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		buildOutSideWrapperPanel();
		loginFrame.add(headerWrapper,BorderLayout.NORTH);
		loginFrame.add(bodyWrapper,BorderLayout.CENTER);
		loginFrame.setVisible(true);	
	}
	
	

	/**
	 * initialize the period, morning, afternoon, or evening, for the background, and greeting
	 * @return void
	 * */
	
	private void initializeTimeAndPeriod(){
	    	cur = d_f.format(new Date());
	    	if(cur.substring(9, 11).equals("AM")){
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
	
	

	/**
	 * build the login panel
	 * @return void
	 * **/
	
	public void buildOutSideWrapperPanel(){
		topBannerLabel = new JLabel("Good " + period + " " + cur);
		topBannerLabel.setFont(new Font("Serif", Font.BOLD, 14));
		registerForm = new JPanel();
		registerForm.setBackground(Color.WHITE);
		registerForm.setLayout(null); //for absolute position
		Insets insets = registerForm.getInsets(); //for absolute position
		registerForm_banner = new JPanel();
		usrLabel = new JLabel("Username: ");
		pswLabel = new JLabel("Password: ");
		rg_usrLabel = new JLabel("Username: ");
		rg_pswLabel = new JLabel("Password: ");
		rg_re_pswLabel = new JLabel("Re-Password: ");
		registerForm_banner_label = new JLabel("Register Account",new ImageIcon(m_p.getFolderPrefix()+"plane_icon.png"),SwingConstants.LEFT);
		registerForm_banner_label.setForeground(Color.WHITE);
		usrField = new JTextField(10);
		pswField = new JPasswordField(10);
		rg_usrField = new JTextField(10);
		rg_pswField = new JPasswordField(10);
		rg_re_pswField = new JPasswordField(10);
		loginBtn = new JButton("Login");
		loginBtn.addActionListener(new loginBtnListener());
		
		//register button
		rigisterBtn = new JButton("Register");
		rigisterBtn.addActionListener(new registerBtnListener());
		headerWrapper  = new JPanel();
		
		headerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16)) ;
		headerWrapper.setBackground(Color.WHITE);
		headerWrapper.setLayout(new BorderLayout());
		
		left_headerWrapper  = new JPanel();
		left_headerWrapper.setBackground(Color.WHITE);


		right_headerWrapper  = new JPanel();
		right_headerWrapper.setBackground(Color.WHITE);

		bodyWrapper  = new JLayeredPane();
		//header includes Logo, login fields
		left_headerWrapper.add(topBannerLabel);
		
		right_headerWrapper.add(usrLabel);
		right_headerWrapper.add(usrField);
		right_headerWrapper.add(pswLabel);
		right_headerWrapper.add(pswField);
		
		headerWrapper.add(left_headerWrapper,BorderLayout.WEST);
		headerWrapper.add(right_headerWrapper,BorderLayout.EAST);
		
		right_headerWrapper.add(loginBtn);
		
		String imgUrl=m_p.getFolderPrefix();
		
		switch(period){
			case "Morning": imgUrl+= "Morning_sky.jpg";break;
			case "Afternoon": imgUrl+= "af_sky.jpg";break;
			default : imgUrl+= "night_sky.jpg";break;
		}
		ImageIcon image = new ImageIcon(imgUrl);
		JLabel bgLabel = new JLabel(image);
		myTimer ts = new myTimer("ANIMATE_CLOCK",topBannerLabel,bgLabel);
    	Timer t = new Timer();
    	t.schedule(ts, 0, 1000);
		
		//specify different z dimension for background, and register form
		bodyWrapper.add(bgLabel,new Integer(20));  //prevent from overlapping
		bodyWrapper.add(registerForm,new Integer(50));

		//manually set the size of background and form
		bgLabel.setBounds(0,0,LOGIN_BG_WIDTH,LOGIN_BG_HEIGHT);
		registerForm.setBounds(800,100,300,220);
		registerForm.add(registerForm_banner);
		
		//username textfield and label
		registerForm.add(rg_usrField);
		registerForm.add(rg_usrLabel);
		
		//password textfield and label
		registerForm.add(rg_pswField);
		registerForm.add(rg_pswLabel);
		
		//re-password textfield and label
		registerForm.add(rg_re_pswField);
		registerForm.add(rg_re_pswLabel);
		
		//register button
		registerForm.add(rigisterBtn);
		
		registerForm_banner.setLayout(new FlowLayout(FlowLayout.LEFT));
		registerForm_banner.setBounds(0,0, 300, 30);
		registerForm_banner.setBorder(BorderFactory.createEmptyBorder(4,6,4,15));
		registerForm_banner.add(registerForm_banner_label);
		registerForm_banner.setBackground(new Color(156,5,20));
		registerForm_banner_label.setFont(new Font("Serif", Font.BOLD, 14));
		//set the position and dimension for username textfield and label
		rg_usrLabel.setBounds(20,50,150,30);
		rg_usrField.setBounds(100,50,180,30);
		
		//set the position and dimension for password textfield and label
		rg_pswLabel.setBounds(20,90,150,30);
		rg_pswField.setBounds(100,90,180,30);
		
		//set the position and dimension for password textfield and label
		rg_re_pswLabel.setBounds(10,130,150,30);
		rg_re_pswField.setBounds(100,130,180,30);
		
		
		//set the position and dimension for the register button
		rigisterBtn.setBounds(203,170,80,30);
	}
	
	
	
	
	

	/**
	 * actionListener class for login button
	 * 
	 * **/
	private class loginBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
				if(m_p.validateIdentity(usrField.getText(),pswField.getPassword() ) ){
					loginFrame.dispose();
					RenderHomePage h = new RenderHomePage(usrField.getText(),"New Reservation");
			    	
				}
				else{
					JOptionPane.showMessageDialog(null, "The username and password don't match");
				}
				
			}catch(Exception ex){
				System.out.println("Error: " + ex.getMessage());
			}
			
			
			
			
			
		}
	}
	
	
	/**
	 * actionListener class for register button
	 * 
	 * **/
	private class registerBtnListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			try{
				if(rg_usrField.getText().length()>0 && rg_pswField.getPassword().length>0 && rg_re_pswField.getPassword().length>0){
					if(rg_usrField.getText().length() >= 6){	
						if(m_p.checkUserExisted(rg_usrField.getText())){
							if(DataProcess.rg_pswValidate(rg_pswField.getPassword(), rg_re_pswField.getPassword())){
								
								m_p.registerUser(rg_usrField.getText(), DataProcess.getRgPsw() );
								successRegistered(rg_usrField.getText());
								
								//RenderHomePage h_p = new RenderHomePage();
								
							}
							else{
								JOptionPane.showMessageDialog(null, DataProcess.rg_status_message);
							}
						}
						else{
							JOptionPane.showMessageDialog(null, "The username has already existed");
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "The username should be at least six characters");
					}
				}
				else{
					JOptionPane.showMessageDialog(null, "Please fill in all the required fields");
				}
			}catch(Exception ex){
				System.out.println("Error: "+ex.getMessage());
			}
		}
		
		/**
		 * show response after successfully registeresd
		 * @return void
		 * **/
		public void successRegistered(String u){
			registerForm.setVisible(false);
			registerForm.removeAll();
			
			JPanel suc_h = new JPanel();
			suc_h.setLayout(new FlowLayout(FlowLayout.LEFT));
			suc_h.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
			JLabel suc_h_l = new JLabel("Register Successfully");
			suc_h.setBackground(new Color(156,5,20));
			suc_h_l.setForeground(Color.WHITE);
			suc_h_l.setFont(new Font("Serif", Font.BOLD, 14));
			suc_h.add(suc_h_l);
			
			JLabel suc_1 = new JLabel("You have successfully registered",new ImageIcon(m_p.getFolderPrefix()+"check_icon.png"),SwingConstants.LEFT);
			JLabel suc_2 = new JLabel("Please login use your username");
			JLabel suc_3 = new JLabel("\"" + u + "\"");
			suc_h.setBounds(0,0,300,30);
			suc_1.setBounds(10,40,260,30);
			suc_2.setBounds(36,60,260,30);
			suc_3.setBounds(33,82,260,30);
			suc_3.setFont(new Font("Serif", Font.BOLD, 14));
			suc_3.setForeground(Color.BLUE);
			registerForm.add(suc_h);		
			registerForm.add(suc_1);			
			registerForm.add(suc_2);
			registerForm.add(suc_3);
			registerForm.setVisible(true);	
		}
	}
	
	
	
	
	public static void main(String[] args) throws SQLException{
		RenderLoginPage redr=new RenderLoginPage();	
	}

}
