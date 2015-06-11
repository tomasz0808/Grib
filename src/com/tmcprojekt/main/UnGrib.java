package com.tmcprojekt.main;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;




public class UnGrib {
	
	
	
	public static ArrayList<Float> coorX = new ArrayList<Float>();
	public static ArrayList<Float> coorY = new ArrayList<Float>();
	public static ArrayList<Float> temp = new ArrayList<Float>();
	public static ArrayList<Float> wind_power = new ArrayList<Float>();
	public static ArrayList<Float> wind_angle = new ArrayList<Float>();
	public static ArrayList<Float> pressure = new ArrayList<Float>();
	
	public UnGrib(String filepath) throws IOException {
		// TODO Auto-generated constructor stub

		FileInputStream fstream = new FileInputStream(filepath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine = null;
		
		
		String[] linia1 = new String[100000];
		
		

		String[] reszta = new String[8];
		
		
		int j = 0;
		
		while ((strLine = br.readLine()) != null) {
			String[] array1 = strLine.split(" ");	
			if(j==0){
				for(int i=0;i<array1.length;i++){
					coorX.add(Float.parseFloat(array1[i]));
				}
				j++;
			}else if (j==1){
				for(int i=0;i<array1.length;i++){
					coorY.add(Float.parseFloat(array1[i]));
				}
				j++;
			}else if (j==2){
				for(int i=0;i<array1.length;i++){
					temp.add(Float.parseFloat(array1[i]));
				}
				j++;
			}else if (j==3 || j==4){
				j++;
			}else if (j==5){
				for(int i=0;i<array1.length;i++){
					wind_power.add(Float.parseFloat(array1[i]));
				}
				j++;
			}else if (j==6){
				for(int i=0;i<array1.length;i++){
					wind_angle.add(Float.parseFloat(array1[i]));
				}
				j++;
			}else if (j==7){
				for(int i=0;i<array1.length;i++){
					pressure.add(Float.parseFloat(array1[i]));
				}
				j++;
			}						
		}
		in.close();
		int Ycount=0;
		int Xcount=0;
		
//		int size = coorX.size()*coorY.size();
//		
//		Float[][] corXY = new Float[2][size];
//		int sizetable=0;
//		for (Float x : coorX){
//			for(Float y: coorY){
//				corXY[Xcount][Ycount]=();
//			}
//		}	
//	}
//	}
	
	}
	
	
	
	
	public static ArrayList<Float> getTemp(){	
		return temp;
	}
	public static ArrayList<Float> getXCor(){	
		return coorX;
	}
	public static ArrayList<Float> getYCor(){	
		return coorY;
	}
}
