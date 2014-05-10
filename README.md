Tumblr Tag Tracker
==================

TTT is a small command-line java application that produces a RSS feed of the Tumblr posts from one or multiple tags.

Limitations
===========

The tumblr public API always has SafeSearch active, which means any post that is flagged as explicit by the system won't notify you.
And, "flagged as explicit" is a very broad concept : try looking up https://www.tumblr.com/tagged/yourtag and ensure "show explicit content" is off, you'll see.Which posts get flagged it entirely up to their system and it's completely undocumented. Good luck with that.

How to use
==========

Note: you need a web server to host this. Also an API key.
Just send me an ask if you really want to try, http://neurovertex.tumblr.com/ask you'll need to be at least know how to make a script on your sever that runs the program.

    usage: java -jar TumblrTagTracker.jar [-a] [-c] [-ck <arg>] [-cs <arg>]
           [-h] [-n] [-o <arg>] [-os <arg>] [-ot <arg>] [-s] [-t <tags>]
     -a,--append-tags              Adds tags from the --tags parameters from
                                   those loaded from the config file instead
                                   of replacing them
     -c,--clear-settings           Doesn't load the settings from the file
     -ck,--consumer-key <arg>      The consumer key
     -cs,--consumer-secret <arg>   The consumer secret
     -h,--help                     Display help
     -n,--dry-run                  Doesn't run the tracker. Combine with -s to
                                   adjust settings on dry runs.
     -o,--output <arg>             Output file, default is - (= console
                                   output)
     -os,--oauth-secret <arg>      The OAuth token secret
     -ot,--oauth-token <arg>       The OAuth token
     -s,--save-settings            Save settings after including command-line
                                   parameters.
     -t,--tags <tags>              Comma-separated list of tags to track

For now I'm not certain anyone cares so if someone does I'll write more doc, otherwise

Dependencies
============

This software uses Tumblr's official Java API [Jumblr](https://github.com/tumblr/jumblr) and [Apache Common CLI](http://commons.apache.org/proper/commons-cli/)