# BT-App
Processing the Log File
Language Compiler/Interpreter Version:
  Java JDK 17 (Java 17 latest version)
1. Log Lines Parsing:
     The getParseDateAndTime function parses the time strings into Date objects using SimpleDateFormat (java.text.SimpleDateFormat).
   
2. Log File Processing:
    The main function tracks each user's start time and session duration by reading the log file line by line and updating the minimum and maximum timestamps.
    Reads the log file using BufferedReader, dividing each line into its component parts (time, username, action).
    Saves session information in a Map<String, SessionInfo> with the username serving as the key and a SessionInfo object with the start and end times of each session as well as their 
    count as the value.
   
3. Managing Unmatched Entries:
   If an end action has no preceding start, it assumes the earliest time in the log as the start time.
   If a start action has no subsequent end, it assumes the latest time in the log as the end time.

4. Producing the Results:
   After processing all entries, it iterates over the session map to calculate the final durations for sessions that didn't end within the log period and prints the results.

Running the Java program:
 Compile the program
   javac <class_name> (javac BillingLogSessionProcess.java)
 Run the program with the log file path as an argument:
   java BillingLogSessionProcess  <argument> (path/to/logfile.log)


    
