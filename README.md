This is a small little test repo showing of some strange behavior in Hikaricp. (Note: strange could be just that I don't know what I'm talking about.)

If you run ./gradlew test you should see two tests, one that passes and one that fails. The main difference between the two, is the second test has a sleep in between the initialization of the Datasource and the usage of it.
