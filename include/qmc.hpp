#pragma once

#include <vector>
#include <cassert>
#include <algorithm>
#include <iostream>

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

void remove_dominated_rows(prime_implicant_chart& chart) {
	for (size_t i{0}; i < chart.rows; i++) {
		if (chart.deleted_rows[i]) {
			continue;
		}

		for (size_t j{i + 1}; j < chart.rows; j++) {
			if (chart.deleted_rows[j]) {
				continue;
			}

			bool i_dominates_j = true;
			bool j_dominates_i = true;
			for (size_t k{0}; k < chart.columns; k++) {
				if (chart.deleted_columns[k]) {
					continue;
				}
				if (chart.chart[i * chart.columns + k] && !chart.chart[j * chart.columns + k]) {
					j_dominates_i = false;
				}
				if (chart.chart[j * chart.columns + k] && !chart.chart[i * chart.columns + k]) {
					i_dominates_j = false;
				}
				if (!i_dominates_j && !j_dominates_i) {
					break;
				}
			}

			if (i_dominates_j && j_dominates_i) {
				// the rows i and j, were equal, so we delete only j
				chart.deleted_rows[j] = true;
				std::cout << "Deleted row " << j << ": dominated by row " << i << std::endl;
			} else if (i_dominates_j) {
				// i dominates j, we delete j
				chart.deleted_rows[j] = true;
				std::cout << "Deleted row " << j << ": dominated by row " << i << std::endl;
			} else if (j_dominates_i) {
				// j dominates i, we delete i
				chart.deleted_rows[i] = true;
				std::cout << "Deleted row " << i << ": dominated by row " << j << std::endl;
			}
		}
	}
}

int find_first_essential_prime_implicant(const prime_implicant_chart& chart) {
	const size_t bits_to_find{1};
	for (size_t r{0}; r < chart.rows; r++) {
		if (chart.deleted_rows[r]) {
			continue;
		}

		for (size_t c{0}; c < chart.columns; c++) {
			if (chart.deleted_columns[c]) {
				continue;
			}

			size_t count{0};
			for (size_t i{0}; i < chart.rows; i++) {
				if (chart.deleted_rows[i]) {
					continue;
				}

				if (chart.chart[i * chart.columns + c]) {
					count++;
				}
			}

			if (count == bits_to_find) {
				return r;
			}
		}
	}

	return -1;
}

std::vector<size_t> find_essential_prime_implicants(prime_implicant_chart& chart) {
	remove_dominated_rows(chart);

	std::vector<size_t> result;
	int epi_idx = find_first_essential_prime_implicant(chart);

	while (epi_idx != -1) {
		const size_t epi = static_cast<size_t>(epi_idx);
		result.push_back(epi);
		std::cout << "Found essential prime implicant: row " << epi << std::endl;

		chart.deleted_rows[epi] = true;
		for (size_t c{0}; c < chart.columns; c++) {
			if (chart.chart[epi * chart.columns + c]) {
				chart.deleted_columns[c] = true;
			}
		}

		remove_dominated_rows(chart);

		epi_idx = find_first_essential_prime_implicant(chart);
	}

	return result;
}

/**
 * Quine - McCluskey algorithm.
 *
 * Quine, W. V. (1952). The Problem of Simplifying Truth Functions. The American Mathematical
 * Monthly, 59(8), 521–531. https://doi.org/10.1080/00029890.1952.11988183
 *
 * Quine, W. V. (1955). A Way to Simplify Truth Functions. The American Mathematical Monthly, 62(9),
 * 627–631. https://doi.org/10.1080/00029890.1955.11988710
 *
 * E. J. McCluskey, "Minimization of Boolean functions," in The Bell System Technical Journal, vol.
 * 35, no. 6, pp. 1417-1444, Nov. 1956, doi: 10.1002/j.1538-7305.1956.tb03835.x.
 *
 */
template <typename T>
std::vector<cf::input<T>> qmc(const std::vector<cf::input<T>>& ones, const size_t nbits) {
	assert(nbits > 0 && nbits <= 8 * sizeof(T));

	std::vector<cf::input<T>> base = ones;
	std::vector<cf::input<T>> next;
	std::vector<cf::input<T>> result;

	for (size_t it{0}; it <= nbits; it++) {
		if (base.size() == 0) {
			break;
		}

		const size_t length = base.size();

		std::cout << "Computing size-" << (1 << it) << " prime implicants." << std::endl;
		std::cout << "Initial size: " << length << std::endl;

		std::vector<bool> used(length);

		for (size_t i{0}; i < length; i++) {
			const cf::input<T> first = base.at(i);
			for (size_t j{i + 1}; j < length; j++) {
				const cf::input<T> second = base.at(j);

				if (first.mask != second.mask) {
					continue;
				}

				const T combined = (first.value & first.mask) ^ (second.value & second.mask);
				const T bits = __builtin_popcount(combined);
				assert(bits != 0);
				if (bits == 1) {
					// if there is only 1 set bit, one variable may be omitted
					used[i] = true;
					used[j] = true;
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
			if (used[i]) {
				continue;
			}

			// This implicant was not used to compute the "next size" implicants.
			const cf::input<T> to_be_added = base.at(i);
			result.push_back(to_be_added);
			std::cout << "The value " << cf::utils::get_bit_string(to_be_added) << " was not used"
					  << std::endl;
		}

		std::cout << "Next size: " << next.size() << std::endl;
		std::cout << "Result size: " << result.size() << std::endl;

		// Removing duplicates from 'next'
		std::sort(next.begin(), next.end());
		next.erase(std::unique(next.begin(), next.end()), next.end());
		base = next;
		next = std::vector<cf::input<T>>();
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
	for (const size_t idx : epi_indices) {
		out.push_back(result.at(idx));
	}
	return out;
}

}  // namespace minimize
}  // namespace cf
