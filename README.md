# Circuit Finder
An algorithm to optimize logic circuits.

The goal of this project is to find a single logic circuit for each operation a modern CPU is required to do.
Unfortunately, for simplicity of design and reduced cost of reusable parts, many arithmetic operation require many more than 1 clock cycle to complete.
If you don't believe it like I did, take a look [here](https://www.agner.org/optimize/instruction_tables.pdf).

## Features
Currently, `cf` implements only the following functions (which you can see with `./gradlew :cli:run --args="--op_list"`):
 - `logic_not`
 - `logic_and`
 - `signed_sum`

## How to use
Currently, no ready jar is provided, so you need to build it yourself. For that, you need:
 - `gradle`
 - `java` >= 17

You can run `./gradlew :cli:run` to run more quickly during development and testing, or you can produce a fat jar with `./gradlew :cli:fatjar` and then run it directly with
```bash
java -jar cli/build/libs/cf-cli.jar
```

If you want to test the algorithm in an interactive way, the `repl` module is for you. It includes a little ANTLR4 parser which you can use to optimize hand-written boolean expressions on-the-fly.

## Ideas/Future work
Obviously, I will implement a lot more functions like:
 - all the remaining logic ones (like OR, XOR, NOR and so on), for testing
 - all the common bitwise operations like `popcount`, `msb`, `lsb`, arithmetic and logic shifts, rotates and so on
 - all the arithmetic operations regarding integers (signed and unsigned) of any size (not only multiplication and division, but also exponetiation, integer logarithm, integer roots and so on)
 - all the operations regarding IEEE 754 floating point numbers (8, 16, 32 and 64 bits)

Soon, I will prepare a complete list.