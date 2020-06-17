# chipcaco-java
Commandline Java tool for converting .264 files produced by some Chinese IP cameras

chipcaco-java (**Ch**inese **IP** **Ca**mera **Co**nverter) is a Java application 
for converting `.264` files produced by some Chinese IP cameras like TPTEK, IeGeek, and other brands that uses CamHi software.

The H.264 recordings downloaded from the camera can't be played or converted by common applications like [VLC](https://www.videolan.org/vlc/). 

This software remove the proprietary extensions from the video so can be played on any video player.

This is a port of the C application by [Ralph Spitzner](https://www.spitzner.org/kkmoon.html), and also based on the original chipcaco written in node-JS by [Sven Jacobs](https://github.com/svenjacobs/chipcaco)

If you are looking for a video player and converter for Windows with Graphical Interface for Chinese Cameras recordings I recomend to use HIP2P Client (from IPCAM XIN).

## Installation

Download JAR file chipcaco.jar: 
[chipcaco.jar](https://raw.githubusercontent.com/eggea/chipcaco-java/master/chipcaco-java/bin/chipcaco.jar)

## Usage
    
    java -jar chipcaco.jar <source file> 

where `<src>` is the source file produced by the camera. The output file has same name but with .h264 extension.

**Note**: The produced file must likely be additionally processed by `ffmpeg` or similar applications before it can be played. Example:

Only copy H264 frames and fix FPS (can be played only in VLC)

    ffmpeg -framerate 25 -i intermediate.h264 -c copy video.h264

Converting to MP4 and fix FPS (can be player in any Video Player or Browser)

    ffmpeg -framerate 25 -i intermediate.264 video.mp4   
