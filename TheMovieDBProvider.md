# Introduction #

This provider will search the themoviedb.org for movies using the api provided by themoviedb.org.  This provider uses an api\_key that has been provided to this project.

# Details #

To use this provider, simply pass the --provider=themoviedb.org to the java command line.

```
java -jar MetadataTool.jar --provider=themoviedb.org
```

If you want to convert your collection from imdb to themoviedb.org, then you can use the following command.

```
java -jar MetadataTool.jar --provider=themoviedb.org --force --forceThumbnail MOVIE_DIR
```