# Startup shell scripts
Instructions for running shell scripts on startup.

1. Create shell script in `/etc/init.d/`
2. Add `sh /etc/init.d/<filename>` line in `/etc/rc.local` under `#This script is executed at the end of each multiuser runlevel.`
3. (optional) To run a python script in this manner, add the line `python3 <absolute-path-to-script>` to the shell script.
