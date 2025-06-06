![GeoTools logo](https://upload.wikimedia.org/wikipedia/commons/5/5b/Geotools-logo.svg)

[![Java](https://img.shields.io/badge/Java-ED7B09?style=for-the-badge&logo=openjdk&logoColor=white)](https://shields.io/)
[![Gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)](https://shields.io/)
[![Postgresql](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://shields.io/)
[![Sentry](https://img.shields.io/badge/Sentry-black?style=for-the-badge&logo=Sentry&logoColor=#362D59)](https://shields.io/)
--------

Geo-Tools is a Java project that provides tools for geospatial data.  
The goal of this project is to allow the extraction of information contained within shapefile or postgis databases.

## Features
* Extraction of geospatial data from shapefile
* Extraction of geospatial data from postgis database  
  Note: One or more candidates can be extracts for both extraction

## Configuration
This project can be used as a library to use the functions it exposes.  
To configure the coordinates you can use the `Candidate` class.  
In addition to this you can also configure its type, which by default is 4326.  
When extracting data from shapefile, it is possible to set some configuration parameters.  
Initializing the `IntersectParames` class you can configure the following variables, otherwise they will acquire the default value:  

| Variable     | Default value | Description                                |
|--------------|---------------|--------------------------------------------|
| radius       | 2             | Radius in meters from the input coordinate |
| increase     | 2             | Radius increase in meters                  |
| attempts     | 100           | Number of search attempts                  |
| candidates   | 1             | Maximum number of candidates drawn         |
| maxDistance  | 50            | Maximum search radius                      |

If you want to extract data from shapefiles, you just need to indicate the location of these files.
Otherwise, if you want to use a database, you need to know the information to access it.  
The database connection url must be in the following format:   
**jdbc:postgresql://host:porta/database?currentSchema=table,schema**  

You can also catch any exceptions through the Sentry configuration. Just configure the "dsn" environment variable specifying the project url on Sentry.

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
An example is: C:/Documents/GeoTools/config.yaml.
Below is a sample configuration file:

```yaml
inputFile: Input file to be processed
outputFile: Output file to be generated
delimiter: Delimiter used in input file to divide columns (Put it in quotes es. "|")
header: Indicates whether the header is present or not (S/N)
columnX: Longitude column (The count starts from 0)
columnY: Latitude column (The count starts from 0)
coordinateType: Type of coordinates (default 4326)
intersect:
  type: Type of intersect to be performed (database/shapefile)
  data: Columns of the database or shapefile to be extracted, separated by commas
  shapefile:
    path: Path to the shapefile (e.g C./Documents/shapefile.shp)
  database:
    url: Database connection url (Example below)
    username: Database username
    password: Database password
  parameters:
    radius: Radius in meters from the input coordinate
    increase: Radius increase in meters
    attempts: Number of search attempts
    candidates: Maximum number of candidates drawn
    maxDistance: Maximum search radius
```
**Note**: If you use a shapefile, you do not need to specify database properties and vice versa.

* In the Intersect with database the database connection URL must be in the following format:   
**jdbc:postgresql://host:port/database?currentSchema=table,schema**  
In the intersect parameters section of the yaml file, only the `candidates` property will be used while all the others will not be considered when using intersection on database.