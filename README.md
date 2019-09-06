# Learn My Way Reader

This is the repository for the Unicef Learn My Way Reader application. Learn My Way Reader is a part of the project Accessible Textbooks for All (https://www.accessibletextbooksforall.org/)

The purpouse of this application is to demonstrate how to create and Android application with extended accesibility features.
The application is based on the Readium SDK Epub reader and enhances it by adding advanced accesibility capabilities.

Port Aberta accesible epub is bundled with the project which highlights and demonstrates the different accesibility features available and includes a set of interactive exercises and activities.

The source code for the epub are available here: https://github.com/ansanes/portaabertaepub 

You can use this project to create you own accesibility enabled solution.

## Setting up your environmenrt.

Clone the repository into your filesystem. Android studio should recognize it and automatically build the executables.
To test the application it is recommended to use a real device. The application is designed to be executed in Android tablets both in portrait and landscape mode.

Running this application in a emulator is not optimal as you'll need an ARM based emulator and those doesn't take advantage of the HAXM intel acceleration feautres. This is because Readium SDK is only compatible with ARM architechture at this moment.

## Customizing the epubs available in the application.

This application includes a Bookshelf Activity. (org/readium/sdk/android/launcher/BookshelfActivity.java) which lets the user pick the epub he wants to read. In this sample app the Bookshelf Activity shows the Porta Aberta epub series but only the first volume is available.

Feel free to modify this bookshelf activity or customize it to include your own epub publications.

Epub files should be placed in the app/src/assets folder. In there you can find the porta_aberta.epub file. The source files for this epub can be found in this repo: https://github.com/ansanes/portaabertaepub . Instructions on how the activities available in this book have been generated can be found there too.

## Adding support for sign language videos in your epubs

Readium has text to speech capabitilities. In order to providad TTS capabilities in you epubs you need to use the smil extension.
Adding SMIL suport to you epub consists of creating the appropiate .smil files where a mapping between the html text elements and the corresponding speech sound files is defined.

Readium will automatically use this SMIL information to highlight the html elements and reproduce the associated sound file when the TTS setting is enabled.

With learn my way reader you can add sign language video features by using the smil approach. To define the mappings between html text elements and the correponding video files SMIL format is used as well.

Video files have to be placed in the app\src\main\res\raw folder and video smil files in app\src\main\assets\signlanguagesmilfiles

In this project you can find the files used by the porta aberta epub.

Video smil files html id references have to match the tts smil files id references.

ie. for the tts smil file page1.smil

```
<?xml version="1.0" encoding="UTF-8"?>
<smil xmlns="http://www.w3.org/ns/SMIL" xmlns:epub="http://www.idpf.org/2007/ops" version="3.0">
    <body>        
        <par id="par1">
            <text src="../page.xhtml#page_w1" />
            <audio src="../../audio/silence_2secs.m4a" clipBegin="0.30" clipEnd="2.00" />
        </par>        
    </body>
</smil>
```

the corresponding video smil file would be page1_sign.smil:
```
<?xml version="1.0" encoding="UTF-8"?>
<smil xmlns="http://www.w3.org/ns/SMIL" xmlns:epub="http://www.idpf.org/2007/ops" version="3.0">
    <body>
        <par id="par1">
            <text src="../page.xhtml#page_w1" />
            <video src="s02_01.mp4" clipBegin="0.00" clipEnd="2.62" />
        </par>
    </body>
</smil>
```

To get sign language video support we had to modify the Readium SDK to add synchronization between text overlays and videos. This is because sign languages usually take longer to play than its corresponding audio files so the engine must wait till the sign language video finishes to start highlighting and reading the next html text element.

Voice overlay and sign language videos can be enabled/disabled manually using the onscreen controls.
