# Multi-Layer-Perceptrons
unfinished work for my AI module. 

Guide to the files:
The main program is src\coursework2.java

The data files consist of CW2DataSet1.csv, CW2DataSet2.csv and digits.csv.
Each file contains data that represents 8 by 8 images. Each line represents an image. Every number in the line represents a pixel with a greyscale value from 0-16, 0 being white 16 being black. The final number in the line is the number the image is supposed to be, this is used for testing. 
digits.csv contains 2 images per number, (i.e. two images of a 1, two of a 2 etc).
CW2DataSet1.csv and CW2DataSet2.csv contain 2810 images each. 

If you want to visualise the data use the function printAll or print8by8. 
printAll takes a file that has already been read into the program and prints every image in the file. 
print8by8 takes one image and prints it to the console. 

When the program is run it first uses euclidian distance to 'guess' which images represent what numbers; the program then outputs how many it got right.
Next the program trains the MLP. unfortunately I did not get to finish the back-propogation part so the full MLP is not functional. 
