# RTSP_Viewer
A Java Swing application for viewing RTSP Streams

# Features
- View multiple streams threaded
- Full screen views individually
- Save/Load configuration of stored RTSP Streams

# Releases
Release builds are compiled jar files for you.
Run with `java -jar rtsp_stream_multiviewer.jar`

# Building
Open this project in your favorite IDE. Project is built with Maven. IntelliJ configuration is provided in this repository.
`mvn package` will setup everything for you. Jar output is quite large to package in openCV and other dependencies.

# Todo
- Cleanup dependencies
- Reduce jar file size
- Have cameras handle sub streams better for full screen viewing.
