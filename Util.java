//Util.java
//Neel Kadikar, Arsal Gazi
//Util.java is used in our program for random

public class Util{
	
	public static int randint(int a, int b){
		return (int)(Math.random() * (b - a + 1) + a);
	}
}