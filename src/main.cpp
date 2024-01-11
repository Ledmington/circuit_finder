#include <iostream>
#include <unordered_map>

namespace cf {
namespace cli {
static constexpr std::string_view help_message = R""""(
     --- Mandatory --- 
         --bits N             Generates a circuit setting the variable number of bits to N
         --operation OPNAME   Generates a circuit which computes the operation corresponding to OPNAME
    
         --op_list            Prints the list of available operations and exits
    
     --- Output --- 
     -q, --quiet    Prints only errors
     -v             Prints errors and warnings
     -vv            Prints errors, warnings and info
     -vvv           Prints all the information
    
     --- Others --- 
     -h, --help     Prints this message and exits
     -j, --jobs N   Uses N threads (default: 1)
    )"""";

static constexpr std::string_view operation_list = R""""(
       OP_NAME           BITS         DESCRIPTION
    logic_not           N -> N      Bitwise NOT.
    logic_and         2*N -> N      Bitwise AND.
    logic_or          2*N -> N      Bitwise OR.
    logic_nand        2*N -> N      Bitwise NAND.
    logic_nor         2*N -> N      Bitwise NOR.
    logic_xor         2*N -> N      Bitwise XOR.
    logic_xnor        2*N -> N      Bitwise XNOR.
    signed_sum        2*N -> N      Sum of signed integers.
    unsigned_sum      2*N -> N      Sum of unsigned integers.
)"""";

static std::unordered_map<std::string, std::string> name_to_operation = {
    {"logic_not", "Logic NOT"}};

bool is_help_argument(const std::string arg) {
  return arg == "-h" || arg == "--help";
}

bool is_bits_argument(const std::string arg) { return arg == "--bits"; }

bool is_op_list_argument(const std::string arg) { return arg == "--op_list"; }

bool is_operation_argument(const std::string arg) {
  return arg == "--operation";
}

}  // namespace cli
}  // namespace cf

int main(int argc, char* argv[]) {
  int8_t bits = -1;
  std::string operation = "";
  for (int i{1}; i < argc; i++) {
    if (cf::cli::is_help_argument(argv[i])) {
      std::cout << cf::cli::help_message << std::endl;
      return 0;
    } else if (cf::cli::is_bits_argument(argv[i])) {
      if (i + 1 >= argc) {
        std::cerr
            << "The parameter '--bits' needs an integer, but none was found."
            << std::endl;
        return -1;
      }

      i++;
      bits = std::atoi(argv[i]);
      if (bits <= 0) {
        std::cerr << "The number of bits should be a positive integer."
                  << std::endl;
        return -1;
      }
    } else if (cf::cli::is_op_list_argument(argv[i])) {
      std::cout << cf::cli::operation_list << std::endl;
      return 0;
    } else if (cf::cli::is_operation_argument(argv[i])) {
      if (i + 1 >= argc) {
        std::cerr << "The parameter '--operation' needs an operation name, but "
                     "none was found."
                  << std::endl;
        return -1;
      }

      i++;
      if (cf::cli::name_to_operation.count(argv[i]) < 1) {
        std::cerr << "Operation '" << argv[i] << "' does not exist."
                  << std::endl;
        return -1;
      }
      operation = cf::cli::name_to_operation[argv[i]];
    }
  }

  if (bits < 1) {
    std::cerr << "Parameter '--bits' was not set" << std::endl;
    return -1;
  }
  if (operation == "") {
    std::cerr << "Parameter '--operation' was not set" << std::endl;
    return -1;
  }

  std::cout << "Selected operation '" << operation << "' with " << bits
            << " bits" << std::endl;

  return 0;
}