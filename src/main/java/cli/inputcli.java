package cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dbaware.IdbConnector;
import dbaware.SQLiteConnector;

public class inputcli {

    static IdbConnector dbConnector = SQLiteConnector.getInstance();;

    public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Set token:");
			String phrase = br.readLine();
			String[] tokens = phrase.split(" ");
			
			long ph_id = dbConnector.SavePhrase(-1);
			for (String token : tokens) {
				if (token.isEmpty()) continue;
				System.out.println("token : " + token);	
				long w_id = dbConnector.SaveLex(token);
				dbConnector.SavePhraseContent(ph_id, w_id);
			}
		}
	}

}
