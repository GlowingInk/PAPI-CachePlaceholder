# CachePlaceholder
A simple placeholder expansion that caches the result of other placeholders for desired period.

Might be useful when you have some resource-intensive placeholders that are called frequently, but it's not that 
important to keep those up-to-date.
## Usage
The format is simple: `%cache_{seconds}_{another_placeholder}%`

If given seconds are equals to 0 or lower, the placeholder will return latest cached value.
Cached results are cleared once player leaves the server.

Example:
* `%statistic_time_played%` with 10 seconds delay: `%cache_10_statistic_time_played%`
* `%vault_eco_balance%` cache just only once: `%cache_0_vault_eco_balance%`