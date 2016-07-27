# PokeFetcher

The PokeFetcher will fetch Pokemon in your area to see which Pokemon are available around you. I use the pokevision to find the pokemon, so there could be a delay of up to 2 minutes. 

Please use maven install to compile. You can run the jar with two arguments to set the position (lat, lon). No parameter yet for save-file. 

## How to run 

### Prerequisition

  JDK and Maven

### Adjust filePath

  PokeFile - Path in PokeFetcher/src/main/java/PokeFetcher.java

### Compile

  run 'maven install'

### Run

  in PokeFetcher/target run 'java -jar PokeFetcher-1.0-SNAPSHOT.jar <lat> <lon>'
