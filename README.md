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
