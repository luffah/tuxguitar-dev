# tuxguitar-dev
This project is kept for archive.

Fork of TuxGuitar v1.2 aimed to ease tabs editing.

The mains points was :
- to have a fast copy mecanism to repeat patterns
- to use with a French laptop keyboard the note edition with numbers 0-9 without NumLock. In fact, you can use Esc and F1-12 keys instead in TuxGuitar.

Here added keybindings :
* Unix-like selection with key 'c'
* Direct copy of selected notes with key 'd'
* Deletion of all notes on the selected beat with 'Shift-d', now useless use 'Ctrl-Del' in TuxGuitar v1.5
* Mute of all notes on the string in the measure with 'Alt-p'
* Tie of all notes on the beats in the measure with 'Shift-l'
* Ghosting of all notes on the beat with 'Shift-o'


Note :
  The aim here was only to edit partition.
  Currently this software don't play sound.
  Use official TuxGuitar for that.

  Search 'luffah' in code to get the change done that add the selection square.

# Install from sources

## Requirement
You need libswt for Java.

On Ubuntu
```
sudo apt install libswt-gtk-4-java libswt-cairo-gtk-4-jni libswt-webkit-gtk-4-jni
```

## Compile
Before compilation the directory ./lib shall contains 'swt.jar'.
If you have it on your Linux system (`sudo apt install lib-swt-gtk3-java` on Ubuntu), try the script `prepare_swt_link.sh`.

Install 'ant' or 'Eclipse' on your system and use 'build.xml' file to compile:
- with 'ant' : just type `ant` ('ant' will use the local 'build.xml' file)
- with Eclipse : use 'build.xml' in Eclipse Ant Tool
