Each project catalog contain subcatalog "src-git" contained source codes of each projects.
These source codes fully entirely to the authors of the projects and are located here only for testing my development standalone java application ! Some files in project catalogs that take up a lot of space were removed for saving capacity in a disk. And they are not used for analysis of projects.

Below is a list of applications.

Catalog name pattern: <prN>_<platform>_[lib]_<name>
Where:
* prN - project Number
* platform - the platform on which the project is implemented 
{Android | JavaSE}
* lib - set if the project is library otherwise not set;
* name - project name. It is prefer to set the project name as in the git repository.

1. pr1_Android_FileManager
https://github.com/prateek2211/File-Manager-for-android

2. pr2_Android_FileManager
https://github.com/realDuYuanChao/FileManager/

3. pr3_Android_AnExplorer
https://github.com/1hakr/AnExplorer

4. pr4_JavaSE_FileRenamer
https://github.com/perdian/filerenamer

5. pr5_javaSE_CodeSeriff
https://github.com/mauricioaniche/codesheriff

6. pr6_javaSE_lib_apacheCommonCSV
https://github.com/apache/commons-csv/tree/master

7. pr7_javaSE_YouTube-Comment-Suite
https://github.com/mattwright324/youtube-comment-suite

Retrieve numeric values of metrics was done using the following command:
java -jar ck-0.7.0-jar-with-dependencies.jar "$in"  true 0 false "$out" test/ androidTest/

Where:
in - absolute input path to analysed project
out - absolute destination path where saved results in csv files