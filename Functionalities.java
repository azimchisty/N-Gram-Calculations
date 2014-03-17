package Assignment1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.regex.Pattern;


class pair
{
	String word;
	double wordCount;
}

// TODO
public class Functionalities 
{
	int tokenCount;
	String outName;
	HashMap<String, Integer> uniGramHashMap = new HashMap<String, Integer>();
	HashMap<String, Integer> biGramHashMap = new HashMap<String, Integer>();
	HashMap<String, HashMap<String, Integer>> triGramHashMap = new HashMap<String, HashMap<String,Integer>>();
	BufferedWriter out;
	PriorityQueue< pair > nGramFrequencyQueue = new PriorityQueue<pair>(10,	new Comparator<pair>() {	
		@Override
		public int compare(pair s1,pair s2) 
		{			
			if(s1.wordCount > s2.wordCount)
				return -1;
			if(s1.wordCount < s2.wordCount)
				return 1;
			return 0;
		}
	});


	void calculateNGrams( String fileName )
	{
		this.outName=fileName.split("/")[fileName.split("/").length-1];
		int wordTypesCount = 0, wordCount;
		String line, prev1 = " ", prev2 = " ";
		String tokens[], key;
		String delims = "[ \\\\|_=,-;\\.\\/\n\t^]+";
		Pattern pat = Pattern.compile(delims);
		BufferedReader br = null; 
		tokenCount = 0;

		try
		{
			br = new  BufferedReader(new FileReader(fileName));
			StringBuilder prevStr = new StringBuilder();
			prevStr.append(" ");

			while(( line = br.readLine()) != null )
			{
				line=line.toLowerCase();
				tokens = pat.split( line );
				tokenCount += tokens.length;
				for( int i = 0; i < tokens.length; i++)
				{
					if( uniGramHashMap.containsKey(tokens[i]) )
					{
						wordCount = uniGramHashMap.get(tokens[i]);
						uniGramHashMap.put(tokens[i], ++wordCount);
					}
					else
					{
						uniGramHashMap.put(tokens[i], 1);
						wordTypesCount++;
					}


					prevStr.append("#" + tokens[i]);
					String tempStr = prevStr.toString().toLowerCase();

					if( biGramHashMap.containsKey(tempStr) )
					{
						wordCount = biGramHashMap.get(tempStr);
						biGramHashMap.put( tempStr, ++wordCount );
					}
					else
					{
						biGramHashMap.put( tempStr, 1 );
					}
					prevStr.setLength(0);
					prevStr.append(tokens[i]);
					key = prev1 + "#" + prev2;
					if( triGramHashMap.containsKey( key ))
					{
						if( triGramHashMap.get(key).containsKey(tokens[i]))
						{
							wordCount = triGramHashMap.get(key).get(tokens[i]);
							triGramHashMap.get(key).put( tokens[i], ++wordCount );
						}
						else
						{
							triGramHashMap.get(key).put(tokens[i], 1 );
						}
					}
					else
					{
						HashMap< String, Integer > tempHashMap = new HashMap< String, Integer >();
						tempHashMap.put( tokens[i], 1 );
						triGramHashMap.put(key, tempHashMap);
					}

					prev1 = prev2;
					prev2 = tokens[i];
				}
			}
			System.out.println("Types: " + wordTypesCount + "\nTokens: " + tokenCount);

			/*			System.out.println("biGramsize: "+ biGramHashMap.size());
			System.out.println("triGramsize: "+ triGramHashMap.size());
			 */	
			br.close();
			writeUniGramToFile();
			writeBiGramToFile();
			writeTriGramToFile();
			writeFrequencyUnigramFile();
			writeFrequencyBigramFile();
			writeFrequencyTrigramFile();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
	void writeFrequencyBigramFile()
	{
		String word;
		pair pairObj;
		Iterator<Entry<String, Integer>> i;

		try 
		{
			out = new BufferedWriter( new FileWriter(this.outName+"biGramFrequency.txt"));
			i = biGramHashMap.entrySet().iterator();
			nGramFrequencyQueue.clear();
			while( i.hasNext() )
			{
				pairObj = new pair();
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer >) i.next();
				word = entry.getKey().substring( 0, entry.getKey().indexOf('#') );
				if( uniGramHashMap.containsKey(word) )
				{
					pairObj.word = entry.getKey();
					pairObj.wordCount = (double) entry.getValue();
					nGramFrequencyQueue.add(pairObj);
				}
			}
			int index=0;
			while( !nGramFrequencyQueue.isEmpty() )
			{
				if(index==10)
					break;
				index++;
				pairObj = nGramFrequencyQueue.poll();
				out.write( pairObj.word+ "-" + pairObj.wordCount + "\n");
			}
			out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	void writeFrequencyTrigramFile()
	{
		pair pairObj;
		nGramFrequencyQueue.clear();

		try
		{
			out = new BufferedWriter( new FileWriter(this.outName+"trigramFrequency.txt"));
			Iterator<Entry<String, HashMap<String, Integer>>> i = triGramHashMap.entrySet().iterator();
			Iterator<Entry<String, Integer>> j;

			while( i.hasNext() )
			{
				Map.Entry<String, HashMap<String, Integer>> entry = 
						(Map.Entry<String, HashMap<String, Integer>>) i.next();
				j = entry.getValue().entrySet().iterator();

				while( j.hasNext() )
				{
					pairObj = new pair();
					Map.Entry<String, Integer> entry1 = (Map.Entry<String, Integer>) j.next();

					if( biGramHashMap.containsKey(entry.getKey()))
					{
						pairObj.word = entry.getKey() + "#" + entry1.getKey();
						pairObj.wordCount = entry1.getValue();
						nGramFrequencyQueue.add(pairObj);
					}
				}
			}
			int index=0;
			while( !nGramFrequencyQueue.isEmpty() )
			{
				if(index==10)
					break;
				index++;
				pairObj = nGramFrequencyQueue.poll();
				out.write( pairObj.word+ "-" + pairObj.wordCount + "\n");
			}
			out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	void writeFrequencyUnigramFile()
	{
		pair pairObj;
		Iterator<Entry<String, Integer>> i;
		nGramFrequencyQueue.clear();

		try
		{
			out = new BufferedWriter( new FileWriter(this.outName+"uniGramFrequency.txt"));
			i = uniGramHashMap.entrySet().iterator();

			while( i.hasNext() )
			{
				pairObj = new pair();
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer >) i.next();

				pairObj.word = entry.getKey();
				pairObj.wordCount = (double) entry.getValue();
				nGramFrequencyQueue.add(pairObj);
			}
			int index=1;
			out.write("Rank"+"-"+"FrequencyCount\n");
			while( !nGramFrequencyQueue.isEmpty() )
			{
				//if(index==10)
			//		break;
				pairObj = nGramFrequencyQueue.poll();
				out.write(index+"-"/*+ pairObj.word+ "-"*/ + pairObj.wordCount + "\n");
				index++;
			}
			out.close();
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}

	}
	void writeUniGramToFile()
	{
		pair pairObj;
		Iterator<Entry<String, Integer>> i;
		nGramFrequencyQueue.clear();

		try
		{
			out = new BufferedWriter( new FileWriter(this.outName+"uniGram.txt"));
			i = uniGramHashMap.entrySet().iterator();

			while( i.hasNext() )
			{
				pairObj = new pair();
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer >) i.next();

				pairObj.word = entry.getKey();
				pairObj.wordCount = (double) entry.getValue() / (double)tokenCount;
				nGramFrequencyQueue.add(pairObj);
			}

			while( !nGramFrequencyQueue.isEmpty() )
			{
				pairObj = nGramFrequencyQueue.poll();
				out.write( pairObj.word+ "-" + pairObj.wordCount + "\n");
			}
			out.close();
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
	}

	void writeBiGramToFile()
	{
		String word;
		pair pairObj;
		Iterator<Entry<String, Integer>> i;

		try 
		{
			out = new BufferedWriter( new FileWriter(this.outName+"biGram.txt"));
			i = biGramHashMap.entrySet().iterator();
			nGramFrequencyQueue.clear();

			while( i.hasNext() )
			{
				pairObj = new pair();
				Map.Entry<String, Integer> entry = (Map.Entry<String, Integer >) i.next();
				word = entry.getKey().substring( 0, entry.getKey().indexOf('#') );

				if( uniGramHashMap.containsKey(word) )
				{
					pairObj.word = entry.getKey();
					pairObj.wordCount = (double) entry.getValue() / (double) uniGramHashMap.get(word);
					nGramFrequencyQueue.add(pairObj);
				}
			}

			while( !nGramFrequencyQueue.isEmpty() )
			{
				pairObj = nGramFrequencyQueue.poll();
				out.write( pairObj.word+ "-" + pairObj.wordCount + "\n");
			}
			out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	void writeTriGramToFile()
	{
		pair pairObj;
		nGramFrequencyQueue.clear();

		try
		{
			out = new BufferedWriter( new FileWriter(this.outName+"trigram.txt"));
			Iterator<Entry<String, HashMap<String, Integer>>> i = triGramHashMap.entrySet().iterator();
			Iterator<Entry<String, Integer>> j;

			while( i.hasNext() )
			{
				Map.Entry<String, HashMap<String, Integer>> entry = 
						(Map.Entry<String, HashMap<String, Integer>>) i.next();
				j = entry.getValue().entrySet().iterator();

				while( j.hasNext() )
				{
					pairObj = new pair();
					Map.Entry<String, Integer> entry1 = (Map.Entry<String, Integer>) j.next();

					if( biGramHashMap.containsKey(entry.getKey()))
					{
						pairObj.word = entry.getKey() + "#" + entry1.getKey();
						pairObj.wordCount = (double) (entry1.getValue()/biGramHashMap.get(entry.getKey()));
						nGramFrequencyQueue.add(pairObj);
					}
				}
			}

			while( !nGramFrequencyQueue.isEmpty() )
			{
				pairObj = nGramFrequencyQueue.poll();
				out.write( pairObj.word+ "-" + pairObj.wordCount + "\n");
			}
			out.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
