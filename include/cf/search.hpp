#pragma once

#include <cstdint>
#include <vector>

#include <cf/logic_function.hpp>

namespace cf {

static std::vector<bool> convert_to_bitarray(const uint64_t x,
											 const size_t bits);

static std::string convert_to_string(const std::vector<bool> v);

void search(const uint64_t limit, const size_t bits, logic_function fun);

}  // namespace cf