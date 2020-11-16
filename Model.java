import java.util.*;
import java.lang.Math;
import java.time.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Model {
	

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Instant start = Instant.now();
		write();
		Instant end = Instant.now();
		Duration interval = Duration.between(start,end);
		System.out.println("Execution time in seconds: " + interval.getSeconds());
		//System.out.println(output.size());
		/*ArrayList<Double> output = decision(-33,0);
		System.out.println(output);*/
		
		
	}
	public static ArrayList<ArrayList<Double>> control()
	{
		ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> temp;
		for(double x = -33; x<34; x++)
		{
			for(double y = 0; y<36; y++)
			{
				//System.out.println("x is: " + x);
				//System.out.println("y is: " +y);
				temp = decision(x,y);
				if(temp!=null)
				{
					output.add(temp);
				}
			}
		}
		return output;
	}
	public static double Distance_to_goal(double x, double y, boolean left)
	{
		if(left == true)
		{
			return Math.sqrt((x+3.55)*(x+3.55)+ (y-52.5)*(y-52.5));
		}
		else
		{
			//System.out.println("it runs here");
			return Math.sqrt(Math.pow(x-3.55, 2)+ Math.pow(y-52.5, 2));
		}
	}	
	public static double horizontal_angle(double x, double y, boolean left)
	{
		double angle;	
		if(left == true)
		{
			angle = Math.atan(Math.abs(y-52.5)/Math.abs(x+3.55));
			return 90-angle*180/Math.PI;
		}
		else
		{
			angle = Math.atan(Math.abs(y-52.5)/Math.abs(x-3.55));
			return 90-angle*180/Math.PI;
		}
	}
	public static ArrayList<Double> projectile(double distance)
	{
		double max_velocity = 33;
		double defensive_location = 9.144;
		double defensive_height = 1.824;
		double gravity = 9.81;
	    ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
	    if(distance/2 > max_velocity){
	    	return null;}
		for(double V_horizon = distance/2; V_horizon<=33; V_horizon = V_horizon + 0.01){
			for(double V_vi=0.01; V_vi <= 33; V_vi = V_vi + 0.01){
				double d_vertical = (V_vi*defensive_location/V_horizon)-
						0.5*gravity*(defensive_location/V_horizon)
						*(defensive_location/V_horizon);//check if the ball passes the defensive location
				if(d_vertical > defensive_height)//check if the ball passes the defensive location
				{
					d_vertical = (V_vi*distance/V_horizon)-0.5*gravity*
							(distance/V_horizon)*(distance/V_horizon);
					if(d_vertical>2 && d_vertical<2.33) // check if the ball passes through the corner
					{
						double overall_velocity = 
								Math.sqrt(V_vi*V_vi + V_horizon*V_horizon);//make sure the overall velocity
																		
						if(overall_velocity<=33){
							ArrayList<Double> pair = new ArrayList<Double>();
							pair.add(V_horizon);
							pair.add(V_vi);
							list.add(pair);}
						}
					}
				}
			}
		if(list.size()!=0){
			ArrayList<Double> max = list.get(0);
			for(int i=0;i<list.size();i++){
				double curr_horizon = list.get(i).get(0);
				double curr_vertical = list.get(i).get(1);
				double max_horizon = max.get(0);
				double max_vertical = max.get(1);
				double max_overall = Math.sqrt(max_horizon*max_horizon + max_vertical*max_vertical);
				double curr_overall = Math.sqrt(curr_horizon*curr_horizon+curr_vertical*curr_vertical);
				if(curr_overall>max_overall){
					max = list.get(i);}
			}
			return max;}
		else{
			return null;}
	}
	public static ArrayList<Double> decision(double x, double y)
	{
		double right_horizon=0;
		double right_vertical=0;
		double right_overall=0;
		double left_horizon=0;
		double left_vertical=0;
		double left_overall=0;
		
		double right_distance = Distance_to_goal(x,y,false);
		double right_horizontal_angle = horizontal_angle(x,y,false);
		ArrayList<Double> right_velocity_set = projectile(right_distance);
		if(right_velocity_set != null)
		{
			right_horizon = right_velocity_set.get(0);
			right_vertical = right_velocity_set.get(1);
			right_overall = Math.sqrt(right_horizon*right_horizon + right_vertical*right_vertical);
		}

		
		double left_distance = Distance_to_goal(x,y,true);
		double left_horizontal_angle = horizontal_angle(x,y,true);
		ArrayList<Double> left_velocity_set = projectile(left_distance);
		if(left_velocity_set != null)
		{
			left_horizon = left_velocity_set.get(0);
			left_vertical = left_velocity_set.get(1);
			left_overall = Math.sqrt(left_horizon*left_horizon + left_vertical*left_vertical);
		}

		
		ArrayList<Double> output = new ArrayList<Double>();
		if(right_velocity_set == null && left_velocity_set == null)
		{
			return null;
		}
		if(left_overall > right_overall)
		{
			double left_vertical_angle = Math.atan(left_vertical/left_horizon)*180/Math.PI;
			output.add(x);
			output.add(y);
			output.add(left_horizontal_angle);
			output.add(left_vertical_angle);
			output.add(left_overall);
			output.add(1.0);
			return output;			
		}
		else
		{
			double right_vertical_angle = Math.atan(right_vertical/right_horizon)*180/Math.PI;
			output.add(x);
			output.add(y);
			output.add(right_horizontal_angle);
			output.add(right_vertical_angle);
			output.add(right_overall);
			output.add(0.0);
			return output;	
		}
	}
	public static void write() throws FileNotFoundException
	{
		ArrayList<ArrayList<Double>> result = control();
		try(PrintWriter writer = new PrintWriter(new File("output.csv")))
		{
			StringBuilder sb = new StringBuilder();
			sb.append("X");
			sb.append(",");
			sb.append("Y");
			sb.append(",");
			sb.append("Horizontal_Angle");
			sb.append(",");
			sb.append("Vertical_Angle");
			sb.append(",");
			sb.append("Velocity");
			sb.append(",");
			sb.append("IfLeft");
			sb.append("\n");
			for(int i=0; i< result.size();i++)
			{
				for(int j=0; j<result.get(i).size()-1;j++)
				{
					sb.append(result.get(i).get(j));
					sb.append(",");
				}
				sb.append(result.get(i).get(result.get(i).size()-1));
				sb.append("\n");
			}
			writer.write(sb.toString());
			System.out.println("done!");
			
		} catch(FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
