package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.util.*;

public class Converter {

    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
     */
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) throws CsvException {

        String result = "{}"; // default return value; replace later!

        try {

            //Csv reader turned into iterator as shown in slideshows
            CSVReader csvreader = new CSVReader(new StringReader(csvString));   
            List<String[]> full = csvreader.readAll();
            Iterator<String[]> iterator = full.iterator();                      

            //Linked hashmap as advised in slideshow
            LinkedHashMap<String, Object> Final = new LinkedHashMap();          

            //Declaring data arrays to be put in hashmap
            //one being the ProdNum and the other being the encasing Json array to pur each sectiion of episode info into
            JsonArray jsonProdArr = new JsonArray();                            
            JsonArray jsonDataArr = new JsonArray();
                
            if (iterator.hasNext()) {
                
                //The header row, basically what the follwoing data actually is
                String[] ColumnHeaders = iterator.next();                       
                
                while (iterator.hasNext()) {
                    
                    //The actual data of the row and colum
                    String[] data = iterator.next();
                    //This is the nested Json array for each episode info to go into
                    JsonArray nestedData = new JsonArray();                     
                    
                    //The for loop used for iterating through each row of information. 
                    //It puts the first row into the ProdNum Array to fit Json.input, and the rest into the nested Json Array
                    for (int i = 0; i < ColumnHeaders.length; i++) {            
                        if (i == 0) {
                            jsonProdArr.add(data[i]); 
                        } else if (i == 1){
                            nestedData.add(data[i]);
                        }  //Below takes the value of the episode and season number to put into the nested Json Array
                        else if (i < 4) {
                            nestedData.add(Integer.valueOf(data[i]));    
                        } else if (i < 6) {
                            nestedData.add(data[i]);
                        } else {
                            nestedData.add(data[i]);
                            
                        }
                    }
                    //This adds the nested Json array to the Json array
                    //with all of the data, which is why each episode info appears in brackets
                    jsonDataArr.add(nestedData);                                
                }
                //All of the arrays being put into the hashmapp
                Final.put("ProdNums", jsonProdArr);                             
                Final.put("ColHeadings", ColumnHeaders);
                Final.put("Data", jsonDataArr);
            }
            //Changing the reults string to the serialized Hashmap, making it a json String
            result = Jsoner.serialize(Final);                                   

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.trim();

    }

    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {

        String result = ""; // default return value; replace later!

        try {
            
            //Creating initial json object based off jstring
            JsonObject jsonObject = Jsoner.deserialize(jsonString, new JsonObject());
            //Json arrays for all of the different json objects
            JsonArray prod = new JsonArray();
            prod = ((JsonArray)jsonObject.get("ProdNums"));
            JsonArray col = new JsonArray();
            col = ((JsonArray)jsonObject.get("ColHeadings"));
            JsonArray data = new JsonArray() ;
            data = ((JsonArray)jsonObject.get("Data"));
            //string writers and csv writers as shown in slideshow
            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer, ',', '"', '\\', "\n");
            //Using the ColHeadings json array to make the first row of the csv
            String[] headerRow = new String[col.size()];
            for (int i = 0; i < col.size(); i++){
                
                String s = col.get(i).toString();
                headerRow[i] = s;
            }
            //Addings header row to csv writer
            csvWriter.writeNext(headerRow);
            //Loops through prodce number based on size, since there are as many data rows as there are of produce numbers
            for (int i = 0; i < prod.size(); i++){
                //making a string to use as the holder for each row of episode data               
                String[] epInfo = new String[col.size()];
                //Array used for iterating through data JSonArray, basically takes each episodes information to be broken apart
                JsonArray nestedData = new JsonArray();
                nestedData = ((JsonArray) data.get(i));
                //iterating through each episode, grabbing the Prod Nums for first column, and then adding the rest as they appear
                for ( int x = 0; x < nestedData.size()+1; x++){
                     
                    if (x == 0){
                        epInfo[x] = prod.get(i).toString();
                    } else if (x < 3){
                        epInfo[x] = nestedData.get(x-1).toString();
                    } else if (x == 3){
                        //This takes the string on the given spot, which is the episode number, parses it to int, makes it turn to a 2 digit int, which adds a leading zero for numbers below 10, then formats it back to a string
                        String s = String.format("%02d", Integer.parseInt(nestedData.get(x-1).toString()));
                        epInfo[x] = s;
                    } else {
                        epInfo[x] = nestedData.get(x-1).toString();
                    }
                }
                //Writes the rows (episode information) to csv Writer
                csvWriter.writeNext(epInfo);
            }
            //Changing the result string to the writer
            result = writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();

    }

}
