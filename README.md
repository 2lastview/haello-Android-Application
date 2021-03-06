haello-Android-Application
==========================

This Android app was developed for a bachelor thesis in "Computer Science and Visual Computing" at the Technical University of Vienna by Moritz Tomasi.

This repository contains the source code for the haello Android application. It uses [**haello RESTful web service**](https://github.com/2lastview/haello-Rest-Service)
to extract text from images and translate said text into a specified language. Further it gives you
the possibility to enrich this translation with information from Wikipedia, Wiktionary and Google.

## Description

* You can choose an image from the gallery or by taking one with the camera.

<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/1.1.png" width="300px" />
<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/1.2.png" width="300px" />

* Choose a source language by tapping on FROM. If you select "Unknown" the web service will
detect the language automatically using [goslate](http://pythonhosted.org/goslate/).
By tapping on TO you can select the target language. There is also a third option for editing the image.

<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/2.png" width="300px" />

* After tapping on GO the extracted text and translation are shown. By pressing on a single word for
a little longer you can search for it in Wikipedia, Wiktionary or on Google.

<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/3.1.png" width="300px" />
<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/3.2.png" width="300px" />

* By tapping CORRECT AND RETRY you can correct the mistakes made by the ocr engine manually. In this
stage there is also the possibility to change the TO language.

<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/4.png" width="300px" />

* By swiping left, or tapping on the IMAGE tab, you can see the chosen image.

<img src="https://github.com/2lastview/haello-Android-Application/blob/master/screenshots/5.png" width="300px" />

## Build

Import the root folder into your IDE (tested on Android Studio). Find the class [translate](https://github.com/2lastview/haello-Android-Application/blob/master/app/src/main/java/com/example/moritztomasi/clicklesstextenricherapplication/enrichment/Translate.java). There the
TRANSLATE_URL can be pointed to the URL where a implementation of the [haello RESTful web service](https://github.com/2lastview/haello-Rest-Service)
is deployed. Then simply build and run the project.

## Support

The application has been tested with SDK version 16 and above.

## Dependencies

* [SlidingUpPanel](https://github.com/umano/AndroidSlidingUpPanel) by umano.

* Wiktionary is not queried directly. A Wiktionary Parser written by Yves Bourques is used. Information
regarding this parser can be found on his [website](http://www.igrec.ca/projects/wiktionary-text-parser/).

## Copyright and License

Author: Moritz Tomasi (moritz.tomasi at gmail dot com)

License: [Apache 2.0 License](https://github.com/2lastview/haello-Android-Application/blob/master/LICENSE)
