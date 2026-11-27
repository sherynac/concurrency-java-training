# Java Concurrency Exercise

## Goal
Provide the fastest way to load the information from the source data

## Procedure
-	Using the WebLoader (com.svi.advancedjavatraining.utils.WebLoader), load all the provincial and city data
-	Match Cities to their corresponding Province Data (key)
-	Match Population Data using the Province Name as folder, City as filename (using JSONFileReader, com.svi.advancedjavatraining.utils.JSONFileReader)
-	Add the sum of all loaded population data and display in the end
-	write all Loaded data to Excel File (com.svi.advancedjavatraining.utils.PopulationFileWriter)

## Note
-	Not all cities have population, default is 0
-	Not all cities have population records (population file), it will return as null, default it to 0
-	Splitter class is used to regenerate the input files
-	Use Future (java.lang.concurrent)
