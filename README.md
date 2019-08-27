### Learn My Way Reader

This is the repository for the Unicef Learn My Way Reader application. Learn My Way Reader is a part of the project Accessible Textbooks for All (https://www.accessibletextbooksforall.org/)

The purpouse of this application is to demonstrate how to create and Android application with extended accesibility features.
The application is based on the Readium SDK Epub reader and enhances it by adding advanced accesibility capabilities.

Port Aberta accesible epub is bundled with the project which highlights and demonstrates the different accesibility features available and includes a set of interactive exercises and activities.

The source code for the epub are available here: https://github.com/ansanes/portaabertaepub 

You can use this project to create you own accesibility enabled solution.

Readium is has text to speech capabitilities. In order to providad TTS capabilities in you epubs you need to use the smil extension.
Adding SMIL suport to you epub consists of creating the appropiate .smil files where a mapping between the html text elements and the corresponding speech sound files is defined.

Readium will automatically use this SMIL information to highlight the html elements and reproduce the associated sound file when the TTS setting is enabled.

With learn my way reader you can add sign language video features by using the smil approach. To define the mappings between html text elements and the correponding video files SMIL format is used as well.

Video files have to be placed in the app\src\main\res\raw folder and video smil files in app\src\main\assets\signlanguagesmilfiles

In this project you can find the files used by the porta abarta epub.

Video smil files html id references hava to match the tts smil files id references.

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

the corresponding video smil fiel would be page1_sign.smil:
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



