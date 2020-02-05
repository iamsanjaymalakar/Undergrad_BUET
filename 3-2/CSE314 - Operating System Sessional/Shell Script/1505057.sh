#!/bin/bash

#Unzipping SubmissionsAll.zip
unzip SubmissionsAll.zip -d 1505057

#Creating backup folder and backing up SubmissionsAll.zip
mkdir -p Backup
mv SubmissionsAll.zip Backup

#creating absentee list 
#extracting only the rolls from csv file 
cut -c 3-9 CSE_322.csv > csvRolls.txt
#extracting the rolls from the zip files and move the file to parent 
cd 1505057
ls | rev | cut -c 5-11 | rev | sort | grep -vFx output > output.txt
mv output.txt ..
cd ..
diff csvRolls.txt output.txt | grep "< " | cut -c 3-9 > Absents.txt

#Create empty directory Output 
mkdir -p Output
cd Output

#remove directory Extra if already exists 
if [ -d Extra ]; then
	sudo rm -r Extra	
fi
mkdir Extra

cd ..
cd 1505057

#store name of the files to a file 
ls | grep -vFx fileNames > fileNames

# for each file
file="fileNames"
while read -r line; do
	name="$line"
	#echo "$name"
	mkdir -p tempDir
	#unzip the file to temp directory 
	var=`echo $line | rev | cut -c 1-3 | rev`
	#echo $var
	if [ $var == "zip" ]; then
		#echo "zippppa"
		unzip "$name" -d tempDir	
	elif [ $var == "rar" ]; then
		#echo "rararara"
		unrar x "$name" -d tempDir
	fi
	
	#check no of folders after unzip 
	cd tempDir
	ls -l
	count=`ls -l | wc -l`
	((count--))
	echo $count
	if [ $count == "1" ]; then
		sid=`ls | cut -c -7`
		#finding the sid exists in csv or not
		occ=`grep $sid ../../csvRolls.txt | wc -l`
		echo $oc
		if [ $occ == "1" ]; then
			#check contains only roll or not 
			fname=`ls`
			len=`echo ${#fname}`			
			if [ $len -gt 7 ]; then
				#fix the folder name student will get 5 marks
				echo "NOT OK"
	 			mv $fname $sid
				mv $sid ../../Output
				echo "$sid 5" >> ../../Marks.txt
			else
				#student will get 10 marks	
				echo "OK"
				mv $fname ../../Output
				echo "$sid 10" >> ../../Marks.txt
			fi
		else
			echo "vua"
			#find the name from file name
 			name=`echo $line | cut -d'_' -f1`
			echo $name 
			match=`grep -i -r "$name" ../../CSE_322.csv | wc -l`
			if [ $match == "1" ]; then
				echo "single"
				matchedRoll=`grep -i -r "$name" ../../CSE_322.csv | cut -c 3-9`
				echo $matchedRoll
				fname=`ls`
				mv "$fname" $matchedRoll
				mv $matchedRoll ../../Output
				echo "$matchedRoll 0" >> ../../Marks.txt
				#delete from absente
				sed -i "/\b\($matchedRoll\)\b/d" ../../Absents.txt
			elif [ $match == "0" ]; then
				echo "no instances"
				cp -r ../tempDir "$name"
				mv "$name" ../../Output/Extra
			else
				echo "more than one"
				grep -i -r "$name"  > temp.txt
				flag=0
				while read -r line; do
					echo "$line"
					matchedRoll=`cut -c 3-9 "$line"`
					echo $matchedRoll
					occ=`grep $matchedRoll Absents.txt | wc -l`
					echo $occ
					if [ $occ == "1" ]; then
						fname=`ls`
						mv "$fname" $matchedRoll
						mv $matchedRoll ../../Output
						echo "$matchedRoll 0" >> ../../Marks.txt
						#delete from absente
						sed -i "/\b\($matchedRoll\)\b/d" ../../Absents.txt
						flag=1
					fi
				done < temp.txt
				if [ flag == "0" ];then
					#not matched in any file 
					cp -r ../tempDir "$name"
					mv "$name" ../../Output/Extra
				fi
			fi
		fi
	else
		echo "more than one file"
		name=`echo $line | cut -d'_' -f1`
		echo $name 
		match=`grep -i -r "$name" ../../CSE_322.csv | wc -l`
		echo $match
		if [ $match == "0" ]; then
			echo "zero match"
			cp -r ../tempDir "$name"
			mv "$name" ../../Output/Extra	
		elif [ $match == "1" ]; then
			echo "one match"
			matchedRoll=`grep -i -r "$name" ../../CSE_322.csv | cut -c 3-9`
			echo $matchedRoll
			cp -r ../tempDir "$matchedRoll"
			mv "$matchedRoll" ../../Output
			echo "$matchedRoll 0" >> ../../Marks.txt
			#delete from absente
			sed -i "/\b\($matchedRoll\)\b/d" ../../Absents.txt
		else 
			echo "more than one match"
			grep -i -r "$name"  > temp.txt
			flag=0
			while read -r line; do
				echo "$line"
				matchedRoll=`cut -c 3-9 "$line"`
				echo $matchedRoll
				occ=`grep $matchedRoll Absents.txt | wc -l`
				echo $occ
				if [ $occ == "1" ]; then
					cp -r ../tempDir "$matchedRoll"
					mv "$matchedRoll" ../../Output
					echo "$matchedRoll 0" >> ../../Marks.txt
					#delete from absente
					sed -i "/\b\($matchedRoll\)\b/d" ../../Absents.txt
					flag=1
				fi
			done < temp.txt
			if [ flag == "0" ];then
				#not matched in any file 
				cp -r ../tempDir "$name"
				mv "$name" ../../Output/Extra
			fi
		fi
	fi
	cd ..
	sudo rm -r tempDir
	sudo rm -r "$line"		
done < "$file"

cd ..
sudo rm -r 1505057
sort Marks.txt > MarksSorted.txt



