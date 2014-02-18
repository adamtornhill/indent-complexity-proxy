# indent-complexity-proxy

This program is a command line tool to calculate complexity metrics using the indentation of the source code as a proxy for complexity. The program reads code from either a file or from stdin (it's designed to work together with the rest of the command line world).

## Indentation as a complexity proxy

The idea of using indentation as a proxy for complexity is backed by [research](http://softwareprocess.es/static/WhiteSpace.html). It's a simple metric, yet it correlates medium to high with more elaborate metrics such as [McCabe Cyclomatic Complexity](http://en.wikipedia.org/wiki/Cyclomatic_complexity) and [Halstead complexity measures](http://en.wikipedia.org/wiki/Halstead_complexity_measures). 

I don't use indentation-based complexity as a replacement. Rather, I see its advantage in the following situations:

* Language independent: no need for a separate parser for each programming language in polyglot code bases.
* Calculates incomplete code: perfect for code snippets or partial code.
* Speed: indentation is a simple and fast metric that quickly hints at the underlaying complexity.

Of course, some constructs are indeed non-trivial despite their flat indentation (list comprehensions come to mind). But again, measuring software complexity from a static snapshot of the code is unlikely to ever produce adequate metrics; it's a hint at best.

## Usage

I'm not hosting any pre-built binaries. The tool is written in Clojure. To build it from source, use [leiningen](https://github.com/technomancy/leiningen):

	   lein uberjar

The command above will create a standalone `jar` containing all the dependencies (you'll probably want to wrap the execution in a shell script).

### Metrics

The tool just outputs the total, aggregated complexity of the provided code. Empty and blank lines are ignored. I'll probably extend the tool with some basic statistics (mean, median, standard deviation) soon.

### Calculate the complexity of a file.

Just provide the file as an argument:

	   adam$ java -jar indent-complexity-proxy-0.1.0-standalone.jar project.clj 
	   15

### Pipe code to the program

The tool accepts input on standard input. Just pipe some code to it:

	   adam$ cat project.clj | java -jar target/indent-complexity-proxy-0.1.0-standalone.jar
	   15

### Program options

Run the tool with the `-h` option to get a list of supported arguments:

	   adam$ java -jar target/indent-complexity-proxy-0.1.0-standalone.jar -h
	   This program calculates code complexity using white space as a proxy.
	   
	   Usage: program-name [options] file|stdin
	   
	   Options:
	     -s, --spaces SPACES  4  The number of spaces to consider one logical indent.
	     -t, --tabs TABS      1  The number of tabs to consider one logical indent.
	     -h, --help

