#pragma once

#include <vector>
#include <cassert>
#include <algorithm>
#include <iostream>
#include <unordered_set>

#include <cf.hpp>

namespace cf {
namespace minimize {

// TODO: may be simplified with pointers
struct prime_implicant_chart {
	size_t rows;
	size_t columns;
	std::vector<bool> chart;
	std::vector<bool> deleted_rows;
	std::vector<bool> deleted_columns;
};

static void remove_dominated_rows(prime_implicant_chart& chart) {
	assert(chart.chart.size() == chart.rows * chart.columns);
	assert(chart.deleted_rows.size() == chart.rows);
	assert(chart.deleted_columns.size() == chart.columns);

	for (size_t i{0}; i < chart.rows; i++) {
		if (chart.deleted_rows.at(i)) {
			continue;
		}

		for (size_t j{i + 1}; j < chart.rows; j++) {
			if (chart.deleted_rows.at(j)) {
				continue;
			}

			bool i_dominates_j = true;
			bool j_dominates_i = true;
			for (size_t k{0}; k < chart.columns; k++) {
				if (chart.deleted_columns.at(k)) {
					continue;
				}

				const size_t i_idx = i * chart.columns + k;
				const size_t j_idx = j * chart.columns + k;
				if (chart.chart.at(i_idx) && !chart.chart.at(j_idx)) {
					j_dominates_i = false;
				}
				if (chart.chart.at(j_idx) && !chart.chart.at(i_idx)) {
					i_dominates_j = false;
				}
				if (!i_dominates_j && !j_dominates_i) {
					break;
				}
			}

			if (i_dominates_j && j_dominates_i) {
				// the rows i and j were equal, so we delete only j
				chart.deleted_rows.at(j) = true;
				cf::utils::debug("Deleted row " + std::to_string(j) + ": dominated by row " +
								 std::to_string(i));
			} else if (i_dominates_j) {
				// i dominates j, we delete j
				chart.deleted_rows.at(j) = true;
				cf::utils::debug("Deleted row " + std::to_string(j) + ": dominated by row " +
								 std::to_string(i));
			} else if (j_dominates_i) {
				// j dominates i, we delete i
				chart.deleted_rows.at(i) = true;
				cf::utils::debug("Deleted row " + std::to_string(i) + ": dominated by row " +
								 std::to_string(j));
			}
		}
	}
}

static int find_first_essential_prime_implicant(const prime_implicant_chart& chart) {
	assert(chart.chart.size() == chart.rows * chart.columns);
	assert(chart.deleted_rows.size() == chart.rows);
	assert(chart.deleted_columns.size() == chart.columns);

	cf::utils::debug(
		"The chart has " + std::to_string(chart.rows) + " (" +
		std::to_string(std::count(chart.deleted_rows.begin(), chart.deleted_rows.end(), true)) +
		" deleted) and " + std::to_string(chart.columns) + " columns (" +
		std::to_string(
			std::count(chart.deleted_columns.begin(), chart.deleted_columns.end(), true)) +
		" deleted)");
	cf::utils::debug("Total size: " + std::to_string(chart.rows * chart.columns) + " elements.");
	cf::utils::debug(
		"Actual size: " +
		std::to_string(
			(chart.rows - static_cast<size_t>(std::count(chart.deleted_rows.begin(),
														 chart.deleted_rows.end(), true))) *
			(chart.columns - static_cast<size_t>(std::count(chart.deleted_columns.begin(),
															chart.deleted_columns.end(), true)))) +
		" elements.");

	for (size_t c{0}; c < chart.columns; c++) {
		if (chart.deleted_columns.at(c)) {
			continue;
		}

		int index{-1};
		for (size_t r{0}; r < chart.rows; r++) {
			if (chart.deleted_rows.at(r)) {
				continue;
			}

			if (chart.chart.at(r * chart.columns + c)) {
				if (index != -1) {
					break;
				}
				index = r;
			}
		}

		if (index != -1) {
			return index;
		}
	}

	return -1;
}

#if defined(CF_LOGGING) && CF_LOGGING == 1
static void print_chart(const prime_implicant_chart& chart) {
	assert(chart.chart.size() == chart.rows * chart.columns);
	assert(chart.deleted_rows.size() == chart.rows);
	assert(chart.deleted_columns.size() == chart.columns);

	std::clog << "Chart:" << std::endl;
	for (size_t i{0}; i < chart.rows; i++) {
		if (chart.deleted_rows.at(i)) {
			continue;
		}
		for (size_t j{0}; j < chart.columns; j++) {
			if (chart.deleted_columns.at(j)) {
				continue;
			}
			if (!chart.chart.at(i * chart.columns + j)) {
				std::clog << "0";
			} else {
				std::clog << "1";
			}
		}
		std::clog << std::endl;
	}
}
#else
void print_chart(const prime_implicant_chart&) {}
#endif	// CF_LOGGING

static std::vector<size_t> find_essential_prime_implicants(prime_implicant_chart& chart) {
	assert(chart.chart.size() == chart.rows * chart.columns);
	assert(chart.deleted_rows.size() == chart.rows);
	assert(chart.deleted_columns.size() == chart.columns);
	assert(std::all_of(chart.deleted_rows.begin(), chart.deleted_rows.end(), [](const bool x) {
		return !x;
	}));
	assert(
		std::all_of(chart.deleted_columns.begin(), chart.deleted_columns.end(), [](const bool x) {
		return !x;
	}));

#if defined(CF_LOGGING) && CF_LOGGING == 1
	print_chart(chart);
#endif	// CF_LOGGING

	remove_dominated_rows(chart);

#if defined(CF_LOGGING) && CF_LOGGING == 1
	print_chart(chart);
#endif	// CF_LOGGING

	std::vector<size_t> result;
	int epi_idx = find_first_essential_prime_implicant(chart);

	while (epi_idx != -1) {
		const size_t epi = static_cast<size_t>(epi_idx);
		result.push_back(epi);
		cf::utils::debug("Found essential prime implicant: row " + std::to_string(epi));

		chart.deleted_rows.at(epi) = true;
		for (size_t c{0}; c < chart.columns; c++) {
			if (chart.deleted_columns.at(c)) {
				continue;
			}
			if (chart.chart.at(epi * chart.columns + c)) {
				chart.deleted_columns.at(c) = true;
			}
		}

#if defined(CF_LOGGING) && CF_LOGGING == 1
		print_chart(chart);
#endif	// CF_LOGGING

		remove_dominated_rows(chart);

#if defined(CF_LOGGING) && CF_LOGGING == 1
		print_chart(chart);
#endif	// CF_LOGGING

		epi_idx = find_first_essential_prime_implicant(chart);
	}

	assert(std::all_of(chart.deleted_rows.begin(), chart.deleted_rows.end(), [](const bool x) {
		return x;
	}));
	assert(
		std::all_of(chart.deleted_columns.begin(), chart.deleted_columns.end(), [](const bool x) {
		return x;
	}));

	return result;
}

/**
 * Quine - McCluskey algorithm.
 *
 * Quine, W. V. (1952). The Problem of Simplifying Truth Functions. The American Mathematical
 * Monthly, 59(8), 521–531. https://doi.org/10.1080/00029890.1952.11988183
 *
 * Quine, W. V. (1955). A Way to Simplify Truth Functions. The American Mathematical Monthly,
 * 62(9), 627–631. https://doi.org/10.1080/00029890.1955.11988710
 *
 * E. J. McCluskey, "Minimization of Boolean functions," in The Bell System Technical Journal,
 * vol. 35, no. 6, pp. 1417-1444, Nov. 1956, doi: 10.1002/j.1538-7305.1956.tb03835.x.
 *
 */
template <typename T>
std::vector<cf::input<T>> qmc(const std::vector<cf::input<T>>& ones, const size_t nbits) {
	assert(nbits > 0 && nbits <= 8 * sizeof(T));
	assert(ones.size() <= (1u << nbits));

	// Check that there are no duplicate entries
	for (size_t i{0}; i < ones.size(); i++) {
		for (size_t j{i + 1}; j < ones.size(); j++) {
			assert(ones.at(i) != ones.at(j));
		}
	}

	std::vector<cf::input<T>> base = ones;
	std::vector<cf::input<T>> next;
	std::vector<cf::input<T>> result;

	for (size_t it{0}; it <= nbits; it++) {
		if (base.size() == 0) {
			break;
		}

		const size_t length = base.size();

		cf::utils::debug("Computing size-" + std::to_string(1 << it) + " prime implicants.");
		cf::utils::debug("Initial size: " + std::to_string(length));

		std::vector<bool> used(length);

		for (size_t i{0}; i < length; i++) {
			const cf::input<T> first = base.at(i);
			for (size_t j{i + 1}; j < length; j++) {
				const cf::input<T> second = base.at(j);

				if (first.mask != second.mask) {
					continue;
				}

				const T combined = (first.value & first.mask) ^ (second.value & second.mask);
				const T bits = cf::utils::popcount(combined);
				assert(bits != 0);
				if (bits == 1) {
					// if there is only 1 set bit, one variable may be omitted
					used.at(i) = true;
					used.at(j) = true;
					const cf::input<T> res{static_cast<T>(first.value & (~combined)),
										   static_cast<T>(first.mask & (~combined))};
					next.push_back(res);
				}
				/*
				else if(bits == 2) {
					// if there are 2 set bits, we can write a XOR
				}
				*/
			}
		}

		for (size_t i{0}; i < length; i++) {
			if (used.at(i)) {
				continue;
			}

			// This implicant was not used to compute the "next size" implicants.
			const cf::input<T> to_be_added = base.at(i);
			result.push_back(to_be_added);
			cf::utils::debug("The value " + cf::utils::get_bit_string(to_be_added) +
							 " was not used");
		}

		cf::utils::debug("Next size: " + std::to_string(next.size()));
		cf::utils::debug("Result size: " + std::to_string(result.size()));

		// Removing duplicates from 'next'
		std::unordered_set<cf::input<T>> s;
		for (const auto x : next) {
			s.insert(x);
		}
		base.clear();
		for (const auto x : s) {
			base.push_back(x);
		}
		next.clear();
	}

	// building the prime implicant chart
	prime_implicant_chart chart{
		result.size(), ones.size(), std::vector<bool>(result.size() * ones.size(), false),
		std::vector<bool>(result.size(), false), std::vector<bool>(ones.size(), false)};
	for (size_t i{0}; i < result.size(); i++) {
		const T vi = result.at(i).value;
		const T mi = result.at(i).mask;
		for (size_t j{0}; j < ones.size(); j++) {
			const size_t idx = i * ones.size() + j;
			const bool val =
				// checking that the 1s are in the right place
				((vi & mi) & (ones.at(j).value & ones.at(j).mask)) == vi &&
				// checking that the 0s are in the right place
				(~(~vi & mi & ~(ones.at(j).value & ones.at(j).mask)) & mi) == vi;
			chart.chart.at(idx) = val;
		}
	}

	std::vector<size_t> epi_indices = find_essential_prime_implicants(chart);
	std::vector<cf::input<T>> out;
	out.reserve(epi_indices.size());
	for (const size_t idx : epi_indices) {
		out.push_back(result.at(idx));
	}
	return out;
}

}  // namespace minimize
}  // namespace cf
