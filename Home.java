package Assignment1;

public class Home 
{
	public static void main(String[] args) 
	{
		String fileName = "/home/kabeer/Documents/NLP/A1_Files/english_sentences.txt";
		Functionalities FObj = new Functionalities();
		FObj.calculateNGrams(fileName);
		fileName="/home/kabeer/Documents/NLP/A1_Files/hindi_sents.txt";
		FObj=new Functionalities();
		FObj.calculateNGrams(fileName);
		fileName="/home/kabeer/Documents/NLP/A1_Files/telugu_sents.txt";
		FObj=new Functionalities();
		FObj.calculateNGrams(fileName);
		fileName="/home/kabeer/Documents/NLP/A1_Files/malayalam_sents.txt";
		FObj=new Functionalities();
		FObj.calculateNGrams(fileName);
	}
}
