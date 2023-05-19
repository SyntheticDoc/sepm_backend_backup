#### CUP

Mental Health Checker uses the parser generator CUP to generate an efficient parser for logic evaluation. Parser.cup defines the grammar for the language of the generated parser. CUP can be downloaded from: 
http://www2.cs.tum.edu/projects/cup/

To generate the parser code from Parser.cup the following command can be used:
```linux
java -jar java-cup-11b.jar -parser Parser Parser.cup
```