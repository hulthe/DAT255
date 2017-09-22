<h1>onTruckConnector</h1>

<h3>Step 0: Download Android Studio</h3>
<a href = "http://bfy.tw/43JI">Use google</a> to download the latest version of Android Studio which fits your operative system.

<h3>Step 1: Git Clone</h3>
* Navigate to the <a href =" https://github.com/hulthe/DAT255">GitHub project page</a>.

* Press the green "Clone or download ↓" button. <a href = "https://i.imgur.com/NWvVLpx.png">Image for clarification.</a>

* Copy the text there to your clipboard. (Should be "https://github.com/hulthe/DAT255.git")

<h3>Step 2: Git Checkout</h3>

* Navigate to the directory where you want to clone the project to using some kind of shell. (Ex: <a href = https://git-for-windows.github.io>Git Bash</a>)

* Type "git clone" and then paste what you copied in Step 1, press enter.

* It should return "Cloning into '<folder name>'..." and NOT return any errors.
  
<h3>Step 3: Checkout to the branch</h3>
Now we need to hop off the master branch and into our development branch where this code actually lies.
Simply enter "git checkout feature/android-controller" into your shell.
You should now see a "(feature/android-controller)"-tag after your directory instead of "(master)".

<h3>Step 4: Open Android Studio</h3>
Start Android Studio and close all pop-ups and tips.

<h3>Step 5: Open Project</h3>

* Select "File" in the top taskbar.

* Select "Open" in the drop down.

* Navigate to the folder were you cloned the project to.

* Select the OnTruckConnector folder and click "OK".

* If you get an error telling you to "Upgrade plugin to version X and sync project" then click that link.

* If the link doesn't work then go to "Tools > Android > Sync Project with Gradle Files" and then press the new sync link.

<h3>Step 6: Create New Virtual Device</h3>

* Press the "Run" button in the top taskbar and "Run 'app'" in the topdown.

* A pop-up with the name "Select Deployment Target" should now have shown up.

* Press the button "Create New Virtual Device" at the bottom left of the pop-up.

* Select "Phone" in the leftest column, "Category".

* Select any phone model in the midlest column. (ex: Nexus 6)

* Press the blue "Next" button in the bottom right.

* Select Nougat, API Level 25.

* Press the blue "Next" button in the bottom right.

* Press the blue "Finish" button in the bottom right.

* Now the Virtual Device should have been constructed. Select it and press "Ok". Then wait for it to finish building the project.

* You might be prompted to download Instant Run. This is not necessary.
