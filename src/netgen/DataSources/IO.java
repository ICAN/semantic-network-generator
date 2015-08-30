/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package netgen.DataSources;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import netgen.Preprocessing.Corpus;

/**
 *
 * @author Neal
 */
public class IO {

	public static ArrayList<Corpus> importEntireSourcesFolder() throws Exception
	{
		ArrayList<Corpus> fullSourceFolderCorpii = new ArrayList<Corpus>();

		String filepath = new File("").getAbsolutePath();
		filepath.concat("./src/netgen/DataSources/Sources");

		File dir = new File(filepath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) 
			{
				String mode = child.getName().split(".")[0];
				fullSourceFolderCorpii.addAll(importCorpora(readFileAsLines(child.getName()), mode));
			}
		} 
		else 
		{
			throw new Exception("Could not find the Data Sources folder!");
		}
		return fullSourceFolderCorpii;
	}

	//Reads a file & returns each line in the file as a string in an arraylist
	public static ArrayList<String> readFileAsLines(String fileName) {
		ArrayList<String> lines = new ArrayList<>();
		Scanner inFile = null;

		try {
			inFile = new Scanner(new FileReader(fileName));
		} catch (Exception e) {
			System.out.println("Failed to open input file. Exiting.");
			System.exit(-1);
		}

		while (inFile.hasNextLine()) {
			lines.add(inFile.nextLine());
		}
		return lines;
	}

	//Reads the specified file then combines its lines into a single string using combine() method
	public static String readFileAsString(String fileName) {
		return combine(readFileAsLines(fileName));
	}

	//Combines the lines of the lines of the arraylist into a single String, separated by newline characters
	//TODO: throw exception if too many chars in strings?
	//Must have a total of less than about 2^30 characters
	public static String combine(ArrayList<String> lines) {
		String condensed = "";
		for (String line : lines) {
			condensed += ("\n" + line);
		}
		return condensed;
	}

	//Produces an arraylist of KTUU, ADN or Homer Tribune corpora 
	//from unprocessed lines of text
	public static ArrayList<Corpus> importCorpora(ArrayList<String> lines, String mode) {

		ArrayList<Corpus> corpora = new ArrayList<>();
		ArrayList<String> rows = new ArrayList<>();
		ArrayList<String> fields = new ArrayList<>();

		String combinedLines = combine(lines);

		rows.addAll(Arrays.asList(combinedLines.split("<ROW>")));

		for (String row : rows) {
			fields.addAll(Arrays.asList(row.split("<COL>")));
		}

		//debug
		//        System.out.println("Fields: " + fields.size());
		//
		//        int count = 1;
		//        for (String field : fields) {
		//            System.out.println("Field " + count + ": " + field);
		//            count++;
		//        }
		if (mode.equalsIgnoreCase("adn")) {

			//in DB dump: Article, Link, Title, Date
			for (int i = 3; i < fields.size(); i += 4) {
				Corpus corpus = new Corpus(fields.get(i - 3), "adn", fields.get(i));
				corpus.setLink(fields.get(i - 2));
				corpus.setTitle(fields.get(i - 1));
				corpus.setDate(fields.get(i));
				corpora.add(corpus);
			}

		} else if (mode.equalsIgnoreCase("ktuu")) {

			//in DB dump: Link, Summary, Article, Title, Date
			for (int i = 4; i < fields.size(); i += 5) {
				Corpus corpus = new Corpus(fields.get(i - 2), "ktuu", fields.get(i));
				corpus.setDate(fields.get(i));
				corpus.setLink(fields.get(i - 4));
				corpus.setSummary(fields.get(i - 3));
				corpus.setTitle(fields.get(i - 1));
				corpora.add(corpus);
			}

		} else if (mode.equalsIgnoreCase("tribune")) {

			//in DB dump: Link, Article, Title, Date
			for (int i = 3; i < fields.size(); i += 4) {
				Corpus corpus = new Corpus(fields.get(i - 2), "tribune", fields.get(i));
				corpus.setLink(fields.get(i - 3));
				corpus.setTitle(fields.get(i - 1));
				corpus.setDate(fields.get(i));
				corpora.add(corpus);
			}

		} else {
			System.out.println("Unrecognized type of input file");
			System.exit(-1);
		}

		return corpora;
	}


}
