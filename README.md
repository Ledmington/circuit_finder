# Circuit Finder
An algorithm to optimize logic circuits.

The goal of this project is to find a single logic circuit for each operation a modern CPU is required to do.
Unfortunately, for simplicity of design and reduced cost of reusable parts, many arithmetic operation require many more than 1 clock cycle to complete.
If you don't believe it, like I did, take a look [here](https://www.agner.org/optimize/instruction_tables.pdf).

## Features
Currently, `cf` implements only some of the functions reported in the following tables. You can see the complete list by running `java -jar cf-cli.jar --op_list`.

### Logic gates
For testing.

| Operation | Implemented? |
|-----------|--------------|
| NOT       | ✅            |
| AND       | ✅            |
| OR        | ✅            |
| NAND      | ✅            |
| NOR       |              |
| XOR       |              |
| XNOR      |              |

### Bitwise operations

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

### Arithmetic operations

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
Not sorted by priority.

### More functions
The tables in the "Features" section give a rough overview of the amount and categories of functions I would like to implement.
Soon, I will prepare a complete and detailed list.

### Output conversion
To simplify testing of the produced circuits and integration into existing applications, I will provide some converters to:
 - programming languages like C, C++, java ecc. in order to integrate the optimized circuit into an existing codebase for further testing
 - hardware definition languages like RTL, Verilog and VHDL with a priority on RTL since the other two are more general and require (AFAIK) a complete working circuit with fancy stuff like clock signals ecc. that this application doesn't care about

### Optimizations
 - Add the possibility to use the complete set of logic gates to further reduce the circuit size
 - Since, the reusability of common parts may help in reducing circuit size in an actual CPU, add the option to merge together different circuits (this could be expanded to create little complete ALU circuits)

### Documentation and cleaner API
Currently, the javadoc comments are present only in some critical methods of some specific classes without any criterion.

## How to contribute?
Pull requests and bug reports are always welcome.

If you want to add a specific function, open an issue explaining what it does with examples and maybe some links to existing implementations.

## License
This project is licensed under the GNU General Public License version 3.
