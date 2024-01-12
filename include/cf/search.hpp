#pragma once

#include <cstdint>

#include <cf/logic_function.hpp>

namespace cf {

void search(const uint64_t limit, const size_t bits, logic_function fun);

}  // namespace cf