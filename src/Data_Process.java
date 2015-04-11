
public class Data_Process {
	private boolean rg_status = false;
	private String rg_psw = "";
	public String rg_status_message;
	
	
	
	
	public boolean rg_pswValidate(char[] rg_p, char[] rg_re_p){
		String rg_s_p = "";
		String rg_re_s_p = "";
		int rg_p_length = rg_p.length;
		int rg_re_p_length = rg_re_p.length;
		
		
		
		
		if(rg_p_length < 6){
			rg_status = false; 
			rg_status_message = "The password need at least 6 characters";
		}
		else{
			if(rg_p_length == rg_re_p_length){
				for(int i=0; i < rg_p_length; i++){
					rg_s_p+=rg_p[i];
					rg_re_s_p+=rg_re_p[i];
				}
			
				if(!rg_s_p.equals(rg_re_s_p)){
					rg_status = false;
					rg_status_message = "The passwords you entered are not the same";
				}
				else{
					rg_psw=rg_s_p;
					rg_status = true;
				}
			}
			else{
				rg_status = false;
				rg_status_message = "The passwords you entered are not the same";
			}
		}
		return rg_status;
	}
	
	public String getRgPsw(){
		return rg_psw;
	}
	
	public char convertColumnNumToChar(int num){
		switch(num){
			case 1: return 'A';
			case 2: return 'B';
			case 3: return 'C';
			case 4: return 'D';
			default: return 'E';
			
		}
	}
	
	
}
