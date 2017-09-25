# Start-up script

### Functionality
The script will save the current date and time in a log file when the system is booted.

### Locations

- #### Log file
`autorun.log` containing the time and date is located in the `~/dat255/` folder.

- #### Python script
The python script that does the prints itself `autorun.py` is located in the `~/dat255/` folder.

- #### Shell script
The shell script is located at `/etc/init.d`

### Running the script
When the system is booted, the files within `init.d` runs where the `autorun.py` is called from.
