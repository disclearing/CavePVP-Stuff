package net.frozenorb.foxtrot.team;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FilterType {
    LOWEST_DTR(1, "Teams with the least amount of DTR"),
    HIGHEST_DTR(2, "Teams with the most DTR"),
    HIGH_MEMBERS(3, "Teams with the most members online"),
    LOW_MEMBERS(4, "Teams with the least amount of members online"),
    KILLS(5, "Teams with the most kills"),
    DEATHS(6, "Teams with the most deaths");

    @Getter int number;
    @Getter String description;

}
