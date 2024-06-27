![GeoTools logo](https://upload.wikimedia.org/wikipedia/commons/5/5b/Geotools-logo.svg)

[![Java](https://img.shields.io/badge/Java-ED7B09?style=for-the-badge&logo=openjdk&logoColor=white)](https://shields.io/)
[![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://shields.io/)
[![Postgresql](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://shields.io/)
--------

Geo-Tools is a Java project that provides tools for geospatial data.  
The goal of this project is to allow the extraction of information contained within shapefile or postgis databases.

## Features
* Extraction of geospatial data from shapefile
* Extraction of geospatial data from postgis database  
  Note: One or more candidates can be extracts for both extraction

## Configuration
To configure the coordinates you can use the `Candidate` class.  
In addition to this you can also configure its type, which by default is 4326.  
When extracting data from shapefile, it is possible to set some configuration parameters.  
Initializing the `IntersectParameters` class you can configure the following variables, otherwise they will acquire the default value:  

| Variable     | Default value | Description                                |
|--------------|---------------|--------------------------------------------|
| radius       | 2             | Radius in meters from the input coordinate |
| increase     | 2             | Radius increase in meters                  |
| attempts     | 100           | Number of search attempts                  |
| candidates   | 1             | Maximum number of candidates drawn         |
| maxDistance  | 50            | Maximum search radius                      |

In order to perform data extraction, it is necessary to indicate the path where the shapefile to be processed is located.  
Otherwise if the data is within a postgis database you need to know the database url, username and password.  
The database connection url must be in the following format: **jdbc:postgresql://host:porta/database?currentSchema=table,schema**

## CLI
This project can be run from the command line and information can be extracted from the postgis database or from the shapefile given a list of coordinates as input. 
The parameters needed to run the project are shown below.  

    java -cp geo-tools.jar com.geocode.search.Intersect --help
      
      usage: [-v] [-c config] [-t thread]
      -c, --config <arg>   Indicates the path to the configuration file
      -h, --help           Show arguments
      -t, --thread <arg>   Specify number of thread (default: 1)
      -v, --version        Show the version of the project


The only required parameter to run the batch process is `config`, which determines the location of the configuration file.
An example is: C:/Documents/GeoTools/config.properties.
Below is a sample configuration file:

```properties
#[input file configuration]
geotools.input_file=Input file to be processed
geotools.delimiter=Delimiter used in input file to divide columns
geotools.header=Indicates whether the header is present or not (S/N)
geotools.column_x=Longitude column (The count starts from 0)
geotools.column_y=Latitude column (The count starts from 0)
geotools.coordinate_type=Type of coordinates (default: 4326)
geotools.output_file=Output files to be generated

#[intersect model]
geotools.intersect_type=Type of intersect to be performed (database/shapefile)
geotools.intersect_data=Columns of the database or shapefile to be extracted, separated by commas
geotools.shapefile_path=Path to the shapefile (e.g C./Documents/shapefile.shp)
geotools.database_connection=Database connection url (Example below)
geotools.database_username=Database username
geotools.database_password=Database password

#[intersect parameters]
geotools.intersect_radius=Radius in meters from the input coordinate
geotools.intersect_increase=Radius increase in meters
geotools.intersect_attempts=Number of search attempts
geotools.intersect_candidates=Maximum number of candidates drawn
geotools.intersect_maxDistance=Maximum search radius
```
**Note**: If a shapefile is used, it is not necessary to specify database properties and contrary.

  * In the Intersect with shapefile, the intersect parameters are not mandatory to be set, if they are not set they take on the default value indicated in the table above.


* In the Intersect with database the database connection URL must be in the following format:   
**jdbc:postgresql://host:port/database?currentSchema=table,schema**  
In the intersect parameters section of the configuration file, only the `geotools.intersect_candidates` property will be used while all the others will not be considered.