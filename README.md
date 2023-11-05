# Circuit Finder
An algorithm to optimize logic circuits.

The goal of this project is to find a single logic circuit for each operation a modern CPU is required to do.
Unfortunately, for simplicity of design and reduced cost of reusable parts, many arithmetic operation require many more than 1 clock cycle to complete.
If you don't believe it, like I did, take a look [here](https://www.agner.org/optimize/instruction_tables.pdf).

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

If you want to test the algorithm in an interactive way, the `repl` module is for you. It includes a little ANTLR4 parser which you can use to optimize handwritten boolean expressions on-the-fly.

## Ideas/Future work
### More functions
Obviously, I will implement a lot more functions like:
 - all the remaining logic gates, for testing

| Operation | Implemented? |
|-----------|--------------|
| NOT       | ✅            |
| AND       | ✅            |
| OR        |              |
| NAND      |              |
| NOR       |              |
| XOR       |              |
| XNOR      |              |

 - all the common bitwise operations like `popcount`, `msb`, `lsb`, arithmetic and logic shifts, rotates and so on

| Operation                                      | Implemented? |
|------------------------------------------------|--------------|
| popcount                                       |              |
| msb                                            |              |
| lsb                                            |              |
| [BLSR](https://www.felixcloutier.com/x86/blsr) |              |
| SAR                                            |              |
| SAL                                            |              |
| SHR                                            |              |
| SHL                                            |              |
| ROR                                            |              |
| ROL                                            |              |
| LZCNT                                          |              |

 - all the arithmetic operations regarding integers (signed and unsigned) of any size (where permitted) and with optional overflow check

| Operation | Implemented? |
|-----------|--------------|
| iadd      | ✅            |
| uadd      |              |
| isub      |              |
| usub      |              |
| imul      |              |
| umul      |              |
| idiv      |              |
| udiv      |              |
| mod       |              |
| exp       |              |
| pow       |              |
| logn      |              |
| log       |              |
| sqrt      |              |
| cbrt      |              |
| nroot     |              |
| hypot     |              |
| muladd    |              |

 - all the operations regarding IEEE 754 floating point numbers (8, 16, 32 and 64 bits)

| Operation | Implemented? |
|-----------|--------------|
| add       |              |
| sub       |              |
| mul       |              |
| div       |              |
| mod       |              |
| exp       |              |
| pow       |              |
| logn      |              |
| log       |              |
| sqrt      |              |
| cbrt      |              |
| nroot     |              |
| hypot     |              |
| muladd    |              |
| normalize |              |

Soon, I will prepare a complete list.

### Output conversion
To simplify testing of the produced circuits and integration into existing applications, I will provide some converters to:
 - programming languages like C, C++, java ecc. in order to integrate the optimized circuit into an existing codebase for further testing
 - hardware definition languages like RTL, Verilog and VHDL with a priority on RTL since the other two are more general and require (AFAIK) a complete working circuit with fancy stuff like clock signals ecc. that this application doesn't care about

### Optimizations
 - Add the possibility to use the complete set of logic gates to further reduce the circuit size
 - Since, the reusability of common parts may help in reducing circuit size in an actual CPU, add the option to merge together different circuits (this could be expanded to create little full ALU circuits)
