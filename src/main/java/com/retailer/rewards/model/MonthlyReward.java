package com.retailer.rewards.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds the reward points a customer earned in a specific month.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReward {

    private int year;
    private String month;       // e.g. "JANUARY"
    private int monthNumber;    // 1-12, useful for sorting
    private long points;
}
